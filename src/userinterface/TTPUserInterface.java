package userinterface;

import network.Request;
import network.Response;
import ttp.RegisteredNodeInformation;
import ttp.SystemParameters;
import ttp.TTP;
import userinterface.packets.requests.ExperimentRequest;
import userinterface.packets.requests.InitializeRequest;
import userinterface.packets.requests.TerminationRequest;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TTPUserInterface extends UserInterface {

    private final TTP ttp;

    public TTPUserInterface(Scanner scanner, TTP ttp) {
        super(scanner);
        this.ttp = ttp;
        setUnderlay(ttp);
    }

    @Override
    protected void initialize() {
        // No initialization is necessary.
    }

    @Override
    protected String menu() {
        return "== TTP/Controller Menu ==\n" +
                "1. Show registered nodes\n" +
                "2. Broadcast initialize requests\n" +
                "3. Start experiments\n" +
                "4. Terminate the skip-graph\n";
    }

    @Override
    public boolean handleUserInput(int input) {
        List<String> addresses = ttp.getRegisteredNodes().stream().map(x -> x.address).collect(Collectors.toList());
        switch (input) {
            case 1:
                for (RegisteredNodeInformation node : ttp.getRegisteredNodes()) {
                    System.out.println(node);
                }
                break;
            case 2:
                for (String address : addresses) {
                    Response r = send(address, new InitializeRequest());
                    if (r.isError()) {
                        System.err.println("Error during join broadcast: " + r.errorMessage);
                    }
                }
                break;
            case 3:
                boolean concurrent = promptBoolean("Concurrent");
                if(concurrent) {
                    sendConcurrentSearchRequests(addresses);
                } else {
                    // Run the experiments sequentially, i.e. node by node.
                    SystemParameters systemParameters = ttp.getSystemParameters();
                    // Form the experiment request that should be sent to every node.
                    ExperimentRequest experimentRequest = new ExperimentRequest(systemParameters.ROUND_COUNT,
                            0, 0, systemParameters.SYSTEM_CAPACITY);
                    for(String address : addresses) {
                        Response r = send(address, experimentRequest);
                        if(r.isError()) {
                            System.err.println("Error during experiment: " + r.errorMessage);
                        }
                    }
                }
                break;
            case 4:
                // Broadcast the termination requests to every node.
                for(String address : addresses) {
                    Response r = send(address, new TerminationRequest());
                    if(r.isError()) {
                        System.err.println("Error during termination: " + r.errorMessage);
                    }
                }
                // Terminate the TTP.
                logger.close();
                terminate();
                return false;
        }
        return true;
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        return null;
    }

    public void sendConcurrentSearchRequests(List<String> addresses) {
        SystemParameters systemParameters = ttp.getSystemParameters();
        // Form the experiment request that should be concurrently sent to every node.
        ExperimentRequest experimentRequest = new ExperimentRequest(systemParameters.ROUND_COUNT,
                systemParameters.WAIT_TIME, 0, systemParameters.SYSTEM_CAPACITY);
        Thread[] threads = new Thread[addresses.size()];
        for(int i = 0; i < threads.length; i++) {
            String address = addresses.get(i);
            // In the thread, send the experiment request to the node and wait for the experiment to complete.
            threads[i] = new Thread(() -> {
                Response r = send(address, experimentRequest);
                if(r.isError()) {
                    System.err.println("Error during experiment: " + r.errorMessage);
                }
            });
            threads[i].start();
        }
        // Collect the results.
        for(Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.err.println("Could not complete the threads.");
                e.printStackTrace();
            }
        }

    }
}

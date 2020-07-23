package userinterface;

import misc.Logger;
import network.Request;
import network.Response;
import ttp.RegisteredNodeInformation;
import ttp.SystemParameters;
import ttp.TTP;
import userinterface.packets.requests.ExperimentRequest;
import userinterface.packets.requests.InitializeRequest;
import userinterface.packets.requests.TerminationRequest;
import userinterface.workers.RetrieveFileWorker;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                "4. Collect data from the nodes\n" +
                "5. Terminate the skip-graph\n";
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
                            0, 0, systemParameters.SYSTEM_CAPACITY-1);
                    for(String address : addresses) {
                        Response r = send(address, experimentRequest);
                        if(r.isError()) {
                            System.err.println("Error during experiment: " + r.errorMessage);
                        }
                    }
                }
                break;
            case 4:
                Thread[] threads = new Thread[addresses.size()];
                // Receive the individual log files from the nodes concurrently.
                for(int i = 0; i < addresses.size(); i++) {
                    String filePath = "received_logs/" + addresses.get(i).replace(':', '_') + ".csv";
                    threads[i] = new Thread(new RetrieveFileWorker(this, filePath, addresses.get(i)));
                }
                for(Thread t : threads) t.start();
                for(Thread t : threads) {
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        System.err.println("Could not join the thread.");
                        e.printStackTrace();
                    }
                }
                // Merge them into a single file.
                System.out.println("Collected the logs. Now merging...");
                try(Stream<Path> paths = Files.walk(Paths.get("received_logs/"))) {
                    List<BufferedReader> fileReaders = paths.filter(Files::isRegularFile)
                            .map(Path::toFile)
                            .map(x -> {
                                try {
                                    return new BufferedReader(new FileReader(x));
                                } catch (FileNotFoundException e) {
                                    System.err.println("Error while opening the file.");
                                    e.printStackTrace();
                                }
                                return null;
                            })
                            .collect(Collectors.toList());
                    // Collect all the log entries, sorted by time.
                    List<LogEntry> logEntries = fileReaders.stream()
                            .flatMap(BufferedReader::lines) // Expand as lines.
                            .filter(line -> !line.startsWith("msg_id")) // Skip the field headers line.
                            .map(line -> {
                                // Map a single line of a csv file to a log entry.
                                String[] fields = line.split(";");
                                long time = 0;
                                try {
                                    time = Long.parseLong(fields[7]);
                                } catch(Exception e) {
                                    System.err.println("Could not parse time at line: " + line);
                                    return null;
                                }
                                return new LogEntry(time, line);
                            })
                            .filter(Objects::nonNull) // Filter out the problematic lines.
                            .sorted(Comparator.comparingLong(x -> x.time)) // Sort by the time.
                            .collect(Collectors.toList()); // Collect as list.
                    // Write the logs to a single file utilizing the logger class.
                    Logger mergedLogger = new Logger("merged", "received_logs");
                    logEntries.forEach(x -> mergedLogger.writeLogToFile(x.entry));
                    // Close the logger.
                    mergedLogger.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 5:
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
                systemParameters.WAIT_TIME, 0, systemParameters.SYSTEM_CAPACITY-1);
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

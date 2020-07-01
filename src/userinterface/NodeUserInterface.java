package userinterface;

import authentication.packets.requests.AuthSearchByNumIDRequest;
import authentication.packets.requests.NodeAssignRequest;
import authentication.packets.requests.NodeConstructRequest;
import authentication.packets.requests.NodeRegisterRequest;
import misc.GlobalRand;
import network.Request;
import network.Response;
import network.packets.responses.AckResponse;
import skipnode.SkipNode;
import skipnode.packets.requests.GetInfoRequest;
import skipnode.packets.requests.InsertRequest;
import skipnode.packets.requests.SearchByNumIDRequest;
import skipnode.packets.responses.NodeInfoResponse;
import skipnode.packets.responses.SearchResultResponse;
import ttp.SystemParameters;
import userinterface.packets.requests.ExperimentRequest;
import userinterface.packets.requests.SearchRequest;

import java.util.Scanner;

public class NodeUserInterface extends UserInterface {

    private final SkipNode authNode;

    public NodeUserInterface(Scanner scanner, SkipNode authNode) {
        super(scanner);
        setUnderlay(authNode);
        this.authNode = authNode;
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        return switch(request.type) {
            case JOIN -> join();
            case SEARCH -> search((SearchRequest) request);
            case INITIALIZE -> initializeGuard();
            case EXPERIMENT -> experiment((ExperimentRequest) request);
            case TERMINATE -> terminateNode();
            default -> null;
        };
    }

    @Override
    public void initialize() {
        join();
    }

    @Override
    public String menu() {
        return "== Node Menu ==\n" +
                "1. Info \n" +
                "2. Initialize\n" +
                "3. Search\n" +
                "4. Start experiments\n" +
                "5. Terminate\n";
    }

    @Override
    public boolean handleUserInput(int input) {
        switch (input) {
            case 1: {
                // Send node info request to this node.
                Response r = send(getAddress(), new GetInfoRequest());
                if (r.isError()) {
                    System.err.println("Received error: " + r.errorMessage);
                } else {
                    // Output the received response.
                    System.out.println(((NodeInfoResponse) r).nodeInfo);
                }
                break;
            }
            case 2: {
                initializeGuard();
                break;
            }
            case 3: {
                int target = promptInteger("Enter target numerical ID");
                boolean auth = promptBoolean("Authenticated");
                search(new SearchRequest(target, auth));
                break;
            }
            case 4: {
                SystemParameters systemParameters = authNode.getSystemParameters();
                experiment(new ExperimentRequest(systemParameters.ROUND_COUNT, systemParameters.WAIT_TIME,
                        0, systemParameters.SYSTEM_CAPACITY));
            }
            break;
            case 5:
                terminateNode();
                return false;
        }
        return true;
    }

    public AckResponse initializeGuard() {
        System.out.println("Construction initiated...");
        // Send construction request to this node.
        Response r = send(getAddress(), new NodeConstructRequest());
        if(r.isError()) {
            System.err.println("Error during construction: " + r.errorMessage);
            return new AckResponse(r.errorMessage);
        }
        System.out.println("Guard assignment initiated...");
        // Send guard assignment request to this node.
        r = send(getAddress(), new NodeAssignRequest());
        if(r.isError()) {
            System.err.println("Error during guard assignment: " + r.errorMessage);
            return new AckResponse(r.errorMessage);
        }
        System.out.println("Successfully joined!");
        return new AckResponse(null);
    }

    public AckResponse experiment(ExperimentRequest request) {
        System.out.println("Initiated experiments...");
        for(int i = 0; i < request.rounds; i++) {
            System.out.println("** Experiment round: " + (i + 1) + "/" + request.rounds);
            // Find a random target.
            int target = request.minNumID + GlobalRand.rand.nextInt(request.minNumID + request.maxNumID);
            // Perform an unauthenticated search on the target.
            SearchResultResponse unauthResult = search(new SearchRequest(target, false));
            if(unauthResult.isError()) {
                System.err.println("Error during unauth. search: " + unauthResult.errorMessage);
                return new AckResponse(unauthResult.errorMessage);
            } else if(unauthResult.result.getNumID() != target) {
                System.err.println("Wrong result received in unauth. search. " +
                        "Expected: " + target + ", but received: " + unauthResult.result.getNumID());
            }
            // Perform an authenticated search on the target.
            SearchResultResponse authResult = search(new SearchRequest(target, true));
            if(authResult.isError()) {
                System.err.println("Error during auth. search: " + authResult.errorMessage);
                return new AckResponse(authResult.errorMessage);
            } else if(unauthResult.result.getNumID() != target) {
                System.err.println("Wrong result received in auth. search. " +
                        "Expected: " + target + ", but received: " + authResult.result.getNumID());
            }
            if(request.maxWaitTime == 0) continue;
            // Choose a random wait time.
            int waitTime = GlobalRand.rand.nextInt(request.maxWaitTime);
            try {
                Thread.sleep(waitTime * 1000);
            } catch (InterruptedException e) {
                System.err.println("Could not wait.");
                e.printStackTrace();
            }
        }
        return new AckResponse(null);
    }

    public AckResponse join() {
        // First, register the node.
        System.out.println("Registering with TTP...");
        Response r = send(getAddress(), new NodeRegisterRequest());
        if(r.isError()) {
            System.err.println("Error while registering: " + r.errorMessage);
            return new AckResponse(r.errorMessage);
        }
        // Output the registration response.
        System.out.println(r);
        // Then, insert the node.
        System.out.println("Self insertion initiated...");
        send(getAddress(), new InsertRequest());
        if(r.isError()) {
            System.err.println("Error while inserting: " + r.errorMessage);
            return new AckResponse(r.errorMessage);
        }
        return new AckResponse(null);
    }

    public SearchResultResponse search(SearchRequest request) {
        System.out.println("Initiated search for " + request.target + ". Auth: " + request.auth);
        Response r = send(getAddress(), (request.auth) ? new AuthSearchByNumIDRequest(request.target) : new SearchByNumIDRequest(request.target));
        if(r.isError()) {
            System.err.println("Error during search: " + r.errorMessage);
            return new SearchResultResponse(null, r.errorMessage);
        }
        // Output the search result response.
        SearchResultResponse resResponse = (SearchResultResponse) r;
        System.out.println(resResponse);
        return resResponse;
    }

    public AckResponse terminateNode() {
        System.out.println("Requested termination...");
        // Close the logger.
        logger.close();
        // Terminate the layers.
        terminate();
        return new AckResponse(null);
    }
}

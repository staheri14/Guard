package userinterface;

import authentication.packets.requests.AuthSearchByNumIDRequest;
import authentication.packets.requests.NodeAssignRequest;
import authentication.packets.requests.NodeConstructRequest;
import authentication.packets.requests.NodeRegisterRequest;
import protocol.Request;
import protocol.Response;
import protocol.packets.responses.AckResponse;
import skipnode.SkipNode;
import skipnode.packets.requests.GetInfoRequest;
import skipnode.packets.requests.InsertRequest;
import skipnode.packets.requests.SearchByNumIDRequest;
import skipnode.packets.responses.NodeInfoResponse;
import skipnode.packets.responses.SearchResultResponse;
import userinterface.packets.requests.SearchRequest;

import java.util.Scanner;

public class NodeUserInterface extends UserInterface {

    public NodeUserInterface(Scanner scanner, SkipNode authNode) {
        super(scanner);
        setUnderlay(authNode);
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        return switch(request.type) {
            case JOIN -> join();
            case SEARCH -> search((SearchRequest) request);
            default -> null;
        };
    }

    @Override
    public void initialize() {
        // First, register the node.
        System.out.println("Registering with TTP...");
        Response r = send(getAddress(), new NodeRegisterRequest());
        if(r.isError()) {
            System.err.println("Error while registering: " + r.errorMessage);
            return;
        }
        // Output the registration response.
        System.out.println(r);
        // Then, insert the node.
        System.out.println("Self insertion initiated...");
        send(getAddress(), new InsertRequest());
        if(r.isError()) {
            System.err.println("Error while inserting: " + r.errorMessage);
        }
    }

    @Override
    public String menu() {
        return "== Node Menu ==\n" +
                "1. Info \n" +
                "2. Join\n" +
                "3. Search\n" +
                "4. Terminate\n";
    }

    @Override
    public boolean handleUserInput(int input) {
        if(input == 1) {
            // Send node info request to this node.
            Response r = send(getAddress(), new GetInfoRequest());
            if(r.isError()) {
                System.err.println("Received error: " + r.errorMessage);
            } else {
                // Output the received response.
                System.out.println(((NodeInfoResponse) r).nodeInfo);
            }
        } else if(input == 2) {
            AckResponse r = join();
            if(r.isError()) {
                System.err.println("Error during join: " + r.errorMessage);
                return false;
            }
        } else if(input == 3) {
            int target = promptInteger("Enter target numerical ID");
            boolean auth = promptBoolean("Authenticated");
            AckResponse r = search(new SearchRequest(target, auth));
            if(r.isError()) {
                System.err.println("Error during search: " + r.errorMessage);
                return false;
            }

        } else if(input == 4) return false;
        return true;
    }

    public AckResponse join() {
        System.out.println("Construction initiated...");
        // Send construction request to this node.
        Response r = send(getAddress(), new NodeConstructRequest());
        if(r.isError()) {
            return new AckResponse(r.errorMessage);
        }
        System.out.println("Guard assignment initiated...");
        // Send guard assignment request to this node.
        r = send(getAddress(), new NodeAssignRequest());
        if(r.isError()) {
            return new AckResponse(r.errorMessage);
        }
        System.out.println("Successfully joined!");
        return new AckResponse(null);
    }

    public AckResponse search(SearchRequest request) {
        System.out.println("Initiated search for " + request.target + ". Auth: " + request.auth);
        Response r = send(getAddress(), (request.auth) ? new AuthSearchByNumIDRequest(request.target) : new SearchByNumIDRequest(request.target));
        if(r.isError()) {
            return new AckResponse(r.errorMessage);
        }
        // Output the search result response.
        SearchResultResponse resResponse = (SearchResultResponse) r;
        System.out.println(resResponse);
        return new AckResponse(null);
    }
}

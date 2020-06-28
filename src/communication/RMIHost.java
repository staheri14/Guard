package communication;

import network.Layer;
import network.Request;
import network.Response;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIHost extends UnicastRemoteObject implements RMIService {

    private final Layer overlay;
    private final String hostAddress;

    public RMIHost(Layer overlay, String hostAddress) throws RemoteException {
        this.overlay = overlay;
        this.hostAddress = hostAddress;
    }

    /**
     * Handles the incoming request by dispatching it to the upper layer.
     * @param request the incoming request.
     * @return the response emitted by the Underlay.
     */
    @Override
    public Response sendRequest(Request request) {
        // Receive the response from the upper layer.
        Response response = overlay.receive(request);
        if(response.isError()) {
            System.err.println("[UnderlayHost] " + hostAddress + " has emitted: " + response.errorMessage);
        }
        // Piggyback the addresses to the response.
        response.senderAddress = hostAddress;
        response.destinationAddress = request.senderAddress;
        return response;
    }

}

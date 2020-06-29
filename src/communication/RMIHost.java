package communication;

import network.Layer;
import network.Request;
import network.Response;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Represents an implementation of the Java RMI service for a process. The responsibility of this
 * implementation to simply receive requests from remote clients and send it to the upper layers.
 */
public class RMIHost extends UnicastRemoteObject implements RMIService {

    // The layer that the received requests will be sent to.
    private final Layer overlay;
    // The address of the process that this service was constructed on.
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

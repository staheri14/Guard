package communication;

import network.Layer;
import network.Request;
import network.Response;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Handles node to node communication with Java RMI.
 */
public class Communication extends Layer {

    private RMIHost host;
    private final String address;
    private final int port;

    public Communication(int port) {
        this.port = port;
        String ipv4 = "";
        try {
            ipv4 = Inet4Address.getLocalHost().getHostAddress();
        } catch(UnknownHostException e) {
            System.err.println("[Middleware] Could not acquire the local host name during construction.");
            e.printStackTrace();
        }
        address = ipv4 + ":" + port;
    }

    /**
     * Returns the address of the middleware.
     * @return the address of the middleware.
     */
    @Override
    public String getAddress() {
        return address;
    }

    /**
     * Connects to the Middleware of a remote server.
     * @param address address of the server in the form of IP:PORT
     * @return a remote Java RMI adapter.
     */
    private RMIService remote(String address) {
        if(host == null) {
            System.err.println("[Middleware] Host does not exist.");
            return null;
        }
        RMIService remote;
        try {
            remote = (RMIService) Naming.lookup("//" + address + "/authentication");
        } catch (Exception e) {
            System.err.println("[Middleware] Could not connect to the remote RMI server.");
            return null;
        }
        return remote;
    }

    /**
     * Initializes the middleware at the given port, and sets the overlay of the middleware.
     * @param overlay the overlay of this middleware.
     * @return whether the initialization was successful or not.
     */
    public boolean initializeHost(Layer overlay) {
        setOverlay(overlay);
        try {
            host = new RMIHost(overlay, address);
            LocateRegistry.createRegistry(port).bind("authentication", host);
        } catch (Exception e) {
            System.err.println("[Middleware] Could not bind.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Sends a request to the node with the given address.
     * @param destinationAddress the address of the remote node.
     * @param request the request that should be sent.
     * @return the response emitted by the remote node.
     */
    @Override
    public Response handleSentRequest(String destinationAddress, Request request) {
        // Set the request's address fields.
        request.senderAddress = address;
        request.destinationAddress = destinationAddress;
        Response response;
        // If the destination address is this node, simply send the request back up.
        if(destinationAddress.equals(address)) {
            response = receive(request);
        } else {
            // Connect to the remote node.
            RMIService remote = remote(destinationAddress);
            if (remote == null) {
                return null;
            }
            try {
                response = remote.sendRequest(request);
            } catch (RemoteException e) {
                System.err.println("[Middleware] Could not send the request.");
                e.printStackTrace();
                return null;
            }
        }
        // Error check.
        if(response.isError()) {
            System.err.println("[Middleware] " + address + " has received: " + response.errorMessage);
        }
        // Return the response.
        return response;
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        // Middleware does not receive requests from the lower layer, since it is the lowermost layer.
        return null;
    }

    @Override
    public boolean terminateLayer() {
        try {
            Naming.unbind("//" + address + "/authentication");
        } catch (Exception e) {
            System.err.println("[Middleware] Could not terminate.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
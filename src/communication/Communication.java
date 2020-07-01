package communication;

import misc.GlobalRand;
import misc.Logger;
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
    private final String fullAddress;
    private final int port;

    public Communication(int port) {
        this.port = port;
        String ipv4 = "";
        try {
            ipv4 = Inet4Address.getLocalHost().getHostAddress();
        } catch(UnknownHostException e) {
            System.err.println("[Communication] Could not acquire the local host name during construction.");
            e.printStackTrace();
        }
        fullAddress = ipv4 + ":" + port;
    }

    /**
     * Returns the address of the comm. layer.
     * @return the address of the comm. layer.
     */
    @Override
    public String getAddress() {
        return fullAddress;
    }

    /**
     * Connects to the Communication of a remote server.
     * @param address address of the server in the form of IP:PORT
     * @return a remote Java RMI adapter.
     */
    private RMIService remote(String address) {
        if(host == null) {
            System.err.println("[Communication] Host does not exist.");
            return null;
        }
        RMIService remote;
        try {
            remote = (RMIService) Naming.lookup("//" + address + "/node");
        } catch (Exception e) {
            System.err.println("[Communication] Could not connect to the remote RMI server.");
            return null;
        }
        return remote;
    }

    /**
     * Initializes the comm. layer at the given port, and sets the overlay of the comm. layer.
     * @param overlay the overlay of this comm. layer.
     * @return whether the initialization was successful or not.
     */
    public boolean initializeHost(Layer overlay) {
        setOverlay(overlay);
        try {
            host = new RMIHost(overlay, fullAddress);
            LocateRegistry.createRegistry(port).bind("node", host);
        } catch (Exception e) {
            System.err.println("[Communication] Could not bind.");
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
        request.sourceAddress = fullAddress;
        request.destinationAddress = destinationAddress;
        // Assign a random ID for the request.
        request.id = Integer.toUnsignedLong(GlobalRand.rand.nextInt());
        // Log the sending of request event.
        logger.logRequestSent(request);
        // Reserve the space for the response.
        Response response;
        // If the destination address is this node, simply send the request back up.
        if(destinationAddress.equals(fullAddress)) {
            response = receive(request);
            // Fill in the response fields.
            response.destinationAddress = fullAddress;
            response.sourceAddress = fullAddress;
            response.auth = request.auth;
            response.phase = request.phase;
        } else {
            // Connect to the remote node.
            RMIService remote = remote(destinationAddress);
            if (remote == null) {
                return null;
            }
            try {
                response = remote.sendRequest(request);
            } catch (RemoteException e) {
                System.err.println("[Communication] Could not send the request.");
                e.printStackTrace();
                return null;
            }
        }
        // Log the receipt of response event.
        logger.logResponseReceived(response, request);
        // Error check.
        if(response.isError()) {
            System.err.println("[Communication] " + fullAddress + " has received: " + response.errorMessage);
        }
        // Return the response.
        return response;
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        // Communication layer does not receive requests from the lower layer, since it is the lowermost layer.
        return null;
    }

    @Override
    public void setLogger(Logger logger) {
        super.setLogger(logger);
        host.setLogger(logger);
    }

    @Override
    public boolean terminateLayer() {
        // Try to terminate the parent layer.
        if(!super.terminateLayer()) return false;
        // Terminate the communication layer.
        try {
            LocateRegistry.getRegistry(port).unbind("node");
        } catch (Exception e) {
            System.err.println("[Communication] Could not terminate.");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

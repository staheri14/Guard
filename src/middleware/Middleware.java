package middleware;

import protocol.Layer;
import protocol.Request;
import protocol.Response;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Handles node to node communication with Java RMI.
 */
public class Middleware extends Layer {

    private MiddlewareHost host;
    private final String address;
    private final int port;

    public Middleware(int port) {
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
    private MiddlewareService remote(String address) {
        if(host == null) {
            System.err.println("[Middleware] Host does not exist.");
            return null;
        }
        MiddlewareService remote;
        try {
            remote = (MiddlewareService) Naming.lookup("//" + address + "/node");
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
            host = new MiddlewareHost(overlay, address);
            LocateRegistry.createRegistry(port).bind("node", host);
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
        request.destinationAddress = address;
        // Connect to the remote node.
        MiddlewareService remote = remote(destinationAddress);
        if(remote == null) {
            return null;
        }
        // Send the request and acquire the response.
        Response response;
        try {
            response = remote.sendRequest(request);
        } catch (RemoteException e) {
            System.err.println("[Middleware] Could not send the request.");
            e.printStackTrace();
            return null;
        }
        if(response.isError()) {
            System.err.println("[Middleware] " + address + " has received: " + response.errorMessage);
        }
        // Return the response.
        return response;
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        return null;
    }

    @Override
    public boolean terminateLayer() {
        try {
            Naming.unbind("//" + address + "/node");
        } catch (Exception e) {
            System.err.println("[Middleware] Could not terminate.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

}

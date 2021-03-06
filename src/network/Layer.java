package network;

import misc.Logger;

/**
 * Represents a network layer.
 */
public abstract class Layer {

    // The upper layer. Should be null if the layer is the uppermost layer.
    protected Layer overlay;
    // The lower layer. Should be null if the layer is the lowermost layer.
    protected Layer underlay;
    // Used to log the events happening at the layer. If not set, then no logging happens.
    protected Logger logger = new Logger();

    /**
     * Sets the logger to be used by this layer.
     * @param logger the logger to use.
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Layer getUnderlay() {
        return underlay;
    }

    public void setOverlay(Layer overlay) {
        this.overlay = overlay;
    }

    public void setUnderlay(Layer underlay) {
        this.underlay = underlay;
    }

    /**
     * Dispatches the request to the upper layer until a response is generated.
     * @param request request to be dispatched.
     */
    public final Response receive(Request request) {
        // Handle the request.
        Response r = handleReceivedRequest(request);
        // If this layer has generated a response, send the response back down.
        if(r != null) {
            return r;
        } else if(overlay != null) {
            // Or, if there is an overlay, send the request up.
            return overlay.receive(request);
        } else {
            // Otherwise, simply send back an error.
            return new Response("invalid request, could not dispatch up");
        }
    }

    /**
     * Sends the request to the lower layers until a middleware is reached and a response is generated.
     * @param destinationAddress the address of the remote server.
     * @param request the request to be handled.
     * @return response generated by the remote server.
     */
    public final Response send(String destinationAddress, Request request) {
        // Handle the request.
        Response r = handleSentRequest(destinationAddress, request);
        // If this layer has generated a response, send the response back up.
        if(r != null) {
            return r;
        } else if(underlay != null) {
            // Or, if there is an underlay, keep sending the request down.
            return underlay.send(destinationAddress, request);
        } else {
            // Otherwise, simply send back an error.
            return new Response("could not dispatch down");
        }
    }

    public String getAddress() {
        if(underlay != null) {
            return underlay.getAddress();
        }
        return null;
    }

    /**
     * Called when a request from a lower layer is received.
     * @param request request from the lower layer.
     * @return an emitted response or null to delegate it to the upper layer.
     */
    public abstract Response handleReceivedRequest(Request request);

    /**
     * Called when a request from an upper layer is received.
     * @param request the request.
     * @return an emitted response or null to delegate it to the lower layer.
     */
    public Response handleSentRequest(String destinationAddress, Request request) {
        return null;
    }

    public boolean terminateLayer() {
        return true;
    }

    /**
     * Terminates this layer and all the lower layers.
     * @return whether the termination was successful.
     */
    public final boolean terminate() {
        boolean l = terminateLayer();
        if(l && underlay != null) {
            return underlay.terminate();
        }
        return l;
    }
}

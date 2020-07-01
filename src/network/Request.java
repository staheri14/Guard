package network;

import misc.Logger;

import java.io.Serializable;

/**
 * Represents a request.
 */
public class Request implements Serializable {

    // Type of the request.
    public final RequestType type;
    // The phase in which the request is emitted at.
    public Logger.Phase phase = Logger.Phase.UNKNOWN;
    // Whether the request is handled by the authentication layer.
    public boolean auth = false;
    // The address of the client that the request was emitted from.
    public String sourceAddress;
    // The address of the client that the request is sent to.
    public String destinationAddress;
    // Unique id of the request.
    public long id;

    public Request(RequestType type) {
        this.type = type;
    }
}

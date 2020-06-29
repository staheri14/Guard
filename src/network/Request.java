package network;

import java.io.Serializable;

/**
 * Represents a request.
 */
public class Request implements Serializable {

    // Type of the request.
    public final RequestType type;
    // The address of the client that the request was emitted from.
    public String senderAddress;
    // The address of the client that the request is sent to.
    public String destinationAddress;

    public Request(RequestType type) {
        this.type = type;
    }
}

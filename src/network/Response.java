package network;

import misc.Logger;

import java.io.Serializable;

/**
 * Represents a response emitted by a process after processing a request.
 */
public class Response implements Serializable {

    // The client that the response is emitted from.
    public String sourceAddress;
    // The client that the response should be sent back to.
    public String destinationAddress;
    // The phase in which the response is emitted at.
    public Logger.Phase phase = Logger.Phase.UNKNOWN;
    // Whether the response is handled by the authentication layer.
    public boolean auth = false;
    // The error produced by the process. Should be `null` if the client has successfully
    // processed the request.
    public final String errorMessage;

    // Unique id of the response.
    public long id;

    public Response(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns whether the response denotes an error.
     * @return whether the response denotes an error.
     */
    public boolean isError() {
        return errorMessage != null;
    }
}

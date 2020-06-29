package network;

import java.io.Serializable;

/**
 * Represents a response emitted by a process after processing a request.
 */
public class Response implements Serializable {

    // The client that the response is emitted from.
    public String senderAddress;
    // The client that the response should be sent back to.
    public String destinationAddress;

    // The error produced by the process. Should be `null` if the client has successfully
    // processed the request.
    public final String errorMessage;

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

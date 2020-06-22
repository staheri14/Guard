package protocol;

import java.io.Serializable;

public class Response implements Serializable {

    public String senderAddress;
    public String destinationAddress;

    public final String errorMessage;

    public Response(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isError() {
        return errorMessage != null;
    }
}

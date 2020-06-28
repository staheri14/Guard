package network;

import java.io.Serializable;

public class Request implements Serializable {

    public final RequestType type;
    public String senderAddress;
    public String destinationAddress;

    public Request(RequestType type) {
        this.type = type;
    }
}

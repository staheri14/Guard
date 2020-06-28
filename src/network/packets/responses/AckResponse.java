package network.packets.responses;

import network.Response;

public class AckResponse extends Response {

    public AckResponse(String errorMessage) {
        super(errorMessage);
    }
}

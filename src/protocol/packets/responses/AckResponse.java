package protocol.packets.responses;

import protocol.Response;

public class AckResponse extends Response {

    public AckResponse(String errorMessage) {
        super(errorMessage);
    }
}

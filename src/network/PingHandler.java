package network;

import communication.Communication;
import network.packets.requests.PingRequest;
import network.packets.responses.AckResponse;

public class PingHandler extends Layer {

    public PingHandler(Communication communication) {
        setUnderlay(communication);
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        PingRequest pingRequest = (PingRequest) request;
        if(pingRequest.emitError) return new AckResponse("some error");
        return new AckResponse(null);
    }

}

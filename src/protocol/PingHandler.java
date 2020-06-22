package protocol;

import middleware.Middleware;
import protocol.packets.requests.PingRequest;
import protocol.packets.responses.AckResponse;

public class PingHandler extends Layer {

    public PingHandler(Middleware middleware) {
        setUnderlay(middleware);
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        PingRequest pingRequest = (PingRequest) request;
        if(pingRequest.emitError) return new AckResponse("some error");
        return new AckResponse(null);
    }

}

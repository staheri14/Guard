package network;

import communication.Communication;
import network.packets.requests.PingRequest;
import network.packets.responses.AckResponse;

/**
 * A layer that simply responds with an acknowledgement response to requests of type `PingRequest`.
 * Used for testing the connectivity of the communication layers.
 */
public class PingHandler extends Layer {

    public PingHandler(Communication communication) {
        setUnderlay(communication);
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        PingRequest pingRequest = (PingRequest) request;
        // Emit an error if the request demands it so.
        if(pingRequest.emitError) return new AckResponse("some error");
        return new AckResponse(null);
    }

}

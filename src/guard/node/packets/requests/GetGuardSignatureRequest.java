package guard.node.packets.requests;

import guard.node.RoutingTranscript;
import protocol.Request;
import protocol.RequestType;

public class GetGuardSignatureRequest extends Request {

    public final RoutingTranscript routingTranscript;

    public GetGuardSignatureRequest(RoutingTranscript routingTranscript) {
        super(RequestType.GET_GUARD_SIGNATURE);
        this.routingTranscript = routingTranscript;
    }
}

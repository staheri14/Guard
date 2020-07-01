package authentication.packets.requests;

import authentication.RoutingTranscript;
import misc.Logger;
import network.Request;
import network.RequestType;

public class GetGuardSignatureRequest extends Request {

    public final RoutingTranscript routingTranscript;

    public GetGuardSignatureRequest(RoutingTranscript routingTranscript) {
        super(RequestType.GET_GUARD_SIGNATURE);
        this.routingTranscript = routingTranscript;
        this.auth = true;
        this.phase = Logger.Phase.SEARCH;
    }
}

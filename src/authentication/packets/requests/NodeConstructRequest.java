package authentication.packets.requests;

import protocol.Request;
import protocol.RequestType;

public class NodeConstructRequest extends Request {

    public NodeConstructRequest() {
        super(RequestType.NODE_CONSTRUCT);
    }
}

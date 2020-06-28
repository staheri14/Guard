package authentication.packets.requests;

import network.Request;
import network.RequestType;

public class NodeConstructRequest extends Request {

    public NodeConstructRequest() {
        super(RequestType.NODE_CONSTRUCT);
    }
}

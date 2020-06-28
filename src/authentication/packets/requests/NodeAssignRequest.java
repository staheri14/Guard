package authentication.packets.requests;

import network.Request;
import network.RequestType;

public class NodeAssignRequest extends Request {

    public NodeAssignRequest() {
        super(RequestType.NODE_ASSIGN);
    }
}

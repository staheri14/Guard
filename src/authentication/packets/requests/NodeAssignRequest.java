package authentication.packets.requests;

import protocol.Request;
import protocol.RequestType;

public class NodeAssignRequest extends Request {

    public NodeAssignRequest() {
        super(RequestType.NODE_ASSIGN);
    }
}

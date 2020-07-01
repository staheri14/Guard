package authentication.packets.requests;

import misc.Logger;
import network.Request;
import network.RequestType;

public class NodeAssignRequest extends Request {

    public NodeAssignRequest() {
        super(RequestType.NODE_ASSIGN);
        this.auth = true;
        this.phase = Logger.Phase.GUARD_ASSIGNMENT;
    }
}

package authentication.packets.requests;

import misc.Logger;
import network.Request;
import network.RequestType;

public class NodeConstructRequest extends Request {

    public NodeConstructRequest() {
        super(RequestType.NODE_CONSTRUCT);
        this.auth = true;
        this.phase = Logger.Phase.CONSTRUCTION;
    }
}

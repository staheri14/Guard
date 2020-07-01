package authentication.packets.requests;

import misc.Logger;
import network.Request;
import network.RequestType;

public class NodeRegisterRequest extends Request {

    public NodeRegisterRequest() {
        super(RequestType.NODE_REGISTER);
        this.auth = true;
        this.phase = Logger.Phase.REGISTRATION;
    }
}

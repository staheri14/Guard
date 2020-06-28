package authentication.packets.requests;

import network.Request;
import network.RequestType;

public class NodeRegisterRequest extends Request {

    public NodeRegisterRequest() {
        super(RequestType.NODE_REGISTER);
    }
}

package authentication.packets.requests;

import protocol.Request;
import protocol.RequestType;

public class NodeRegisterRequest extends Request {

    public NodeRegisterRequest() {
        super(RequestType.NODE_REGISTER);
    }
}

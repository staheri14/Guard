package userinterface.packets.requests;

import protocol.Request;
import protocol.RequestType;

public class JoinRequest extends Request {

    public JoinRequest() {
        super(RequestType.JOIN);
    }

}

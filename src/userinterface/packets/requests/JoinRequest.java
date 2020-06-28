package userinterface.packets.requests;

import network.Request;
import network.RequestType;

public class JoinRequest extends Request {

    public JoinRequest() {
        super(RequestType.JOIN);
    }

}

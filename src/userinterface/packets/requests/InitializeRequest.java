package userinterface.packets.requests;

import network.Request;
import network.RequestType;

public class InitializeRequest extends Request {

    public InitializeRequest() {
        super(RequestType.INITIALIZE);
    }
}

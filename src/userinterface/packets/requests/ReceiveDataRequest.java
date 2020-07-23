package userinterface.packets.requests;

import network.Request;
import network.RequestType;

public class ReceiveDataRequest extends Request {

    public ReceiveDataRequest() {
        super(RequestType.RECEIVE_DATA);
    }

}

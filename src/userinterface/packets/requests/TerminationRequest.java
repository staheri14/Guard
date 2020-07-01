package userinterface.packets.requests;

import network.Request;
import network.RequestType;

public class TerminationRequest extends Request {

    public TerminationRequest() {
        super(RequestType.TERMINATE);
    }

}

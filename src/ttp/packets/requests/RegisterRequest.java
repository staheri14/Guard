package ttp.packets.requests;

import network.Request;
import network.RequestType;

public class RegisterRequest extends Request {

    public RegisterRequest() {
        super(RequestType.TTP_REGISTER);
    }
}

package ttp.packets.requests;

import misc.Logger;
import network.Request;
import network.RequestType;

public class RegisterRequest extends Request {

    public RegisterRequest() {
        super(RequestType.TTP_REGISTER);
        this.auth = true;
        this.phase = Logger.Phase.REGISTRATION;
    }
}

package guard.ttp.packets.requests;

import protocol.Request;
import protocol.RequestType;

public class RegisterRequest extends Request {

    public RegisterRequest() {
        super(RequestType.TTP_REGISTER);
    }
}

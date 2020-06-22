package guard.ttp.packets.requests;

import protocol.Request;
import protocol.RequestType;

public class AuthChallengeRequest extends Request {

    public AuthChallengeRequest() {
        super(RequestType.TTP_AUTH_CHALLENGE);
    }

}

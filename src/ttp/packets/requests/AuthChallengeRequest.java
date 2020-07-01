package ttp.packets.requests;

import misc.Logger;
import network.Request;
import network.RequestType;

public class AuthChallengeRequest extends Request {

    public AuthChallengeRequest() {
        super(RequestType.TTP_AUTH_CHALLENGE);
        this.auth = true;
        this.phase = Logger.Phase.GUARD_ASSIGNMENT;
    }

}

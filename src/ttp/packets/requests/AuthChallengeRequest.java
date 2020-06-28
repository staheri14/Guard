package ttp.packets.requests;

import network.Request;
import network.RequestType;

public class AuthChallengeRequest extends Request {

    public AuthChallengeRequest() {
        super(RequestType.TTP_AUTH_CHALLENGE);
    }

}

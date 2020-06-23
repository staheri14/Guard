package ttp.packets.responses;

import protocol.Response;

public class AuthChallengeResponse extends Response {

    public final String challenge;

    public AuthChallengeResponse(String challenge, String errorMessage) {
        super(errorMessage);
        this.challenge = challenge;
    }

}

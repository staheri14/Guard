package authentication.workers;

import crypto.memento.SignatureShareMemento;
import authentication.Authentication;
import authentication.RoutingTranscript;
import authentication.packets.requests.GetGuardSignatureRequest;
import authentication.packets.responses.PartialSignatureResponse;
import network.Response;

public class GuardSignWorker implements Runnable {

    private final String guardAddress;
    private final RoutingTranscript transcript;
    private final Authentication authentication;

    public SignatureShareMemento signatureShare;
    public String errorMsg;

    public GuardSignWorker(String guardAddress, RoutingTranscript transcript, Authentication authentication) {
        this.guardAddress = guardAddress;
        this.transcript = transcript;
        this.authentication = authentication;
    }

    @Override
    public void run() {
        Response r = authentication.send(guardAddress, new GetGuardSignatureRequest(transcript));
        if(r.isError()) {
            errorMsg = r.errorMessage;
        } else {
            signatureShare = ((PartialSignatureResponse) r).partialSignature;
        }
    }
}

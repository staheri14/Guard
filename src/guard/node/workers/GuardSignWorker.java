package guard.node.workers;

import crypto.memento.SignatureShareMemento;
import guard.node.Authentication;
import guard.node.RoutingTranscript;
import guard.node.packets.requests.GetGuardSignatureRequest;
import guard.node.packets.responses.PartialSignatureResponse;
import protocol.Response;

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
        if(r == null) {
            errorMsg = "could not connect to the guard";
        } else if(r.isError()) {
            errorMsg = r.errorMessage;
        } else {
            signatureShare = ((PartialSignatureResponse) r).partialSignature;
        }
    }
}

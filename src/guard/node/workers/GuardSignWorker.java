package guard.node.workers;

import crypto.memento.SignatureShareMemento;
import guard.node.AuthNode;
import guard.node.RoutingTranscript;
import guard.node.packets.requests.GetGuardSignatureRequest;
import guard.node.packets.responses.PartialSignatureResponse;
import protocol.Response;

public class GuardSignWorker implements Runnable {

    private final String guardAddress;
    private final RoutingTranscript transcript;
    private final AuthNode authNode;

    public SignatureShareMemento signatureShare;
    public String errorMsg;

    public GuardSignWorker(String guardAddress, RoutingTranscript transcript, AuthNode authNode) {
        this.guardAddress = guardAddress;
        this.transcript = transcript;
        this.authNode = authNode;
    }

    @Override
    public void run() {
        Response r = authNode.send(guardAddress, new GetGuardSignatureRequest(transcript));
        if(r == null) {
            errorMsg = "could not connect to the guard";
        } else if(r.isError()) {
            errorMsg = r.errorMessage;
        } else {
            signatureShare = ((PartialSignatureResponse) r).partialSignature;
        }
    }
}

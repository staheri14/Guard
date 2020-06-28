package authentication.packets.responses;

import crypto.memento.SignatureShareMemento;
import network.Response;

public class PartialSignatureResponse extends Response {

    public final SignatureShareMemento partialSignature;

    public PartialSignatureResponse(SignatureShareMemento partialSignature, String errorMessage) {
        super(errorMessage);
        this.partialSignature = partialSignature;
    }
}

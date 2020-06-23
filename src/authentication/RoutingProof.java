package authentication;

import crypto.memento.SignatureMemento;

import java.io.Serializable;

/**
 * Used to transfer routing proofs between nodes.
 */
public class RoutingProof implements Serializable {

    public final RoutingTranscript transcript;
    public final SignatureMemento selfSignature;
    public final SignatureMemento guardSignature;

    public RoutingProof(RoutingTranscript transcript, SignatureMemento selfSignature, SignatureMemento guardSignature) {
        this.transcript = transcript;
        this.selfSignature = selfSignature;
        this.guardSignature = guardSignature;
    }
}

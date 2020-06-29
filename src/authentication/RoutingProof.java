package authentication;

import crypto.memento.SignatureMemento;

import java.io.Serializable;

/**
 * Used to transfer routing proofs between nodes.
 */
public class RoutingProof implements Serializable {

    public final RoutingTranscript transcript;
    // Signature of the transcript by the generator of the transcript with the private signature key that
    // it has acquired at the registration phase.
    public final SignatureMemento selfSignature;
    // Signature of the transcript by the guards of the generator of the transcript. This signature is
    // constructed by receiving 3 valid partial signatures from the guards.
    public final SignatureMemento guardSignature;

    public RoutingProof(RoutingTranscript transcript, SignatureMemento selfSignature, SignatureMemento guardSignature) {
        this.transcript = transcript;
        this.selfSignature = selfSignature;
        this.guardSignature = guardSignature;
    }
}

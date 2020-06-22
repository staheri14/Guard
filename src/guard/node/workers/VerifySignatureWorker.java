package guard.node.workers;

import crypto.PublicParameters;
import crypto.Scheme;
import crypto.Signature;
import crypto.memento.SignatureMemento;
import it.unisa.dia.gas.jpbc.Pairing;

public class VerifySignatureWorker implements Runnable {

    public boolean valid = false;

    private final SignatureMemento signature;
    private final PublicParameters publicParameters;
    private final Pairing pairing;
    private final String identity;
    private final String message;

    public VerifySignatureWorker(SignatureMemento signature, PublicParameters publicParameters, Pairing pairing,
                                 String identity, String message) {
        this.signature = signature;
        this.publicParameters = publicParameters;
        this.pairing = pairing;
        this.identity = identity;
        this.message = message;
    }

    @Override
    public void run() {
        Signature sgn = signature.reconstruct(publicParameters);
        valid = Scheme.VerifyGlobal(publicParameters, pairing, identity.length(), message.length(),
                identity, message, sgn);
    }
}

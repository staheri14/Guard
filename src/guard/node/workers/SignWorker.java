package guard.node.workers;

import crypto.PrivateKey;
import crypto.PublicParameters;
import crypto.Signature;
import crypto.threshold.ThresholdScheme;

public class SignWorker implements Runnable {

    private final String message;
    private final PrivateKey signatureKey;
    private final PublicParameters publicParameters;

    public Signature signature;

    public SignWorker(String message, PrivateKey signatureKey, PublicParameters publicParameters) {
        this.message = message;
        this.signatureKey = signatureKey;
        this.publicParameters = publicParameters;
    }

    @Override
    public void run() {
        signature = ThresholdScheme.SignGlobal(message, signatureKey, publicParameters);
    }
}

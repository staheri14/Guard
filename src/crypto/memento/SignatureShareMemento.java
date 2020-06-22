package crypto.memento;

import crypto.PublicParameters;
import crypto.threshold.SignatureShare;
import it.unisa.dia.gas.jpbc.Element;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Used to transfer signature shares (i.e. partial signatures) between nodes.
 */
public class SignatureShareMemento implements Serializable {

    private byte[] firstBytes;
    private byte[] secondBytes;
    private BigInteger r_k;

    public SignatureShareMemento(SignatureShare signatureShare) {
        firstBytes = signatureShare.getFirst().toBytes();
        secondBytes = signatureShare.getSecond().toBytes();
        r_k = signatureShare.getR_k();
    }

    public SignatureShare reconstruct(PublicParameters publicParameters) {
        Element first = publicParameters.G.newElementFromBytes(firstBytes);
        Element second = publicParameters.G.newElementFromBytes(secondBytes);
        return new SignatureShare(r_k, first, second);
    }
}

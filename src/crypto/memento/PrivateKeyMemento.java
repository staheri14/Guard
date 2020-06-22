package crypto.memento;

import crypto.PrivateKey;
import crypto.PublicParameters;
import it.unisa.dia.gas.jpbc.Element;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Used to transfer private keys between nodes.
 */
public class PrivateKeyMemento implements Serializable {

    byte[] secondBytes;
    byte[] firstBytes;
    BigInteger r_u;

    public PrivateKeyMemento(PrivateKey key) {
        firstBytes = key.getFirst().toBytes();
        secondBytes = key.getSecond().toBytes();
        r_u = key.getR_u();
    }

    // Reconstructs the private key from the public G field.
    public PrivateKey reconstruct(PublicParameters publicParameters) {
        Element first = publicParameters.G.newElementFromBytes(firstBytes);
        Element second = publicParameters.G.newElementFromBytes(secondBytes);
        return new PrivateKey(first, second, r_u);
    }
}

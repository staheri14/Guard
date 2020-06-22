package crypto.threshold;

import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;
import java.util.Arrays;

public class DistributedKeys {

    private BigInteger[] privateKeys;
    private Element[] verificationKeys;
    private Element[] Y;

    public DistributedKeys(Element[] Y, BigInteger[] privateKeys, Element[] verificationKeys) {
        // Make sure that the elements are immutable.
        this.Y = Arrays.stream(Y)
                .map(Element::getImmutable)
                .toArray(Element[]::new);
        this.privateKeys = privateKeys;
        // Make sure that the elements are immutable.
        this.verificationKeys = Arrays.stream(verificationKeys)
                .map(Element::getImmutable)
                .toArray(Element[]::new);
    }

    public BigInteger getPrivateKey(int server) {
        return this.privateKeys[server-1];
    }

    public Element getVerificationKey(int server) {
        return this.verificationKeys[server-1];
    }

    public Element[] getY() {
        return Y;
    }
}

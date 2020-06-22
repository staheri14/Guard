package crypto.memento;

import crypto.PublicParameters;
import crypto.Signature;
import it.unisa.dia.gas.jpbc.Element;

import java.io.Serializable;

/**
 * Used to transfer signatures between nodes.
 */
public class SignatureMemento implements Serializable {

    byte[] firstBytes;
    byte[] secondBytes;
    byte[] thirdBytes;

    public SignatureMemento(Signature signature) {
        firstBytes = signature.getFirst().toBytes();
        secondBytes = signature.getSecond().toBytes();
        thirdBytes = signature.getThird().toBytes();
    }

    public Signature reconstruct(PublicParameters publicParameters) {
        Element first = publicParameters.G.newElementFromBytes(firstBytes);
        Element second = publicParameters.G.newElementFromBytes(secondBytes);
        Element third = publicParameters.G.newElementFromBytes(thirdBytes);
        return new Signature(first, second, third);
    }
}

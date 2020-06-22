package crypto.memento;

import crypto.PublicParameters;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.Serializable;

/**
 * Used to transfer public scheme parameters between nodes.
 */
public class PublicParametersMemento implements Serializable {

    PairingParameters pairingParameters;
    byte[] gBytes;
    byte[] g1Bytes;
    byte[] g2Bytes;
    byte[] mPrimeBytes;
    byte[] uPrimeBytes;
    byte[][] MBytes;
    byte[][] UBytes;

    public PublicParametersMemento(PairingParameters pairingParameters, PublicParameters publicParameters) {
        this.pairingParameters = pairingParameters;
        gBytes = publicParameters.g.toBytes();
        g1Bytes = publicParameters.g1.toBytes();
        g2Bytes = publicParameters.g2.toBytes();
        mPrimeBytes = publicParameters.mPrime.toBytes();
        uPrimeBytes = publicParameters.uPrime.toBytes();
        MBytes = new byte[publicParameters.M.length][];
        for(int i = 0; i < publicParameters.M.length; i++) {
            MBytes[i] = publicParameters.M[i].toBytes();
        }
        UBytes = new byte[publicParameters.U.length][];
        for(int i = 0; i < publicParameters.U.length; i++) {
            UBytes[i] = publicParameters.U[i].toBytes();
        }
    }

    public PublicParameters reconstruct() {
        PublicParameters reconstructed = new PublicParameters();
        Pairing pairing = PairingFactory.getPairing(pairingParameters);
        reconstructed.G = pairing.getG1();
        reconstructed.GT = pairing.getGT();
        reconstructed.g = reconstructed.G.newElementFromBytes(gBytes).getImmutable();
        reconstructed.g1 = reconstructed.G.newElementFromBytes(g1Bytes).getImmutable();
        reconstructed.g2 = reconstructed.G.newElementFromBytes(g2Bytes).getImmutable();
        reconstructed.mPrime = reconstructed.G.newElementFromBytes(mPrimeBytes).getImmutable();
        reconstructed.uPrime = reconstructed.G.newElementFromBytes(uPrimeBytes).getImmutable();
        reconstructed.U = new Element[UBytes.length];
        for(int i = 0; i < UBytes.length; i++) {
            reconstructed.U[i] = reconstructed.G.newElementFromBytes(UBytes[i]).getImmutable();
        }
        reconstructed.M = new Element[MBytes.length];
        for(int i = 0; i < MBytes.length; i++) {
            reconstructed.M[i] = reconstructed.G.newElementFromBytes(MBytes[i]).getImmutable();
        }
        return reconstructed;
    }

    public Pairing reconstructPairing() {
        return  PairingFactory.getPairing(pairingParameters);
    }
}

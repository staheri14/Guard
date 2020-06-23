package authentication;

import crypto.PublicParameters;
import crypto.Signature;
import crypto.memento.SignatureMemento;

import java.io.Serializable;

public class TableProof implements Serializable {

    private final SignatureMemento[][] signatures;
    public final int ownerNumID;

    public TableProof(int maxLevels, int ownerNumID) {
        signatures = new SignatureMemento[maxLevels][2];
        this.ownerNumID = ownerNumID;
    }

    public void insertProof(int level, int position, SignatureMemento signature) {
        signatures[level][position] = signature;
    }

    public Signature getSignature(int level, int position, PublicParameters publicParameters) {
        SignatureMemento sgnMemento = signatures[level][position];
        if(sgnMemento == null) {
            return null;
        }
        return sgnMemento.reconstruct(publicParameters);
    }
}

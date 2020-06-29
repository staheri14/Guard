package authentication;

import crypto.PublicParameters;
import crypto.Signature;
import crypto.memento.SignatureMemento;

import java.io.Serializable;

/**
 * Represents the table proof of a node. Can be sent accross the wire.
 */
public class TableProof implements Serializable {

    private final SignatureMemento[][] signatures;
    public final int ownerNumID;

    public TableProof(int maxLevels, int ownerNumID) {
        signatures = new SignatureMemento[maxLevels][2];
        this.ownerNumID = ownerNumID;
    }

    /**
     * Inserts a table proof entry at the given level and position
     * @param level the level.
     * @param position the position. 0 for left, 1 for right.
     * @param signature the signature received from the neighbor.
     */
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

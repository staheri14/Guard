package ttp;

import crypto.PrivateKey;
import crypto.threshold.DistributedKeys;
import authentication.CircularLookupTable;
import authentication.TableProof;
import skipnode.LookupTable;

public class RegisteredNodeInformation {

    public final String address;
    public final int numID;
    public final String nameID;
    public final PrivateKey signatureKey;

    public LookupTable lookupTable;
    public CircularLookupTable circularLookupTable;
    public TableProof tableProof;
    public String[] guardNameIDs;
    public DistributedKeys guardKeys;

    public RegisteredNodeInformation(String address, int numID, String nameID, PrivateKey signatureKey) {
        this.address = address;
        this.numID = numID;
        this.nameID = nameID;
        this.signatureKey = signatureKey;
    }
}

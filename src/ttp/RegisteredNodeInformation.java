package ttp;

import crypto.PrivateKey;
import crypto.threshold.DistributedKeys;
import authentication.CircularLookupTable;
import authentication.TableProof;
import skipnode.LookupTable;

/**
 * Represents a node that is registered with the TTP.
 */
public class RegisteredNodeInformation {

    // Address of the registered node.
    public final String address;
    // Assigned numerical ID of the registered node.
    public final int numID;
    // Assigned name ID of the registered node.
    public final String nameID;
    // Assigned private signature key of the registered node.
    public final PrivateKey signatureKey;

    // Lookup table of the registered node. Received at the guard assignment phase.
    public LookupTable lookupTable;
    // Circular lookup table of the registered node. Received at the guard assignment phase.
    public CircularLookupTable circularLookupTable;
    // Table proof of the registered node. Received at the guard assignment phase.
    public TableProof tableProof;
    // Assigned guards of the registered node. Initialized at the guard assignment phase.
    public String[] guardNameIDs;
    // Assigned guard keys of the registered node. Initialized at the guard assignment phase.
    public DistributedKeys guardKeys;

    public RegisteredNodeInformation(String address, int numID, String nameID, PrivateKey signatureKey) {
        this.address = address;
        this.numID = numID;
        this.nameID = nameID;
        this.signatureKey = signatureKey;
    }

    @Override
    public String toString() {
        return "{Address = " + address + ", " +
                "Num. ID = " + numID + ", " +
                "Name ID = " + nameID + "}";
    }
}

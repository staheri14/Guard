package ttp;

import java.io.Serializable;

/**
 * Represents the system parameters.
 */
public class SystemParameters implements Serializable {

    // Number of nodes in the system.
    public final int SYSTEM_CAPACITY;
    // Identity length for the signature scheme.
    public final int IDENTITY_LENGTH = 256;
    // Message length for the signature scheme.
    // In the context of Guard, this denotes the size of a routing transcript.
    public final int MESSAGE_LENGTH = 300;
    // Parameters for the signature scheme.
    public final int R_BITS = 160;
    public final int Q_BITS = 512;
    // Length of the nonce that is appended to each routing transcript.
    public final int NONCE_LENGTH = 10;

    /** Experiment parameters **/
    public final int ROUND_COUNT = 1000;
    public final int WAIT_TIME = 5;

    /** Authentication parameters **/
    // Whether the piggybacked routing proofs should be verified at the
    // search initiator.
    public final boolean VERIFY_AT_INITIATOR;
    // Whether the routing proof of each node should be verified by the subsequent
    // node at the search path.
    public final boolean VERIFY_AT_ROUTER;
    public final boolean DIRECT_ROUTE_BACK = true;

    public SystemParameters(int systemCapacity, boolean verifyAtInitiator, boolean verifyAtRouter) {
        SYSTEM_CAPACITY = systemCapacity;
        VERIFY_AT_INITIATOR = verifyAtInitiator;
        VERIFY_AT_ROUTER = verifyAtRouter;
    }

    /**
     * Returns length of a name ID from the system capacity.
     * @return the name ID length in the system.
     */
    public int getNameIDLength() {
        return (int)(Math.log(SYSTEM_CAPACITY)/Math.log(2));
    }

    /**
     * Returns the size of the lookup table.
     * @return size of the lookup table.
     */
    public int getMaxLevels() {
        return getNameIDLength();
    }

}

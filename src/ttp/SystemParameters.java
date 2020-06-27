package ttp;

import java.io.Serializable;

public class SystemParameters implements Serializable {

    public final int SYSTEM_CAPACITY;
    public final int IDENTITY_LENGTH = 256;
    public final int MESSAGE_LENGTH = 300;
    public final int R_BITS = 160;
    public final int Q_BITS = 512;
    public final int NONCE_LENGTH = 10;

    public final boolean VERIFY_AT_INITIATOR;
    public final boolean VERIFY_AT_ROUTER;
    public final boolean DIRECT_ROUTE_BACK = true;

    public SystemParameters(int systemCapacity, boolean verifyAtInitiator, boolean verifyAtRouter, boolean automaticInitialization) {
        SYSTEM_CAPACITY = systemCapacity;
        VERIFY_AT_INITIATOR = verifyAtInitiator;
        VERIFY_AT_ROUTER = verifyAtRouter;
    }

    public int getNameIDLength() {
        return (int)(Math.log(SYSTEM_CAPACITY)/Math.log(2));
    }

    public int getMaxLevels() {
        return getNameIDLength();
    }

}

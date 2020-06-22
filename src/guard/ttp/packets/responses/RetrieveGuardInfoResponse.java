package guard.ttp.packets.responses;

import skipnode.LookupTable;
import protocol.Response;

import java.math.BigInteger;

public class RetrieveGuardInfoResponse extends Response {

    public final BigInteger partialSignatureKey;
    public final LookupTable guardedLookupTable;
    public final int guardedNumID;

    public RetrieveGuardInfoResponse(String errorMessage) {
        super(errorMessage);
        partialSignatureKey = null;
        guardedLookupTable = null;
        guardedNumID = -1;
    }

    public RetrieveGuardInfoResponse(BigInteger partialSignatureKey, LookupTable guardedLookupTable, int guardedNumID) {
        super(null);
        this.partialSignatureKey = partialSignatureKey;
        this.guardedLookupTable = guardedLookupTable;
        this.guardedNumID = guardedNumID;
    }
}

package authentication.packets.requests;

import network.Request;
import network.RequestType;

public class GetTableProofEntryRequest extends Request {

    public final int requesterNumID;
    public final int neighborLevel;
    public final int relativePosition;

    public GetTableProofEntryRequest(int requesterNumID, int neighborLevel, int relativePosition) {
        super(RequestType.GET_TABLE_PROOF_ENTRY);
        this.requesterNumID = requesterNumID;
        this.neighborLevel = neighborLevel;
        this.relativePosition = relativePosition;
    }
}

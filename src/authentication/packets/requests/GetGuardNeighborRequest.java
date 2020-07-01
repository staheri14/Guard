package authentication.packets.requests;

import misc.Logger;
import network.Request;
import network.RequestType;

public class GetGuardNeighborRequest extends Request {

    public final int position;

    public GetGuardNeighborRequest(int position) {
        super(RequestType.GET_GUARD_NEIGHBOR);
        this.position = position;
        this.auth = true;
        this.phase = Logger.Phase.CONSTRUCTION;
    }

}

package authentication.packets.requests;

import misc.Logger;
import network.Request;
import network.RequestType;
import skipnode.NodeInfo;

public class SetGuardNeighborRequest extends Request {

    public final int position;
    public final NodeInfo neighbor;

    public SetGuardNeighborRequest(int position, NodeInfo neighbor) {
        super(RequestType.SET_GUARD_NEIGHBOR);
        this.position = position;
        this.neighbor = neighbor;
        this.auth = true;
        this.phase = Logger.Phase.CONSTRUCTION;
    }
}

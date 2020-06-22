package guard.node.packets.requests;

import protocol.Request;
import protocol.RequestType;

public class GetGuardNeighborRequest extends Request {

    public final int position;

    public GetGuardNeighborRequest(int position) {
        super(RequestType.GET_GUARD_NEIGHBOR);
        this.position = position;
    }

}

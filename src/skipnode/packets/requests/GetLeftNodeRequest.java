package skipnode.packets.requests;

import network.Request;
import network.RequestType;

public class GetLeftNodeRequest extends Request {

    public final int level;

    public GetLeftNodeRequest(int level) {
        super(RequestType.GET_LEFT_NODE);
        this.level = level;
    }
}

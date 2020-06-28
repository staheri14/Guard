package skipnode.packets.requests;

import network.Request;
import network.RequestType;
import skipnode.NodeInfo;

public class SetRightNodeRequest extends Request {
    public final int level;
    public final NodeInfo nodeInfo;

    public SetRightNodeRequest(int level, NodeInfo nodeInfo) {
        super(RequestType.SET_RIGHT_NODE);
        this.level = level;
        this.nodeInfo = nodeInfo;
    }
}

package skipnode.packets.requests;

import protocol.Request;
import protocol.RequestType;
import skipnode.NodeInfo;

public class SetLeftNodeRequest extends Request {

    public final int level;
    public final NodeInfo nodeInfo;

    public SetLeftNodeRequest(int level, NodeInfo nodeInfo) {
        super(RequestType.SET_LEFT_NODE);
        this.level = level;
        this.nodeInfo = nodeInfo;
    }

}

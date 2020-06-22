package skipnode.packets.responses;

import protocol.Response;
import skipnode.NodeInfo;

public class NodeInfoResponse extends Response {

    public final NodeInfo nodeInfo;

    public NodeInfoResponse(NodeInfo nodeInfo, String errorMessage) {
        super(errorMessage);
        this.nodeInfo = nodeInfo;
    }
}

package guard.ttp.workers;

import guard.node.packets.requests.NodeAssignRequest;
import guard.ttp.TTP;
import protocol.packets.responses.AckResponse;

public class AssignNodeWorker implements Runnable {

    private final TTP ttp;
    private final String nodeAddress;

    public AckResponse response;

    public AssignNodeWorker(TTP ttp, String nodeAddress) {
        this.ttp = ttp;
        this.nodeAddress = nodeAddress;
    }

    @Override
    public void run() {
        response = (AckResponse) ttp.send(nodeAddress, new NodeAssignRequest());
    }
}

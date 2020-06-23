package guard.ttp.workers;

import guard.node.packets.requests.NodeAssignRequest;
import guard.ttp.TTP;
import protocol.packets.responses.AckResponse;

public class AssignNodeWorker extends PhaseWorker {

    public AssignNodeWorker(TTP ttp, String nodeAddress) {
        super(ttp, nodeAddress);
    }

    @Override
    public void run() {
        response = (AckResponse) ttp.send(nodeAddress, new NodeAssignRequest());
    }
}

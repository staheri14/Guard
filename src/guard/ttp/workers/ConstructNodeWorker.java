package guard.ttp.workers;

import guard.node.packets.requests.NodeConstructRequest;
import guard.ttp.TTP;
import protocol.packets.responses.AckResponse;

public class ConstructNodeWorker extends PhaseWorker {

    public ConstructNodeWorker(TTP ttp, String nodeAddress) {
        super(ttp, nodeAddress);
    }

    @Override
    public void run() {
        response = (AckResponse) ttp.send(nodeAddress, new NodeConstructRequest());
    }
}

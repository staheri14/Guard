package guard.ttp.workers;

import guard.node.packets.requests.NodeConstructRequest;
import guard.ttp.TTP;
import protocol.packets.responses.AckResponse;

public class ConstructNodeWorker implements Runnable {

    private final TTP ttp;
    private final String nodeAddress;

    public AckResponse response;

    public ConstructNodeWorker(TTP ttp, String nodeAddress) {
        this.ttp = ttp;
        this.nodeAddress = nodeAddress;
    }

    @Override
    public void run() {
        response = (AckResponse) ttp.send(nodeAddress, new NodeConstructRequest());
    }
}

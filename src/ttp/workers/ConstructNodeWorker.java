package ttp.workers;

import authentication.packets.requests.NodeConstructRequest;
import ttp.TTP;
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

package ttp.workers;

import ttp.TTP;
import protocol.packets.responses.AckResponse;
import skipnode.packets.requests.InsertRequest;

public class InsertNodeWorker extends PhaseWorker {

    private final String introducerAddress;

    public InsertNodeWorker(TTP ttp, String nodeAddress, String introducerAddress) {
        super(ttp, nodeAddress);
        this.introducerAddress = introducerAddress;
    }

    @Override
    public void run() {
        response = (AckResponse) ttp.send(nodeAddress, new InsertRequest(introducerAddress));
    }
}

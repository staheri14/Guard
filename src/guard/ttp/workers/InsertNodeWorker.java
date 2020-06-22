package guard.ttp.workers;

import guard.ttp.TTP;
import protocol.Response;
import protocol.packets.responses.AckResponse;
import skipnode.packets.requests.InsertRequest;

public class InsertNodeWorker implements Runnable {

    private final TTP ttp;
    private final String nodeAddress;
    private final String introducerAddress;

    public AckResponse response;

    public InsertNodeWorker(TTP ttp, String nodeAddress, String introducerAddress) {
        this.ttp = ttp;
        this.nodeAddress = nodeAddress;
        this.introducerAddress = introducerAddress;
    }

    @Override
    public void run() {
        response = (AckResponse) ttp.send(nodeAddress, new InsertRequest(introducerAddress));
    }
}

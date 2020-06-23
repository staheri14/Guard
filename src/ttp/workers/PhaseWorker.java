package ttp.workers;

import ttp.TTP;
import protocol.packets.responses.AckResponse;

public abstract class PhaseWorker implements Runnable {

    protected final TTP ttp;
    protected final String nodeAddress;

    public AckResponse response;

    public PhaseWorker(TTP ttp, String nodeAddress) {
        this.ttp = ttp;
        this.nodeAddress = nodeAddress;
    }
}

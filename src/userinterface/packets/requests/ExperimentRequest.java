package userinterface.packets.requests;

import network.Request;
import network.RequestType;

public class ExperimentRequest extends Request {

    public final int rounds;
    public final int maxWaitTime;
    public final int maxNumID;
    public final int minNumID;

    public ExperimentRequest(int rounds, int maxWaitTime, int minNumID, int maxNumID) {
        super(RequestType.EXPERIMENT);
        this.rounds = rounds;
        this.maxWaitTime = maxWaitTime;
        this.minNumID = minNumID;
        this.maxNumID = maxNumID;
    }
}

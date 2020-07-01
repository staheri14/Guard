package authentication.packets.requests;

import misc.Logger;
import network.Request;
import network.RequestType;

public class InformGuardRequest extends Request {

    public final int guardIndex;

    public InformGuardRequest(int guardIndex) {
        super(RequestType.INFORM_GUARD);
        this.guardIndex = guardIndex;
        this.auth = true;
        this.phase = Logger.Phase.GUARD_ASSIGNMENT;
    }
}

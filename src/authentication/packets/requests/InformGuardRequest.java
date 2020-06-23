package authentication.packets.requests;

import protocol.Request;
import protocol.RequestType;

public class InformGuardRequest extends Request {

    public final int guardIndex;

    public InformGuardRequest(int guardIndex) {
        super(RequestType.INFORM_GUARD);
        this.guardIndex = guardIndex;
    }
}

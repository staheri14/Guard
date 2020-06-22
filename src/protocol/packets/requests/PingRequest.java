package protocol.packets.requests;

import protocol.Request;
import protocol.RequestType;

public class PingRequest extends Request {

    public final boolean emitError;

    public PingRequest(boolean emitError) {
        super(RequestType.PING);
        this.emitError = emitError;
    }

}

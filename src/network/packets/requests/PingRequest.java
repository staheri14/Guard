package network.packets.requests;

import network.Request;
import network.RequestType;

public class PingRequest extends Request {

    public final boolean emitError;

    public PingRequest(boolean emitError) {
        super(RequestType.PING);
        this.emitError = emitError;
    }

}

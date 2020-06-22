package skipnode.packets.requests;

import protocol.Request;
import protocol.RequestType;

public class GetInfoRequest extends Request {
    public GetInfoRequest() {
        super(RequestType.GET_INFO);
    }
}

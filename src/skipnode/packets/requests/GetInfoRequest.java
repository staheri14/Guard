package skipnode.packets.requests;

import network.Request;
import network.RequestType;

public class GetInfoRequest extends Request {
    public GetInfoRequest() {
        super(RequestType.GET_INFO);
    }
}

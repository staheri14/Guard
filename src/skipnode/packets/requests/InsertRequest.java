package skipnode.packets.requests;

import network.Request;
import network.RequestType;

public class InsertRequest extends Request {

    public InsertRequest() {
        super(RequestType.INSERT);
    }
}

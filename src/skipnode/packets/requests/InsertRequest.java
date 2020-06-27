package skipnode.packets.requests;

import protocol.Request;
import protocol.RequestType;

public class InsertRequest extends Request {

    public InsertRequest() {
        super(RequestType.INSERT);
    }
}

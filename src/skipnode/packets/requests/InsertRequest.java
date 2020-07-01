package skipnode.packets.requests;

import misc.Logger;
import network.Request;
import network.RequestType;

public class InsertRequest extends Request {

    public InsertRequest() {
        super(RequestType.INSERT);
        this.phase = Logger.Phase.CONSTRUCTION;
    }
}

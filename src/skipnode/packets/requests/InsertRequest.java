package skipnode.packets.requests;

import protocol.Request;
import protocol.RequestType;

public class InsertRequest extends Request {

    public final String introducerAddress;

    public InsertRequest(String introducerAddress) {
        super(RequestType.INSERT);
        this.introducerAddress = introducerAddress;
    }
}

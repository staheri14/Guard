package skipnode.packets.requests;

import protocol.Request;
import protocol.RequestType;

public class SearchByNumIDRequest extends Request {

    public final int target;

    public SearchByNumIDRequest(int target) {
        super(RequestType.SEARCH_BY_NUM_ID);
        this.target = target;
    }
}

package userinterface.packets.requests;

import network.Request;
import network.RequestType;

public class SearchRequest extends Request {

    public final int target;
    public final boolean auth;

    public SearchRequest(int target, boolean auth) {
        super(RequestType.SEARCH);
        this.target = target;
        this.auth = auth;
    }
}

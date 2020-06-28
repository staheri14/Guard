package skipnode.packets.responses;

import network.Response;
import skipnode.NodeInfo;

public class SearchResultResponse extends Response {

    public final NodeInfo result;

    public SearchResultResponse(NodeInfo result, String errorMessage) {
        super(errorMessage);
        this.result = result;
    }

    @Override
    public String toString() {
        return "{Result = " + result.toString() + "}";
    }
}

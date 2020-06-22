package skipnode.packets.responses;

import protocol.Response;
import skipnode.NodeInfo;

public class SearchResultResponse extends Response {

    public final NodeInfo result;

    public SearchResultResponse(NodeInfo result, String errorMessage) {
        super(errorMessage);
        this.result = result;
    }
}

package authentication.packets.responses;

import authentication.RoutingProof;
import skipnode.NodeInfo;
import skipnode.packets.responses.SearchResultResponse;

import java.util.List;

public class AuthSearchResultResponse extends SearchResultResponse {

    public final List<RoutingProof> routingProofs;

    public AuthSearchResultResponse(List<RoutingProof> routingProofs, NodeInfo result, String errorMessage) {
        super(result, errorMessage);
        this.routingProofs = routingProofs;
    }
}

package authentication.packets.requests;

import authentication.RoutingProof;
import skipnode.packets.requests.RouteSearchNumIDRequest;

import java.util.LinkedList;

public class AuthRouteSearchNumIDRequest extends RouteSearchNumIDRequest {

    public final LinkedList<RoutingProof> routingProofs;
    public final String nonce;

    public AuthRouteSearchNumIDRequest(LinkedList<RoutingProof> routingProofs, int target, int level, String nonce) {
        super(target, level);
        this.routingProofs = routingProofs;
        this.nonce = nonce;
    }

}

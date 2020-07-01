package misc;

import authentication.Authentication;
import ttp.SystemParameters;
import ttp.TTP;
import skipnode.SkipNode;

/**
 * Represents a local authenticated skip graph. Can be used for testing.
 * Please note that no logging is done in a local system.
 */
public class LocalSystem {

    // The ttp process of the skip graph.
    private final TTP ttp;
    // The authenticated skip-node layers of each process.
    private final SkipNode[] nodes;
    // The authentication layers of each process.
    private final Authentication[] authLayers;

    public LocalSystem(SystemParameters systemParameters, int startingPort) {
        ttp = Builders.buildTTP(systemParameters, startingPort);
        nodes = new SkipNode[systemParameters.SYSTEM_CAPACITY];
        authLayers = new Authentication[systemParameters.SYSTEM_CAPACITY];
        for(int i = 1; i <= systemParameters.SYSTEM_CAPACITY; i++) {
            nodes[i-1] = Builders.buildAuthNode(ttp.getAddress(), startingPort + i);
            authLayers[i-1] = (Authentication) nodes[i-1].getUnderlay();
        }
    }

    public TTP getTTP() {
        return ttp;
    }

    public SkipNode[] getNodes() {
        return nodes;
    }

    public Authentication[] getAuthLayers() {
        return authLayers;
    }

    public void terminate() {
        ttp.terminate();
        for(SkipNode node : nodes) {
            node.terminate();
        }
    }
}

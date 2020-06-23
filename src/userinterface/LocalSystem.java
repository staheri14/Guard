package userinterface;

import guard.node.Authentication;
import guard.ttp.SystemParameters;
import guard.ttp.TTP;
import skipnode.SkipNode;

public class LocalSystem {

    private final TTP ttp;
    private final SkipNode[] nodes;
    private final Authentication[] authLayers;

    public LocalSystem(SystemParameters systemParameters, int startingPort) {
        ttp = Constructors.createTTP(systemParameters, startingPort);
        nodes = new SkipNode[systemParameters.SYSTEM_CAPACITY];
        authLayers = new Authentication[systemParameters.SYSTEM_CAPACITY];
        for(int i = 1; i <= systemParameters.SYSTEM_CAPACITY; i++) {
            nodes[i-1] = Constructors.createAuthNode(ttp.getAddress(), startingPort + i);
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

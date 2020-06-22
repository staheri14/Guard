package userinterface;

import guard.node.AuthNode;
import guard.ttp.SystemParameters;
import guard.ttp.TTP;

public class LocalSystem {

    private final TTP ttp;
    private final AuthNode[] nodes;

    public LocalSystem(SystemParameters systemParameters, int startingPort) {
        ttp = Constructors.createTTP(systemParameters, startingPort);
        nodes = new AuthNode[systemParameters.SYSTEM_CAPACITY];
        for(int i = 1; i <= systemParameters.SYSTEM_CAPACITY; i++) {
            nodes[i-1] = Constructors.createAuthNode(ttp.getAddress(), startingPort + i);
        }
    }

    public TTP getTTP() {
        return ttp;
    }

    public AuthNode[] getNodes() {
        return nodes;
    }

    public void terminate() {
        ttp.terminate();
        for(AuthNode node : nodes) {
            node.terminate();
        }
    }
}

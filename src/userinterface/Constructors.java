package userinterface;

import guard.node.AuthNode;
import guard.ttp.SystemParameters;
import guard.ttp.TTP;
import middleware.Middleware;

public class Constructors {

    public static TTP createTTP(SystemParameters systemParameters, int port) {
        Middleware middleware = new Middleware(port);
        TTP ttp = new TTP(systemParameters, middleware);
        middleware.initializeHost(ttp);
        return ttp;
    }

    public static AuthNode createAuthNode(String ttpAddress, int port) {
        Middleware middleware = new Middleware(port);
        AuthNode authNode = new AuthNode(ttpAddress, middleware.getAddress());
        middleware.initializeHost(authNode);
        authNode.setUnderlay(middleware);
        return authNode;
    }

}

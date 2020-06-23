package userinterface;

import authentication.Authentication;
import ttp.SystemParameters;
import ttp.TTP;
import middleware.Middleware;
import skipnode.SkipNode;

public class Constructors {

    public static TTP createTTP(SystemParameters systemParameters, int port) {
        Middleware middleware = new Middleware(port);
        TTP ttp = new TTP(systemParameters, middleware);
        middleware.initializeHost(ttp);
        return ttp;
    }

    public static SkipNode createAuthNode(String ttpAddress, int port) {
        Middleware middleware = new Middleware(port);
        Authentication authentication = new Authentication(ttpAddress);
        SkipNode skipNode = new SkipNode();
        middleware.initializeHost(authentication);
        authentication.setUnderlay(middleware);
        authentication.setOverlay(skipNode);
        skipNode.setUnderlay(authentication);
        return skipNode;
    }

    public static NodeUserInterface createNodeUserInterface(String ttpAddress, int port) {
        SkipNode authNode = createAuthNode(ttpAddress, port);
        NodeUserInterface userInterface = new NodeUserInterface();
        authNode.setOverlay(userInterface);
        userInterface.setUnderlay(authNode);
        return userInterface;
    }

}

package userinterface;

import authentication.Authentication;
import ttp.SystemParameters;
import ttp.TTP;
import middleware.Middleware;
import skipnode.SkipNode;

import java.util.Scanner;

public class Constructors {

    public static TTP createTTP(SystemParameters systemParameters, int port) {
        Middleware middleware = new Middleware(port);
        TTP ttp = new TTP(systemParameters);
        ttp.setUnderlay(middleware);
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

    public static NodeUserInterface createAuthNodeUserInterface(Scanner scanner, String ttpAddress, int port) {
        SkipNode authNode = createAuthNode(ttpAddress, port);
        NodeUserInterface userInterface = new NodeUserInterface(scanner, authNode);
        authNode.setOverlay(userInterface);
        return userInterface;
    }

    public static TTPUserInterface createTTPUserInterface(Scanner scanner, SystemParameters systemParameters, int port) {
        TTP ttp = createTTP(systemParameters, port);
        TTPUserInterface userInterface = new TTPUserInterface(scanner, ttp);
        ttp.setOverlay(userInterface);
        userInterface.setUnderlay(ttp);
        return userInterface;
    }

}

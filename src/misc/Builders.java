package misc;

import authentication.Authentication;
import ttp.SystemParameters;
import ttp.TTP;
import communication.Communication;
import skipnode.SkipNode;
import userinterface.NodeUserInterface;
import userinterface.TTPUserInterface;

import java.util.Scanner;

/**
 * Contains the static helper methods to build processes with the correct
 * layers interconnected.
 */
public class Builders {

    /**
     * Builds the TTP with the following layers (from bottom to top)
     * (1) Communication
     * (2) TTP
     * @param systemParameters the system parameters that the TTP should be constructed with.
     * @param port the port that the comm. layer should be bound to.
     * @return the TTP ready to be used.
     */
    public static TTP buildTTP(SystemParameters systemParameters, int port) {
        Communication communication = new Communication(port);
        TTP ttp = new TTP(systemParameters);
        ttp.setUnderlay(communication);
        communication.initializeHost(ttp);
        return ttp;
    }

    /**
     * Builds an authenticated node process. This is done by creating (from bottom to top)
     * (1) Communication
     * (2) Authentication
     * (3) SkipNode
     * @param ttpAddress the TTP address for the authentication layer to register with.
     * @param port the port that the comm. layer should be bound to.
     * @return the auth. SkipNode ready to be used.
     */
    public static SkipNode buildAuthNode(String ttpAddress, int port) {
        Communication communication = new Communication(port);
        Authentication authentication = new Authentication(ttpAddress);
        SkipNode skipNode = new SkipNode();
        communication.initializeHost(authentication);
        authentication.setUnderlay(communication);
        authentication.setOverlay(skipNode);
        skipNode.setUnderlay(authentication);
        return skipNode;
    }

    /**
     * Builds an authenticated node user interface process by putting a `NodeUserInterface` layer on top
     * of an auth. node process that is constructed by the `buildAuthNode` builder. Logging is enabled.
     * Resulting network stack is as following (from bottom to top):
     * (1) Communication
     * (2) Authentication
     * (3) SkipNode
     * (4) User interface
     * @param scanner the user input method for the user interface layer.
     * @param ttpAddress the address of the TTP for the authentication layer.
     * @param port the port for the communication layer.
     * @return a ready to use authenticated node user interface.
     */
    public static NodeUserInterface buildAuthNodeUserInterface(Scanner scanner, String ttpAddress, int port) {
        SkipNode authNode = buildAuthNode(ttpAddress, port);
        NodeUserInterface userInterface = new NodeUserInterface(scanner, authNode);
        authNode.setOverlay(userInterface);
        // Initialize the logger and supply it to the layers.
        // Construct the logger.
        Logger logger = new Logger(userInterface.getAddress());
        // Set the logger for the comm. layer.
        authNode.getUnderlay().getUnderlay().setLogger(logger);
        // Set the logger for auth. layer.
        authNode.getUnderlay().setLogger(logger);
        // Set the logger for skip node layer.
        authNode.setLogger(logger);
        // Set the logger for user interface layer.
        userInterface.setLogger(logger);
        return userInterface;
    }

    /**
     * Builds a TTP user interface process by putting a `TTPUserInterface` layer on top of a TTP process that is
     * constructed by the `buildTTP` builder. Logging is enabled.
     * Resulting network stack is as following (from bottom to top):
     * (1) Communication
     * (2) TTP
     * (3) User interface
     * @param scanner the user input method for the user interface layer.
     * @param systemParameters the system parameters that the TTP layer should be constructed with.
     * @param port the port for the communication layer.
     * @return a ready to use TTP user interface.
     */
    public static TTPUserInterface buildTTPUserInterface(Scanner scanner, SystemParameters systemParameters, int port) {
        TTP ttp = buildTTP(systemParameters, port);
        TTPUserInterface userInterface = new TTPUserInterface(scanner, ttp);
        ttp.setOverlay(userInterface);
        userInterface.setUnderlay(ttp);
        // Initialize the logger and supply it to the layers.
        // Construct the logger.
        Logger logger = new Logger(ttp.getAddress());
        // Set the logger for the comm. layer.
        ttp.getUnderlay().setLogger(logger);
        // Set the logger for TTP layer.
        ttp.setLogger(logger);
        // Set the logger for user interface layer.
        userInterface.setLogger(logger);
        return userInterface;
    }

}

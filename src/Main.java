import ttp.SystemParameters;
import misc.Builders;

import java.util.Scanner;

public class Main {

    /**
     * Returns the system parameters that should be used by the TTP.
     * @return the system parameters.
     */
    public static SystemParameters getSystemParameters() {
        return new SystemParameters(4, true, true);
    }

    /**
     * Main entry point of the application. Can be run in two modes:
     * 1. TTP Mode  => ttp [port]
     * 2. Node Mode => node [TTP address] [port]
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Please provide a mode (ttp or node).");
            return;
        }
        // Get the `mode` of the process. This can be ttp or node.
        String mode = args[0];
        // We will receive user inputs from the standard input.
        Scanner scanner = new Scanner(System.in);
        // If the mode is set to ttp, we only need a port to start running the ttp.
        if(mode.equals("ttp")) {
            // Make sure that port is provided.
            if(args.length < 2) {
                System.out.println("Please provide a port for TTP to run.");
                return;
            }
            // Make sure that provided port is an integer.
            int port;
            try {
                port = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.out.println("Invalid port: " + args[1]);
                return;
            }
            // Start running the process as a TTP.
            runTTP(scanner, getSystemParameters(), port);
        } else if(mode.equals("node")) {
            // If the mode is set to node, we need the TTP address and a port. Make sure
            // that those arguments are provided.
            if(args.length < 3) {
                System.out.println("Please provide the TTP address and the port");
                return;
            }
            String ttpAddress = args[1];
            int port;
            try {
                port = Integer.parseInt(args[2]);
            } catch (Exception e) {
                System.out.println("Invalid port: " + args[2]);
                return;
            }
            runNode(scanner, ttpAddress, port);
        }
        System.exit(0);
    }

    /**
     * Constructs the node user interface with the correct protocol stack and starts asking the user
     * for inputs to be handled by the user interface.
     * @param scanner the user input method.
     * @param ttpAddress address of the TTP.
     * @param port the port that the communication layer should be bound to.
     */
    public static void runNode(Scanner scanner, String ttpAddress, int port) {
        Builders.buildAuthNodeUserInterface(scanner, ttpAddress, port).run();
    }

    /**
     * Constructs the TTP user interface with the correct protocol stack and starts asking the user
     * for inputs.
     * @param scanner the user input method.
     * @param systemParameters the system parameters that the TTP should work with.
     * @param port the port that the communication layer should be bound to.
     */
    public static void runTTP(Scanner scanner, SystemParameters systemParameters, int port) {
        Builders.buildTTPUserInterface(scanner, systemParameters, port).run();
    }
}

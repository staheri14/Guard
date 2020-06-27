import ttp.SystemParameters;
import userinterface.Constructors;

import java.util.Scanner;

public class Main {

    public static SystemParameters getSystemParameters() {
        return new SystemParameters(4, true, true, true);
    }

    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Please provide a mode (ttp or node).");
            return;
        }
        String mode = args[0];
        Scanner scanner = new Scanner(System.in);
        if(mode.equals("ttp")) {
            if(args.length < 2) {
                System.out.println("Please provide a port for TTP to run.");
                return;
            }
            int port;
            try {
                port = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.out.println("Invalid port: " + args[1]);
                return;
            }
            runTTP(scanner, getSystemParameters(), port);
        } else if(mode.equals("node")) {
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

    public static void runNode(Scanner scanner, String ttpAddress, int port) {
        Constructors.createAuthNodeUserInterface(scanner, ttpAddress, port).run();
    }

    public static void runTTP(Scanner scanner, SystemParameters systemParameters, int port) {
        Constructors.createTTPUserInterface(scanner, systemParameters, port).run();
    }
}

package userinterface;

import protocol.Request;
import protocol.Response;
import skipnode.NodeInfo;
import ttp.RegisteredNodeInformation;
import ttp.TTP;

import java.util.List;
import java.util.Scanner;

public class TTPUserInterface extends UserInterface {

    private final TTP ttp;

    public TTPUserInterface(Scanner scanner, TTP ttp) {
        super(scanner);
        this.ttp = ttp;
        setUnderlay(ttp);
    }

    @Override
    protected void initialize() {
        // No initialization is necessary.
    }

    @Override
    protected String menu() {
        return "== TTP Menu ==\n" +
                "1. Show registered nodes\n" +
                "2. Broadcast join requests\n" +
                "3. Start experiments\n" +
                "4. Terminate\n";
    }

    @Override
    public boolean handleUserInput(int input) {
        if(input == 1) {
            for(RegisteredNodeInformation node : ttp.getRegisteredNodes()) {
                System.out.println(node);
            }
        } else if(input == 2) {
        } else if(input == 4) return false;
        return true;
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        return null;
    }
}

package userinterface;

import guard.node.packets.requests.NodeRegisterRequest;
import protocol.Response;
import skipnode.packets.requests.SearchByNumIDRequest;

public class NodeUserInterface extends UserInterface {

    @Override
    public void showMenu() {
        System.out.println("1. Join");
        System.out.println("2. Search");
    }

    @Override
    public void handleUserInput(int input) {
        if(input == 1) {
            Response r = send(getAddress(), new NodeRegisterRequest());
        } else if(input == 2) {
            Response r = send(getAddress(), new SearchByNumIDRequest(3));
        }
    }
}

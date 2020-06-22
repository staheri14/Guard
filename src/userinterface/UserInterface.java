package userinterface;

import protocol.Layer;
import protocol.Request;
import protocol.Response;

public class UserInterface extends Layer {

    public UserInterface(int port) {
    }

    public void showMenu() {
        System.out.println();
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        return null;
    }
}

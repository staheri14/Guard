package userinterface;

import protocol.Layer;
import protocol.Request;
import protocol.RequestType;
import protocol.Response;

public abstract class UserInterface extends Layer {

    public abstract void showMenu();

    public abstract void handleUserInput(int input);

    @Override
    public Response handleReceivedRequest(Request request) {
        return null;
    }
}

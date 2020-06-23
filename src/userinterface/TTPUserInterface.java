package userinterface;

public class TTPUserInterface extends UserInterface {

    @Override
    public void showMenu() {
        System.out.println("1. Show registered nodes");
        System.out.println("2. Start experiments");
    }

    @Override
    public void handleUserInput(int input) {

    }
}

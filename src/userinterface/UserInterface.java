package userinterface;

import network.Layer;

import java.util.Scanner;

public abstract class UserInterface extends Layer {

    private final Scanner scanner;

    public UserInterface(Scanner scanner) {
        this.scanner = scanner;
    }

    protected abstract void initialize();

    protected abstract String menu();

    protected abstract boolean handleUserInput(int input);

    public void run() {
        initialize();
        while(true) {
            System.out.println(menu());
            int input = promptInteger("Make a selection");
            if(!handleUserInput(input)) break;
        }
        terminate();
    }

    protected int promptInteger(String prompt) {
        System.out.print("> " + prompt + ": ");
        int response;
        while(true) {
            try {
                String responseStr = scanner.nextLine();
                response = Integer.parseInt(responseStr.trim());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Expected an integer.");
            }
        }
        return response;
    }

    protected boolean promptBoolean(String prompt) {
        System.out.print("> " + prompt + " (y/n): ");
        String response;
        while(true) {
            response = scanner.nextLine();
            response = response.trim().toLowerCase();
            if(response.equals("y") || response.equals("n")) break;
            System.out.println("Invalid input. Please type y for yes, n for no.");
        }
        return response.equals("y");
    }
}

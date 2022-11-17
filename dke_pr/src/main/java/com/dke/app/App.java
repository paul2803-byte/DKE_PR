package com.dke.app;

import com.dke.app.State.MockStates;
import com.dke.app.State.RealStates;
import com.dke.app.State.StateService;

import java.util.Scanner;

public class App 
{
    public static void main( String[] args )
    {
        Scanner scanner = new Scanner(System.in);
        // check if the states should be read through real or mock data
        System.out.print("Should real or mock data be used? Type in m for mock or r for real: ");
        String input = scanner.nextLine();
        while (!input.equals("r") && !input.equals("m")) {
            System.out.print("Not a valid option. Type in m for mock data or r for real data: ");
            input = scanner.nextLine();
        }
        boolean mockData = input.equals("m");

        // inform the user about the mode
        if(mockData) {
            System.out.println("Mock data gets used");
        } else {
            System.out.println("Real data gets used");
        }

        // read in the static data and store it to the knowledge graph
        System.out.println("Reading the static data");


        // let the user read in new data or terminate the application
        askForNewStates(mockData, scanner);
    }

    private static void askForNewStates(boolean mockData, Scanner scanner) {
        while(true) {
            System.out.print("Enter r to read new states or e to exit: ");
            String input = scanner.nextLine();
            if(input.equals("r")){
                System.out.println("Reading new states");
                storeStates(mockData);
                System.out.println("New states got stored");
            } else if (input.equals("e")){
                System.out.println("Exiting the application");
                break;
            } else {
                System.out.println("Symbol not found");
            }
        }
    }

    private static void storeStates(boolean mockData){
        StateService stateService;
        if(mockData) {
            stateService = new MockStates();
        } else {
            stateService = new RealStates();
        }
        StorageService.storeStates(stateService.getStates());
    }
}

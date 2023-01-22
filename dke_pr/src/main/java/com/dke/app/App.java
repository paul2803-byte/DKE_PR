package com.dke.app;

import com.dke.app.State.MockStates;
import com.dke.app.State.RealStates;
import com.dke.app.State.StateService;
import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args ) {
        Scanner scanner = new Scanner(System.in);

        boolean mockData = askForDataType(scanner);

        String owner = askForOwner(scanner);

        NumberFlightService.getFlightsPerOwner(owner);

        double collisionLimit = askForCollisionDistance(scanner);

        try {
            if(StorageService.checkIfStaticData()) {
                System.out.println("Static data already got read in continuing with States");
            } else {
                System.out.println("Reading the static data...");
                List<Model> aircraft =  AircraftService.getAircrafts();
                System.out.println("Storing the data to the knowledge graph...");
                StorageService.storeAircrafts(aircraft);
                System.out.println("Static data got stored");
            }
            askForNewStates(mockData, collisionLimit, scanner);
        } catch (HttpException e) {
            System.out.println();
            System.out.println("Could not upload to the knowledge graph. Check if the Fuseki Server is running. \nThen restart the application.");
        }
    }

    private static boolean askForDataType(Scanner scanner) {
        System.out.print("Should real or mock data be used? Type in m for mock or r for real: ");
        String input = scanner.nextLine();
        while (!input.equals("r") && !input.equals("m")) {
            System.out.print("Not a valid option. Type in m for mock data or r for real data: ");
            input = scanner.nextLine();
        }
        boolean mockData = input.equals("m");
        if (mockData) {
            System.out.println("Mock data gets used.");
        } else {
            System.out.println("Real data gets used.");
        }
        return mockData;
    }


    private static void askForNewStates(boolean mockData, double collisionLimit, Scanner scanner)  throws HttpException{
        while(true) {
            System.out.print("Select \nr to read new states \ne to exit \nc to change the collision limit \nenter your input: ");
            String input = scanner.nextLine();
            if(input.equals("r")){
                System.out.println("Reading new states");
                storeStates(mockData, collisionLimit);
                System.out.println("New states got stored");
            } else if (input.equals("e")){
                System.out.println("Exiting the application");
                break;
            } else if(input.equals("c")) {
                collisionLimit = askForCollisionDistance(scanner);
            } else {
                System.out.println("Symbol not found");
            }
        }
    }

    private static void storeStates(boolean mockData, double collisionLimit) throws HttpException{
        StateService stateService;
        if (mockData) {
            stateService = new MockStates();
        } else {
            stateService = new RealStates();
        }
        List<Model> states = stateService.getStates();

        checkForCollision(states, collisionLimit);
        // StorageService.storeStates(states);
    }

    private static double askForCollisionDistance(Scanner scanner) {
        double distance = 0;
        while(true) {
            System.out.print("Enter under which distance of two flights a warning should be given out: ");
            String input = scanner.nextLine();
            try {
                distance = Double.parseDouble(input);
                break;
            } catch (Exception e) {
                System.out.println("Please type in a valid distance in the double format!");
            }
        }
        return distance;
    }

    private static String askForOwner(Scanner scanner) {
        System.out.print("Please enter the owner for which you want to retrieve the number of aircraft: ");
        return scanner.nextLine();
    }

    private static void checkForCollision(List<Model> states, double collisionLimit) {
        Model collisions = CollisionService.checkForCollisions(states, collisionLimit);
        StorageService.storeCollisionEvents(collisions);
    }
}

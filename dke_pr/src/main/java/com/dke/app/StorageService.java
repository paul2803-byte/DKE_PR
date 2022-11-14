package com.dke.app;

import org.apache.jena.graph.Graph;
import org.opensky.model.StateVector;

import java.util.List;

public class StorageService {

    public static void storeAircraft() {
        // TODO: implement getting the aircraft from the AircraftService.java and storing them in the knowledge graph
    }

    public static void storeStates(List<Graph> states) {
        states.forEach(StorageService::storeState);
    }

    private static void storeState(Graph state) {
        // TODO: implement the storage of a state
        System.out.println("storing new state");
        System.out.println(state);
    }
}

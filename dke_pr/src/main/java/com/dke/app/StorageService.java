package com.dke.app;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnection;

import java.util.List;

public class StorageService {

    private static final String SERVER = "http://localhost:3030/flight_database/";

    public static void storeAircraft() {
        // TODO: implement getting the aircraft from the AircraftService.java and storing them in the knowledge graph
    }

    public static void storeStates(List<Model> states) {
        states.forEach(StorageService::storeState);
    }

    private static void storeState(Model state) {
        // TODO: implement the storage of a state
        RDFConnection.connect(SERVER).load(state);

    }
}

package com.dke.app;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnection;
import java.util.List;

public class StorageService {

    private static final String SERVER = "http://localhost:3030/flight_database/";

    public static void storeAircrafts(List<Model> aircrafts) {
        // TODO: implement getting the aircraft from the AircraftService.java and storing them in the knowledge graph
        System.out.println(aircrafts.size());
        aircrafts.forEach(StorageService::storeModel);
    }

    public static void storeStates(List<Model> states) {
        states.forEach(StorageService::storeModel);
    }

    private static void storeModel(Model state) {
        // TODO: implement the storage of a state
        RDFConnection.connect(SERVER).load(state);

    }
}

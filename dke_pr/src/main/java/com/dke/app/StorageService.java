package com.dke.app;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.vocabulary.RDF;

import java.time.LocalDateTime;
import java.util.List;

public class StorageService {

    private static final String SERVER = "http://localhost:3030/flight_database/";
    private static final String STATIC_GRAPH = "http://www.dke.uni-linz.ac.at/pr-dke/static_graph";
    private static final String DYNAMIC_GRAPH = "http://www.dke.uni-linz.ac.at/pr-dke/dynamic_graph/";

    public static void storeAircrafts(List<Model> aircrafts) throws HttpException {
        aircrafts.forEach(aircraft -> storeModel(aircraft, STATIC_GRAPH));
    }

    public static void storeStates(List<Model> states) throws HttpException {
        String dynamicLink = DYNAMIC_GRAPH + LocalDateTime.now();
        states.forEach(state -> storeModel(state, dynamicLink));
    }

    private static void storeModel(Model model, String graphName) {
        RDFConnection.connect(SERVER).load(graphName, model);
    }

    public static boolean checkIfStaticData() {
        try {
            return !RDFConnection.connect(SERVER).fetch(STATIC_GRAPH).isEmpty();
        } catch (Exception e){
            System.out.println(e.getStackTrace());
            return false;
        }

    }
}

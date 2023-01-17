package com.dke.app;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.vocabulary.RDF;

import java.time.LocalDateTime;
import java.util.List;

public class StorageService {

    public static final String SERVER = "http://localhost:3030/flight_database/";
    private static final String STATIC_GRAPH = "http://www.dke.uni-linz.ac.at/pr-dke/static_graph";
    private static final String DYNAMIC_GRAPH = "http://www.dke.uni-linz.ac.at/pr-dke/dynamic_graph/";
    private static final String COLLISION_GRAPH = "http://www.dke.uni-linz.ac.at/pr-dke/collision_graph/";
    private static String lastCollisionUpdate;

    public static void storeAircrafts(List<Model> aircrafts) throws HttpException {
        RDFConnection server = RDFConnection.connect(SERVER);
        ProgressBar pb = new ProgressBar("Uploading Aircrafts", aircrafts.size());
        aircrafts.forEach(aircraft -> {
            storeModel(aircraft, STATIC_GRAPH, server);
            pb.step();
        });
        pb.close();
    }

    public static void storeStates(List<Model> states) throws HttpException {
        RDFConnection server = RDFConnection.connect(SERVER);
        ProgressBar pb = new ProgressBar("Uploading States", states.size());
        String dynamicLink = DYNAMIC_GRAPH + LocalDateTime.now();
        states.forEach(state -> {
            storeModel(state, dynamicLink, server);
            pb.step();
        });
        pb.close();
    }

    public static void storeCollisionEvents(Model events) {
        RDFConnection server = RDFConnection.connect(SERVER);
        String time = LocalDateTime.now().toString();
        String link = COLLISION_GRAPH + time;
        lastCollisionUpdate = time;
        storeModel(events, link, server);
    }

    private static void storeModel(Model model, String graphName, RDFConnection server) {
        server.load(graphName, model);
    }

    public static boolean checkIfStaticData() {
        try {
            return !RDFConnection.connect(SERVER).fetch(STATIC_GRAPH).isEmpty();
        } catch (Exception e){
            return false;
        }
    }

    public static String getLastCollisionURL() {
        if(lastCollisionUpdate != null) {
            return COLLISION_GRAPH + lastCollisionUpdate;
        } else {
            return null;
        }

    }
}

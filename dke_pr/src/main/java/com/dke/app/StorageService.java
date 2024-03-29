package com.dke.app;

import me.tongfei.progressbar.ProgressBar;
import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.vocabulary.RDF;
import java.time.LocalDateTime;
import java.util.List;

public class StorageService {

    public static final String SERVER = "http://localhost:3030/flight_database/";
    public static final String STATIC_GRAPH = "http://www.dke.uni-linz.ac.at/pr-dke/static_graph";
    private static final String DYNAMIC_GRAPH = "http://www.dke.uni-linz.ac.at/pr-dke/dynamic_graph/";
    private static final String COLLISION_GRAPH = "http://www.dke.uni-linz.ac.at/pr-dke/collision_graph/";
    private static final String VELOCITY_GRAPH = "http://www.dke.uni-linz.ac.at/pr-dke/velocity_graph/";
    private static final String DIRECTION_GRAPH = "http://www.dke.uni-linz.ac.at/pr-dke/direction_graph/";
    private static final String AGGREGATION_GRAPH = "http://www.dke.uni-linz.ac.at/pr-dke/aggregation_graph/";

    private static final String EXPECTED_POSITION_GRAPH = "http://www.dke.uni-linz.ac.at/pr-dke/expected_position/";

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
        storeStateGraph(dynamicLink, String.valueOf(LocalDateTime.now()));
    }

    public static void storeCollisionEvents(Model events) {
        RDFConnection server = RDFConnection.connect(SERVER);
        String time = LocalDateTime.now().toString();
        String link = COLLISION_GRAPH + time;
        storeCollisionGraph(link, time);
        storeModel(events, link, server);
    }

    public static void storeExpectedPosEvents(Model events) {
        RDFConnection server = RDFConnection.connect(SERVER);
        String time = LocalDateTime.now().toString();
        String link = EXPECTED_POSITION_GRAPH + time;
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

    private static void storeCollisionGraph(String url, String time) {
        Model model = ModelFactory.createDefaultModel();
        model.createResource(url)
                .addProperty(RDF.type, model.createProperty(RDFService.EX_URL+"CollisionGraph"))
                .addProperty(model.createProperty(RDFService.PROPERTY_URL+"time"), time)
                .addProperty(model.createProperty(RDFService.PROPERTY_URL+"url"), url);
        RDFConnection server = RDFConnection.connect(SERVER);
        server.load(model);
    }

    public static void storeVelocityEvents(Model events) {
        RDFConnection server = RDFConnection.connect(SERVER);
        String time = LocalDateTime.now().toString();
        String link = VELOCITY_GRAPH + time;
        storeModel(events, link, server);
    }


   public static void storeNumberAircraftsAggregation(Model number) {
        RDFConnection server = RDFConnection.connect(SERVER);
        String time = LocalDateTime.now().toString();
        String link = AGGREGATION_GRAPH + time;
        storeModel(number, link, server);
    }


    public static void storeDirectionEvents(Model events) {
        RDFConnection server = RDFConnection.connect(SERVER);
        String time = LocalDateTime.now().toString();
        String link = DIRECTION_GRAPH + time;
        storeModel(events, link, server);
    }

    private static void storeStateGraph(String link, String time) {
        Model model = ModelFactory.createDefaultModel();
        model.createResource(link)
                .addProperty(RDF.type, model.createProperty(RDFService.EX_URL+"StateGraph"))
                .addProperty(model.createProperty(RDFService.PROPERTY_URL+"time"), time)
                .addProperty(model.createProperty(RDFService.PROPERTY_URL+"url"), link);
        RDFConnection server = RDFConnection.connect(SERVER);
        server.load(model);
    }
}

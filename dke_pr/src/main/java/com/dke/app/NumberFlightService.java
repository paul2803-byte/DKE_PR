package com.dke.app;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.topbraid.shacl.rules.RuleUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class NumberFlightService {

    public static Model getFlightsPerOwner(String owner) {

        Model shapesModel = getShapeModel(owner);

        Model dataModel = ModelFactory.createDefaultModel();
        RDFConnection server = RDFConnection.connect(StorageService.SERVER);
        Model staticData = server.fetch(StorageService.STATIC_GRAPH);
        AircraftService.getAircrafts().forEach(model -> dataModel.add(model));
        // RDFDataMgr.write(System.out, allStates, Lang.TTL);

        // Perform the validation of everything, using the data model
        // also as the shapes model - you may have them separated
        Model report = RuleUtil.executeRules(dataModel, shapesModel, null, null);

        // Print violations
        RDFDataMgr.write(System.out, report, Lang.TTL);

        return report;
    }

    private static Model getShapeModel(String owner){
        String SHAPE = "dke_pr/shacl_shapes/number_flights.ttl";
        String NEW_SHAPE = "dke_pr/shacl_shapes/adapted_number_flights.ttl";
        Path path = Paths.get(SHAPE);
        Path newPath = Paths.get(NEW_SHAPE);

        String content = null;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        content = content.replace("Private", owner);
        try {
            Files.writeString(newPath, content);
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        Model model = RDFDataMgr.loadModel(NEW_SHAPE);
        return model;
    }
}

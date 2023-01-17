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

public class CollisionService {



    /*
    Offene Fragen

        dynamisches Setzen der distance: kann aus einem String ein Model erzeugt werden oder muss gelesen eingesetzt in File geschrieben und dann Model erzeugt werden
        ByteOutput Stream
     */

    // TODO: abklären warum shacl Funktion nicht funktioniert: sonst funktioniert die Query auch ohne Funktion ist aber anders schöner
    public static Model checkForCollisions(List<Model> modelList, double distance) {

        Model shapesModel = getShapeModel(distance);
        System.out.println(modelList.size());
        // RDFDataMgr.write(System.out, shapesModel, Lang.TTL);
        Model dataModel = ModelFactory.createDefaultModel();
        modelList.stream().forEach(model -> dataModel.add(model));
        dataModel.add(getLastCollisionEvents());

        // Perform the validation of everything, using the data model
        // also as the shapes model - you may have them separated
        Model report = RuleUtil.executeRules(dataModel, shapesModel, null, null);

        // Print violations
        RDFDataMgr.write(System.out, report, Lang.TTL);

        return report;
    }

    private static Model getShapeModel(double distance) {
        String SHAPE = "dke_pr/shacl_shapes/collision_check.ttl";
        String NEW_SHAPE = "dke_pr/shacl_shapes/adapted_collision_check.ttl";
        Path path = Paths.get(SHAPE);
        Path newPath = Paths.get(NEW_SHAPE);

        String content = null;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        content = content.replace("/*distance*/", ""+calculateDistance(distance));
        try {
            Files.writeString(newPath, content);
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        Model model = RDFDataMgr.loadModel(NEW_SHAPE);
        return model;
    }

    private static double calculateDistance(double input) {
        return Math.pow(input * 0.008, 2);
    }

    private static Model getLastCollisionEvents() {
        String lastCollision = StorageService.getLastCollisionURL();
        if(lastCollision != null) {
            RDFConnection server = RDFConnection.connect(StorageService.SERVER);
            Model collision =  server.fetch(lastCollision);
            RDFDataMgr.write(System.out, collision, Lang.TTL);
            return collision;
        } else {
            return ModelFactory.createDefaultModel();
        }
    }
}

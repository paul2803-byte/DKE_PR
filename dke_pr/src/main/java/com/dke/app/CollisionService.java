package com.dke.app;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
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

     public static Model checkForCollisions(List<Model> modelList, double distance) {

        Model shapesModel = getShapeModel(distance);
        Model dataModel = ModelFactory.createDefaultModel();
        modelList.stream().forEach(model -> dataModel.add(model));
        dataModel.add(getLastCollisionEvents());

        Model report = RuleUtil.executeRules(dataModel, shapesModel, null, null);

        RDFDataMgr.write(System.out, report, Lang.TTL);

        return report;
    }

    private static Model getShapeModel(double distance) {
        String SHAPE = "dke_pr/shacl_shapes/collision_check.ttl";
        String NEW_SHAPE = "dke_pr/shacl_shapes/adapted_collision_check.ttl";
        Path path = Paths.get(SHAPE);
        Path newPath = Paths.get(NEW_SHAPE);

        String content;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        content = content.replace("/*distance*/", ""+calculateDistance(distance));
        try {
            Files.writeString(newPath, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return RDFDataMgr.loadModel(NEW_SHAPE);
    }

    private static double calculateDistance(double input) {
        return Math.pow(input * 0.008, 2);
    }

    private static Model getLastCollisionEvents() {
        String lastCollision = getLastCollisionLink();
        if(lastCollision != null) {
            RDFConnection server = RDFConnection.connect(StorageService.SERVER);
            try {
                Model collision =  server.fetch(lastCollision);
                return collision;
            } catch (Exception e){
                System.out.println("Could not fetch last Collision Graph. Checks concerning converging are not possible");
                return ModelFactory.createDefaultModel();
            }

        } else {
            return ModelFactory.createDefaultModel();
        }
    }

    private static String getLastCollisionLink() {
        RDFConnection server = RDFConnection.connect(StorageService.SERVER);
        QueryExecution execution = server.query("PREFIX property: <http://www.dke.uni-linz.ac.at/pr-dke/property#> \n" +
                "PREFIX ex: <http://www.w3.org/2022/example#>\n" +
                "PReFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "SELECT ?url WHERE {\n" +
                "  ?collisionGraph a ex:CollisionGraph ;\n" +
                "  \tproperty:url ?url ;\n" +
                "  \tproperty:time ?time.\n" +
                "} ORDER BY DESC(xsd:dateTime(?time)) LIMIT 1");
        ResultSet results = execution.execSelect();
        if(results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            return solution.get("url").toString();
        } else return null;
    }
}

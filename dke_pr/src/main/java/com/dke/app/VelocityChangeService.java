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

public class VelocityChangeService {

    public static Model checkForVelocityChanges(List<Model> modelList, double thresholdValue) {

        Model shapesModel = getShapeModel(thresholdValue);
        Model dataModel = ModelFactory.createDefaultModel();
        modelList.stream().forEach(model -> dataModel.add(model));
        dataModel.add(getOldStates());

        Model report = RuleUtil.executeRules(dataModel, shapesModel, null, null);
        RDFDataMgr.write(System.out, report, Lang.TTL);

        return report;
    }

    private static Model getShapeModel(double thresholdValue) {
        String SHAPE = "dke_pr/shacl_shapes/velocity_check.ttl";
        Path path = Paths.get(SHAPE);

        String content;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        content = content.replace("/thresholdValue/", "" + thresholdValue);
        try {
            Files.writeString(path, content);
        } catch (IOException ex) {
            System.out.println(ex);
            throw new RuntimeException(ex);
        }
        return RDFDataMgr.loadModel(SHAPE);
    }

    private static Model getOldStates() {
        String lastVelocity = getLastVelocityLink();
        if(lastVelocity != null) {
            RDFConnection server = RDFConnection.connect(StorageService.SERVER);
            try {
                Model oldStates =  server.fetch(lastVelocity);
                return oldStates;
            } catch (Exception e){
                System.out.println("Could not fetch last Velocity Graph. Checks concerning converging are not possible");
                return ModelFactory.createDefaultModel();
            }

        } else {
            return ModelFactory.createDefaultModel();
        }
    }

    private static String getLastVelocityLink() {
        RDFConnection server = RDFConnection.connect(StorageService.SERVER);
        QueryExecution execution = server.query("PREFIX property: <http://www.dke.uni-linz.ac.at/pr-dke/property#> \n" +
                "PREFIX ex: <http://www.w3.org/2022/example#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "SELECT ?url WHERE {\n" +
                "  ?stateGraph a ex:StateGraph ;\n" +
                "  \tproperty:url ?url ;\n" +
                "  \tproperty:time ?time.\n" +
                "} ORDER BY DESC(xsd:dateTime(?time)) LIMIT 1");
        ResultSet results = execution.execSelect();
        if (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            return solution.get("url").toString();
        } else return null;
    }
}

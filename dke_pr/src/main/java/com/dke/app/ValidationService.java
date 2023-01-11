package com.dke.app;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.topbraid.shacl.rules.RuleUtil;
import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.util.ModelPrinter;

import java.util.List;

public class ValidationService {

    public static boolean validateState(Model stateModel) {
        String SHAPE = "dke_pr/shacl_shapes/state_shacl.ttl";
        return validate(stateModel, SHAPE);
    }

    /**
     *
     * Validate aircraft
     *
     * @param aircraft  the aircraft graph
     * @return boolean
     */
    public static boolean validateAircraft(Model aircraft) {
        String SHAPE = "dke_pr/shacl_shapes/aircraft_shacl.ttl";
        return validate(aircraft, SHAPE);
    }

    private static boolean validate(Model aircraft, String SHAPE) {
        Graph shapesGraph = RDFDataMgr.loadGraph(SHAPE);

        Shapes shapes = Shapes.parse(shapesGraph);
        ValidationReport report = ShaclValidator.get().validate(shapes, aircraft.getGraph());

        boolean valid = report.conforms();
        if(valid) {
            return true;
        } else {
            // log the result to console if the model is invalid
            RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
            return false;
        }
    }

    /*
    Offene Fragen

        Fehler beim Anwenden des Shacl Shapes: "Value must be an instance of rdfs:Class"
        Wo wird durch construct erzeugt, wie kann erzeugtes Model abgefragt werden
        dynamisches Setzen der distance: kann aus einem String ein Model erzeugt werden oder muss gelesen eingesetzt in File geschrieben und dann Model erzeugt werden
        ByteOutput Stream
     */

    // TODO: abklären warum shacl Funktion nicht funktioniert: sonst funktioniert die Query auch ohne Funktion ist aber anders schöner
    public static void checkForCollisions(List<Model> modelList) {

        String SHAPE = "dke_pr/shacl_shapes/collision_check.ttl";
        Model shapesModel = RDFDataMgr.loadModel(SHAPE);
        // RDFDataMgr.write(System.out, shapesModel, Lang.TTL);

        Model dataModel = ModelFactory.createDefaultModel();
        modelList.stream().forEach(model -> dataModel.add(model));
        // RDFDataMgr.write(System.out, allStates, Lang.TTL);

        // Perform the validation of everything, using the data model
        // also as the shapes model - you may have them separated
        Model report = RuleUtil.executeRules(dataModel, shapesModel, null, null);

        // Print violations
        RDFDataMgr.write(System.out, report, Lang.TTL);
    }
    
}

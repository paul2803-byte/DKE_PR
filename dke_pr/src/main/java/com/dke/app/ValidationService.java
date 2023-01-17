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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
}

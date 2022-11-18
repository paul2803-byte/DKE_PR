package com.dke.app;

import org.apache.jena.base.Sys;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;

public class ValidationService {

    public static boolean validateState(Model stateModel) {
        String SHAPE = "shacl_shapes/flight_shacl.ttl";
        Graph shapesGraph = RDFDataMgr.loadGraph(SHAPE);
        // TODO: check why type of state gets not checked

        Shapes shapes = Shapes.parse(shapesGraph);
        ValidationReport report = ShaclValidator.get().validate(shapes, stateModel.getGraph());
        boolean valid = report.conforms();
        if(valid) {
            return true;
        } else {
            // log the result to console if the model is invalid
            RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
            return false;
        }
    }

    public static boolean validateAircraft(Graph aircraftGraph) {
        // TODO: implement validation with the shcal shape
        return true;
    }
}

package com.dke.app;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ValidationService {

    public static boolean validateState(Model stateModel) {
        // TODO: implement validation with the shacl shape
        String SHAPE = "shacl_shapes/flight_shacl.ttl";
        System.out.println(stateModel.getGraph());
        Graph shapesGraph = RDFDataMgr.loadGraph(SHAPE);
        System.out.println(shapesGraph);

        Shapes shapes = Shapes.parse(shapesGraph);
        ValidationReport report = ShaclValidator.get().validate(shapes, stateModel.getGraph());
        ShLib.printReport(report);
        System.out.println();
        RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
        return true;
    }

    public static boolean validateAircraft(Graph aircraftGraph) {
        // TODO: implement validation with the shcal shape
        return true;
    }
}

package com.dke.app;

import org.apache.jena.graph.Graph;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;

public class ValidationService {

    public static boolean validateState(Graph stateGraph) {
        // TODO: implement validation with the shacl shape
        String SHAPES = "shape_graph_test.ttl";
        String DATA = "data_graph_test.tll";

        Graph shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        Graph dataGraph = RDFDataMgr.loadGraph(DATA);

        Shapes shapes = Shapes.parse(shapesGraph);

        ValidationReport report = ShaclValidator.get().validate(shapes, dataGraph);
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

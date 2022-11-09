package com.dke.app;

import org.apache.jena.graph.Graph;
import org.apache.jena.riot.Lang;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;
import org.opensky.api.OpenSkyApi;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;
import org.apache.jena.shacl.Shapes;

import java.io.IOException;
import java.util.Collection;


public class App 
{
    public static void main( String[] args )
    {
        // storeFlights(getFlights(new String[]{"4b1815", "4b1817"}));
        testShacl();
    }


    private static Collection<StateVector> getFlights(String[] flights) {
        OpenSkyApi api = new OpenSkyApi();
        String[] wishedFlights = new String[]{"abc0e4", "4b1900", "a9c380"};
        try {
            return api.getStates(0, wishedFlights).getStates();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void storeFlights(Collection<StateVector> flights) {
        Model model = ModelFactory.createDefaultModel();
        final String FLIGHT_URL = "http://somewhere/";
        final String STATE_URL = "http://somewhere/state/";
        final String PROPERTY_URL = "http://somewhere/property/";
        flights.forEach(flight -> {
            System.out.println(flight);
            System.out.println(flight.getIcao24());
            Resource newFlight = model.createResource(FLIGHT_URL + flight.getIcao24())
                    .addProperty(model.createProperty(PROPERTY_URL + "#Country"), flight.getOriginCountry());

            Resource newState = model.createResource( STATE_URL + flight.getLastPositionUpdate() + flight.getIcao24())
                    .addProperty(model.createProperty(PROPERTY_URL + "#Longitude"), String.valueOf(flight.getLongitude()))
                    .addProperty(model.createProperty(PROPERTY_URL + "#Latitude"), String.valueOf(flight.getLatitude()))
                    .addProperty(model.createProperty(PROPERTY_URL + "#Altitude"), String.valueOf(flight.getGeoAltitude()))
                    .addProperty(model.createProperty(PROPERTY_URL + "#Heading"), String.valueOf(flight.getHeading()))
                    .addProperty(model.createProperty(PROPERTY_URL + "#Velocity"), String.valueOf(flight.getVelocity()));

            model.add(model.createStatement(newFlight, model.createProperty(PROPERTY_URL +"#hasState"), newState));
        });
        model.write(System.out);
        // TODO: later use this to directly validate with shacl shape
        model.getGraph();

    }

    private static void testShacl() {
        // TODO: find the place to out the shacl shapes
        String SHAPES = "shape_graph_test.ttl";
        String DATA = "data_graph_test.tll";

        Graph shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        Graph dataGraph = RDFDataMgr.loadGraph(DATA);

        Shapes shapes = Shapes.parse(shapesGraph);

        ValidationReport report = ShaclValidator.get().validate(shapes, dataGraph);
        ShLib.printReport(report);
        System.out.println();
        RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
    }
}

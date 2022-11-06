package com.dke.app;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.opensky.api.OpenSkyApi;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;

import java.io.IOException;
import java.util.Collection;


public class App 
{
    public static void main( String[] args )
    {
        storeFlights(getFlights(new String[]{"4b1815", "4b1817"}));
    }


    private static Collection<StateVector> getFlights(String[] flights) {
        OpenSkyApi api = new OpenSkyApi();
        try {
            return api.getStates(0, null).getStates();
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

            model.createStatement(newFlight, model.createProperty(PROPERTY_URL +"#hasState"), newState);

        });

        model.write(System.out);

    }
}

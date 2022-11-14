package com.dke.app.State;

import com.dke.app.ValidationService;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.opensky.model.StateVector;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class StateService {

    protected abstract Collection<StateVector> getStateVectors();

    public List<Graph> getStates() {
        // getting a list of state vectors depending on the implementation in the concrete class
        return getStateVectors().stream()
                // convert each element in the stream to a graph
                .map(this::convertToGraph)
                // validate each element in the stream with the shacl shape and only use return the valid ones
                .filter(ValidationService::validateState)
                // convert stream to list
                .collect(Collectors.toList());
    }

    private Graph convertToGraph(StateVector state) {
        // TODO: check how the composition of state and aircraft is working --> state gets added to the aircraft
        Model model = ModelFactory.createDefaultModel();
        final String FLIGHT_URL = "http://somewhere/";
        final String STATE_URL = "http://somewhere/state/";
        final String PROPERTY_URL = "http://somewhere/property/";
        Resource newFlight = model.createResource(FLIGHT_URL + state.getIcao24())
                .addProperty(model.createProperty(PROPERTY_URL + "#Country"), state.getOriginCountry());

        Resource newState = model.createResource( STATE_URL + state.getLastPositionUpdate() + state.getIcao24())
                .addProperty(model.createProperty(PROPERTY_URL + "#Longitude"), String.valueOf(state.getLongitude()))
                .addProperty(model.createProperty(PROPERTY_URL + "#Latitude"), String.valueOf(state.getLatitude()))
                .addProperty(model.createProperty(PROPERTY_URL + "#Altitude"), String.valueOf(state.getGeoAltitude()))
                .addProperty(model.createProperty(PROPERTY_URL + "#Heading"), String.valueOf(state.getHeading()))
                .addProperty(model.createProperty(PROPERTY_URL + "#Velocity"), String.valueOf(state.getVelocity()));

        model.add(model.createStatement(newFlight, model.createProperty(PROPERTY_URL +"#hasState"), newState));
        return model.getGraph();
    }
}

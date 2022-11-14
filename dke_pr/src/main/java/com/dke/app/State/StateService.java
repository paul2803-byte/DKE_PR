package com.dke.app.State;

import com.dke.app.ValidationService;
import org.apache.jena.graph.Graph;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACL;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.opensky.model.StateVector;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class StateService {

    protected abstract Collection<StateVector> getStateVectors();

    public List<Model> getStates() {
        // getting a list of state vectors depending on the implementation in the concrete class
        return getStateVectors().stream()
                // convert each element in the stream to a graph
                .map(this::convertToModel)
                // validate each element in the stream with the shacl shape and only use return the valid ones
                .filter(ValidationService::validateState)
                // convert stream to list
                .collect(Collectors.toList());
    }

    private Model convertToModel(StateVector state) {
        // TODO: check how the composition of state and aircraft is working --> state gets added to the aircraft
        Model model = ModelFactory.createDefaultModel();

        Map<String, String> prefixes = new HashMap<>();

        final String TYPE_URL = "http://www.somewhere/type#";
        final String FLIGHT_URL = "http://www.somewhere/flight#";
        final String STATE_URL = "http://www.somewhere/state#";
        final String PROPERTY_URL = "http://wwww.somewhere/property#";
        model.setNsPrefix("flight", FLIGHT_URL);
        model.setNsPrefix("property", PROPERTY_URL);
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

        Resource newFlight = model.createResource(FLIGHT_URL + state.getIcao24())
                .addProperty(RDF.type, TYPE_URL + "Flight")
                .addProperty(model.createProperty(PROPERTY_URL + "Country"), state.getOriginCountry())
                .addProperty(model.createProperty(PROPERTY_URL + "Icao24"), state.getIcao24());

        Resource newState = model.createResource( STATE_URL + state.getLastPositionUpdate() + state.getIcao24())
                .addProperty(RDF.type, TYPE_URL+"State")
                .addProperty(model.createProperty(PROPERTY_URL + "Longitude"), String.valueOf(state.getLongitude()))
                .addProperty(model.createProperty(PROPERTY_URL + "Latitude"), String.valueOf(state.getLatitude()))
                .addProperty(model.createProperty(PROPERTY_URL + "Altitude"), String.valueOf(state.getGeoAltitude()))
                .addProperty(model.createProperty(PROPERTY_URL + "Heading"), String.valueOf(state.getHeading()))
                .addProperty(model.createProperty(PROPERTY_URL + "Velocity"), String.valueOf(state.getVelocity()));
        model.add(model.createStatement(newFlight, model.createProperty(PROPERTY_URL +"HasState"), newState));

        return model;
    }
}

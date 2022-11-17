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

    static final String FLIGHT_URL = "http://www.w3.org/flight#";
    static final String EX_URL = "http://www.w3.org/2022/example#";
    static final String STATE_URL = "http://www.w3.org/state#";
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

        model.setNsPrefix("ex", "http://www.w3.org/2022/example#");
        model.setNsPrefix("state", "http://www.w3.org/state#");
        model.setNsPrefix("flight", "http://www.w3.org/flight#");
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

        Resource newFlight = model.createResource(FLIGHT_URL + state.getIcao24())
                .addProperty(RDF.type, model.createProperty(EX_URL + "Flight"));
        // set the property through method call -> only adds a new value if its not null
        setProperty(newFlight, model, "Country", state.getOriginCountry());
        setProperty(newFlight, model, "Icao24", state.getIcao24());

        Resource newState = model.createResource( STATE_URL + state.getLastPositionUpdate() + state.getIcao24())
                .addProperty(RDF.type, model.createProperty(EX_URL+"State"));
        setProperty(newState, model, "Icao24", state.getIcao24());
        setProperty(newState, model, "Time", String.valueOf(state.getLastPositionUpdate()));
        setProperty(newState, model, "Longitude", String.valueOf(state.getLongitude()));
        setProperty(newState, model, "Latitude", String.valueOf(state.getLatitude()));
        setProperty(newState, model, "AltitudeGeo", String.valueOf(state.getGeoAltitude()));
        setProperty(newState, model, "AltitudeBaro", String.valueOf(state.getBaroAltitude()));
        setProperty(newState, model, "OnGround", String.valueOf(state.isOnGround()));
        setProperty(newState, model, "Heading", String.valueOf(state.getHeading()));
        setProperty(newState, model, "VerticalRateShape", String.valueOf(state.getVerticalRate()));
        setProperty(newState, model, "Velocity", String.valueOf(state.getVelocity()));
        model.add(model.createStatement(newFlight, model.createProperty(EX_URL +"HasState"), newState));

        return model;
    }

    private void setProperty(Resource resource, Model model, String name, String value) {
        if(value != null && !value.equals("") && !value.equals("null")) {
            resource.addProperty(
                    model.createProperty(EX_URL + name),
                    value
            );
        }
    }
}

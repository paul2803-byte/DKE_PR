package com.dke.app.State;

import com.dke.app.RDFService;
import com.dke.app.ValidationService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.opensky.model.StateVector;

import java.util.Collection;
import java.util.List;
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

        RDFService.setPrefixes(model);

        Resource newFlight = model.createResource(RDFService.FLIGHT_URL + state.getIcao24())
                .addProperty(RDF.type, model.createProperty(RDFService.EX_URL + "Flight"));
        // set the property through method call -> only adds a new value if its not null
        RDFService.setProperty(newFlight, model, "Icao24", state.getIcao24());

        Resource newState = model.createResource( RDFService.STATE_URL + state.getLastPositionUpdate() + state.getIcao24())
                .addProperty(RDF.type, model.createProperty(RDFService.EX_URL+"State"));
        RDFService.setProperty(newState, model, "Icao24", state.getIcao24());
        RDFService.setProperty(newState, model, "Time", String.valueOf(state.getLastPositionUpdate()));
        RDFService.setProperty(newState, model, "Country", state.getOriginCountry());
        RDFService.setProperty(newState, model, "Longitude", String.valueOf(state.getLongitude()));
        RDFService.setProperty(newState, model, "Latitude", String.valueOf(state.getLatitude()));
        RDFService.setProperty(newState, model, "AltitudeGeo", String.valueOf(state.getGeoAltitude()));
        RDFService.setProperty(newState, model, "AltitudeBaro", String.valueOf(state.getBaroAltitude()));
        RDFService.setProperty(newState, model, "OnGround", String.valueOf(state.isOnGround()));
        RDFService.setProperty(newState, model, "Heading", String.valueOf(state.getHeading()));
        RDFService.setProperty(newState, model, "VerticalRateShape", String.valueOf(state.getVerticalRate()));
        RDFService.setProperty(newState, model, "Velocity", String.valueOf(state.getVelocity()));
        model.add(model.createStatement(newFlight, model.createProperty(RDFService.PROPERTY_URL +"HasState"), newState));

        return model;
    }
}

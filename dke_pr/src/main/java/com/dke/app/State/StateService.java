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
        Model model = ModelFactory.createDefaultModel();

        RDFService.setPrefixes(model);

        Resource newState = model.createResource( RDFService.STATE_URL + state.getLastPositionUpdate() + state.getIcao24())
                .addProperty(RDF.type, model.createProperty(RDFService.EX_URL+"State"))
                .addProperty(model.createProperty(RDFService.PROPERTY_URL + "Aircraft")
                        , model.createResource(RDFService.FLIGHT_URL + state.getIcao24()));
        RDFService.setProperty(newState, model, "Time", String.valueOf(state.getLastPositionUpdate()));
        RDFService.setProperty(newState, model, "TimeLastContact", String.valueOf(state.getLastContact()));
        RDFService.setProperty(newState, model, "CallSign", String.valueOf(state.getCallsign()));
        RDFService.setProperty(newState, model, "Country", state.getOriginCountry());
        RDFService.setProperty(newState, model, "Longitude", String.valueOf(state.getLongitude()));
        RDFService.setProperty(newState, model, "Latitude", String.valueOf(state.getLatitude()));
        RDFService.setProperty(newState, model, "AltitudeGeo", String.valueOf(state.getGeoAltitude()));
        RDFService.setProperty(newState, model, "AltitudeBaro", String.valueOf(state.getBaroAltitude()));
        RDFService.setProperty(newState, model, "OnGround", String.valueOf(state.isOnGround()));
        RDFService.setProperty(newState, model, "Heading", String.valueOf(state.getHeading()));
        RDFService.setProperty(newState, model, "VerticalRateShape", String.valueOf(state.getVerticalRate()));
        RDFService.setProperty(newState, model, "Velocity", String.valueOf(state.getVelocity()));

        return model;
    }
}

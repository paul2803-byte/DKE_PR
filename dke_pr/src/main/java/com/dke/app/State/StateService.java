package com.dke.app.State;

import com.dke.app.RDFService;
import com.dke.app.ValidationService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class StateService {

    protected abstract OpenSkyStates getStateVectors();

    public List<Model> getStates() {
        // getting a list of state vectors depending on the implementation in the concrete class
        OpenSkyStates states = getStateVectors();
        List<Model> stateModels =  states.getStates().stream()
                .filter(this::overAustria)
                // convert each element in the stream to a graph
                .map(state -> convertToModel(state, states.getTime()))
                .collect(Collectors.toList());

        stateModels.add(getTimeStampModel(states.getTime()));

        return stateModels.stream()
                .filter(ValidationService::validateState)
                .collect(Collectors.toList());

    }

    private Model convertToModel(StateVector state, int timeStamp) {
        Model model = ModelFactory.createDefaultModel();

        RDFService.setPrefixes(model);

        Resource newState = model.createResource( RDFService.STATE_URL + state.getLastPositionUpdate() + state.getIcao24())
                .addProperty(RDF.type, model.createProperty(RDFService.EX_URL+"State"))
                .addProperty(model.createProperty(RDFService.PROPERTY_URL + "aircraft"),
                        model.createResource(RDFService.AIRCRAFT_URL + state.getIcao24()))
                .addProperty(model.createProperty(RDFService.PROPERTY_URL + "timeStamp"),
                        model.createResource(RDFService.TIME_STAMP_URL + timeStamp));
        RDFService.setProperty(newState, model, "timeLastPos", String.valueOf(state.getLastPositionUpdate()));
        RDFService.setProperty(newState, model, "timeLastContact", String.valueOf(state.getLastContact()));
        RDFService.setProperty(newState, model, "callSign", String.valueOf(state.getCallsign()));
        RDFService.setProperty(newState, model, "country", state.getOriginCountry());
        RDFService.setProperty(newState, model, "longitude", String.valueOf(state.getLongitude()));
        RDFService.setProperty(newState, model, "latitude", String.valueOf(state.getLatitude()));
        RDFService.setProperty(newState, model, "altitudeGeo", String.valueOf(state.getGeoAltitude()));
        RDFService.setProperty(newState, model, "altitudeBaro", String.valueOf(state.getBaroAltitude()));
        RDFService.setProperty(newState, model, "onGround", String.valueOf(state.isOnGround()));
        RDFService.setProperty(newState, model, "heading", String.valueOf(state.getHeading()));
        RDFService.setProperty(newState, model, "verticalRateShape", String.valueOf(state.getVerticalRate()));
        RDFService.setProperty(newState, model, "velocity", String.valueOf(state.getVelocity()));

        return model;
    }

    private Model getTimeStampModel(int time) {
        Model model = ModelFactory.createDefaultModel();

        RDFService.setPrefixes(model);

        Resource timeStamp = model.createResource(RDFService.TIME_STAMP_URL+time)
                .addProperty(RDF.type, model.createProperty(RDFService.EX_URL+"TimeStamp"));

        RDFService.setProperty(timeStamp, model, "time", String.valueOf(time));

        return model;
    }

    private boolean overAustria(StateVector state) {
        return state.getLongitude() != null &&
                state.getLatitude() != null &&
                state.getLatitude() < 49.01 &&
                state.getLatitude() > 46.22 &&
                state.getLongitude() > 9.32 &&
                state.getLongitude() < 17.1;
    }
}

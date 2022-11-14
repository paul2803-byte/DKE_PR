package com.dke.app.State;

import org.opensky.api.OpenSkyApi;
import org.opensky.model.StateVector;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class RealStates extends StateService{

    @Override
    public Collection<StateVector> getStateVectors(){
        OpenSkyApi api = new OpenSkyApi();
        Collection<StateVector> allStates = null;
        try {
            allStates = api.getStates(0, null).getStates();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // filtering the states to only get the ones over austria
        return allStates.stream()
                .filter(state -> (state.getLongitude() != null &&
                        state.getLatitude() != null &&
                        state.getLatitude() < 49.01 &&
                        state.getLatitude() > 46.22 &&
                        state.getLongitude() > 9.32 &&
                        state.getLongitude() < 17.1))
                .collect(Collectors.toList());
    }
}

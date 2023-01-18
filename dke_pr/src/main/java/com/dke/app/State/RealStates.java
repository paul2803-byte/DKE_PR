package com.dke.app.State;

import org.opensky.api.OpenSkyApi;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class RealStates extends StateService{

    @Override
    public OpenSkyStates getStateVectors(){
        OpenSkyApi api = new OpenSkyApi();
        OpenSkyStates allStates = null;
        try {
            allStates = api.getStates(0, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // filtering the states to only get the ones over austria
        return allStates;
    }
}

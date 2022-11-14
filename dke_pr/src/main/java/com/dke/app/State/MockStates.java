package com.dke.app.State;

import org.opensky.model.StateVector;

import java.util.ArrayList;
import java.util.Collection;

public class MockStates extends StateService{

    @Override
    public Collection<StateVector> getStateVectors() {
        // TODO: implement mocking of states
        StateVector testVector = new StateVector("111111");
        testVector.setOriginCountry("Austria");
        Collection<StateVector> mockData = new ArrayList<>();
        mockData.add(testVector);
        return mockData;
    }
}

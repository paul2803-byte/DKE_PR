package com.dke.app.State;

import org.opensky.model.StateVector;

import java.util.ArrayList;
import java.util.Collection;

public class MockStates extends StateService{

    @Override
    public Collection<StateVector> getStateVectors() {
        // TODO: implement mocking of states
        Collection<StateVector> mockData = new ArrayList<>();
        mockData.add(createMockedVector());
        return mockData;
    }

    private StateVector createMockedVector() {
        StateVector testVector = new StateVector("111111");
        testVector.setOriginCountry("Austria");
        testVector.setLastPositionUpdate(1600000000.0);
        testVector.setLongitude(160.0);
        testVector.setLatitude(000.0);
        testVector.setGeoAltitude(1000.0);
        testVector.setOnGround(false);
        testVector.setVelocity(16000.0);
        testVector.setVerticalRate(33333.2);
        return testVector;
    }
}

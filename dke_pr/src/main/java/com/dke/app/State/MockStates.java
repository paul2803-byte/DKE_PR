package com.dke.app.State;

import org.opensky.model.StateVector;

import java.util.ArrayList;
import java.util.Collection;

public class MockStates extends StateService{

    @Override
    public Collection<StateVector> getStateVectors() {
        Collection<StateVector> mockData = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            mockData.add(createMockedVector(i));
        }
        return mockData;
    }

    private StateVector createMockedVector(int i) {

        StateVector testVector = new StateVector("10000"+i);
        testVector.setOriginCountry("Austria");
        testVector.setLastPositionUpdate(150000000 + (Math.random()*(1600000000 - 1500000000)));
        testVector.setLongitude(Math.round((46.3775998 + (Math.random() * (49.020703 - 46.377598)))*10000.0)/10000.0);
        testVector.setLatitude(Math.round((9.478079 + (Math.random()*(17.1601461 - 9.478079)))*10000.0)/10000.0);
        testVector.setGeoAltitude(Math.round((1000+ (Math.random()*(16000-1000)))*100.0)/100.0);
        testVector.setOnGround(false);
        testVector.setVelocity(Math.round((83.3333 + (Math.random()*(305.5556 - 83.3333)))*100.0)/100.0);
        testVector.setVerticalRate(Math.round((-5 + (Math.random()*(5 - -5)))*100.0)/100.0);

        return testVector;
    }
}

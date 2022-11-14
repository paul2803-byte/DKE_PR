package com.dke.app.State;

import org.apache.jena.graph.Graph;
import java.util.List;

public abstract class StateService {

    public abstract List<Graph> getStates();
}

package com.dke.app;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public class RDFService {

    static public final String AIRCRAFT_URL = "http://www.dke.uni-linz.ac.at/pr-dke/flight#";
    static public final String TIME_STAMP_URL = "http://www.dke.uni-linz.ac.at/pr-dke/time_stamp#";
    static public final String EX_URL = "http://www.w3.org/2022/example#";
    static public final String STATE_URL = "http://www.dke.uni-linz.ac.at/pr-dke/state#";
    static public final String MANUFACTURER_URL = "http://www.dke.uni-linz.ac.at/pr-dke/manufacturer#";
    static public final String PROPERTY_URL = "http://www.dke.uni-linz.ac.at/pr-dke/property#";
    static public final String RDF_URL = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    public static void setProperty(Resource resource, Model model, String name, String value) {
        if (value != null && !value.equals("") && !value.equals("null")) {
            resource.addProperty(
                    model.createProperty(PROPERTY_URL + name),
                    value
            );
        }
    }

    public static void setPrefixes(Model model) {
        model.setNsPrefix("ex", EX_URL);
        model.setNsPrefix("state", STATE_URL);
        model.setNsPrefix("flight", AIRCRAFT_URL);
        model.setNsPrefix("rdf", RDF_URL);
        model.setNsPrefix("property", PROPERTY_URL);
        model.setNsPrefix("manufacturer", MANUFACTURER_URL);
        model.setNsPrefix("time_stamp", TIME_STAMP_URL);
    }
}

package com.dke.app;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.jena.graph.Graph;
import org.apache.jena.riot.Lang;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;
import org.opensky.api.OpenSkyApi;
import org.opensky.model.StateVector;
import org.apache.jena.shacl.Shapes;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public class App 
{
    public static void main( String[] args )
    {
        List<String[]> staticData= getStaticData();
        //storeFlights(getFlights(new String[]{"4b1815", "4b1817"}));

        storeFlights(getDataList(getDynamicData(new String[]{"4b1815", "4b1817"}), getStaticData()));


        // testShacl();
    }

    private static List<String[]> getDataList(Collection<StateVector> dynamicData, List<String[]> staticData){
        List<String[]> flightsList = new ArrayList<>();

        dynamicData.forEach(flight -> {
            flightsList.add(new String[]{flight.getIcao24(),flight.getOriginCountry(),String.valueOf(flight.getLongitude()),
                                String.valueOf(flight.getLatitude()),String.valueOf(flight.getGeoAltitude()),
                                String.valueOf(flight.getHeading()),String.valueOf(flight.getVelocity())});
        });

        List<String[]> staticAndDynamicFlightData= new ArrayList<>();
        List<String[]> staticFlights=staticData;
        for(String [] a : staticFlights){
            boolean alreadyAssigned=false;
            String icao= a[0];
            for(String [] i : flightsList){
                if(i[0].equals(icao)){
                    int length= a.length+(i.length-1);
                    String[] data = new String[length];
                    for(int j=0; j< a.length; j++ ){
                        data[j]=a[j];
                    }
                    for(int j=0; j<i.length-1; j++){
                        data[a.length+j]=i[j+1];
                    }
                    staticAndDynamicFlightData.add(data);
                    alreadyAssigned=true;
                    break;
                }
            }
            if(!alreadyAssigned){
                int length= a.length+(flightsList.get(0).length-1);
                String[] data = new String[length];
                for(int j=0; j< length; j++ ){
                    if(j<a.length){
                        data[j]=a[j];
                    }
                    else{
                        data[j]=""; //keine Ahnung ob "null" besser wäre
                    }
                }
                staticAndDynamicFlightData.add(data);
            }
        }

        return staticAndDynamicFlightData;
    }

    private static Collection<StateVector> getDynamicData(String[] flights) {
        OpenSkyApi api = new OpenSkyApi();
        String[] wishedFlights = new String[]{"abc0e4", "4b1900", "a9c380"};
        try {
            return api.getStates(0, null).getStates();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String[]> getStaticData(){
        List<String[]> r = new LinkedList<>();
        try (CSVReader reader = new CSVReader(new FileReader("dke_pr/staticData/aircraftDatabase.csv"))){
            while(reader.readNext()!=null){
                String[] i = reader.readNext();
                if(i != null){
                    if(i[1]!= null){
                        if(i[1].length()!=0){
                            if(/*i[1].charAt(0)=='D'||*/(i[1].charAt(0)=='O'&& i[1].charAt(1)=='E')||(i[1].charAt(0)=='C'&&i[1].charAt(1)=='H')){
                                //r.add(i);
                                String[] values = new String[14];
                                for(int j=0; j<7; j++){
                                    values[j]=i[j];
                                }
                                values[7]=i[8];
                                values[8]=i[13];
                                values[9]=i[15];
                                values[10]=i[16];
                                values[11]=i[18];
                                values[12]=i[21];
                                values[13]=i[26];
                                r.add(values);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }
        return r;
    }

    private static void storeFlights(Collection<StateVector> flights) {
        Model model = ModelFactory.createDefaultModel();
        final String FLIGHT_URL = "http://somewhere/";
        final String STATE_URL = "http://somewhere/state/";
        final String PROPERTY_URL = "http://somewhere/property/";
        flights.forEach(flight -> {
            System.out.println(flight);
            System.out.println(flight.getIcao24());
            Resource newFlight = model.createResource(FLIGHT_URL + flight.getIcao24())
                    .addProperty(model.createProperty(PROPERTY_URL + "#Country"), flight.getOriginCountry());

            Resource newState = model.createResource( STATE_URL + flight.getLastPositionUpdate() + flight.getIcao24())
                    .addProperty(model.createProperty(PROPERTY_URL + "#Longitude"), String.valueOf(flight.getLongitude()))
                    .addProperty(model.createProperty(PROPERTY_URL + "#Latitude"), String.valueOf(flight.getLatitude()))
                    .addProperty(model.createProperty(PROPERTY_URL + "#Altitude"), String.valueOf(flight.getGeoAltitude()))
                    .addProperty(model.createProperty(PROPERTY_URL + "#Heading"), String.valueOf(flight.getHeading()))
                    .addProperty(model.createProperty(PROPERTY_URL + "#Velocity"), String.valueOf(flight.getVelocity()));

            model.add(model.createStatement(newFlight, model.createProperty(PROPERTY_URL +"#hasState"), newState));
        });
        model.write(System.out);
        // TODO: later use this to directly validate with shacl shape
        model.getGraph();

    }

    private static void storeFlights(List<String[]> r){
        Model model = ModelFactory.createDefaultModel();
        final String PROPERTY_URL= "http://www.dke.uni-linz.ac.at/pr-dke/";
        final String AIRCRAFT_URL= "http://www.dke.uni-linz.ac.at/pr-dke/aircraft/";
        final String MANUFACTURER_URL= "http://www.dke.uni-linz.ac.at/pr-dke/manufacturer/";
        for(String[] a : r)
        {
            Resource airplane = model.createResource(AIRCRAFT_URL+a[0])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Icao24"),a[0])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Registration"),a[1])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Model"),a[4])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Typecode"),a[5])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Serialnumber"),a[6])
                    .addProperty(model.createProperty(PROPERTY_URL+"#IcaoAircraftType"),a[7])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Owner"), a[8])
                    .addProperty(model.createProperty(PROPERTY_URL+"#RegisteredSince"),a[9])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Reguntil"),a[10])
                    .addProperty(model.createProperty(PROPERTY_URL+"#WasBuilt"),a[11])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Engine"),a[12])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Description"),a[13])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Country"),a[14])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Longitude"),a[15])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Latitude"),a[16])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Altitude"),a[17])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Heading"),a[18])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Velocity"),a[19]);

            Resource manufacturer= model.createResource(MANUFACTURER_URL+a[2])
                    .addProperty(model.createProperty(PROPERTY_URL+"#ManufacturerIcao"),a[2])
                    .addProperty(model.createProperty(PROPERTY_URL+"#ManufacturerName"),a[3]);

            model.add(model.createStatement(airplane, model.createProperty(PROPERTY_URL+"#hasManufacturer"),manufacturer));
        }
        //model.write(System.out, "Turtle");
        model.write(System.out);
    }



    private static void testShacl() {
        // TODO: find the place to out the shacl shapes
        String SHAPES = "shape_graph_test.ttl";
        String DATA = "data_graph_test.tll";

        Graph shapesGraph = RDFDataMgr.loadGraph(SHAPES);
        Graph dataGraph = RDFDataMgr.loadGraph(DATA);

        Shapes shapes = Shapes.parse(shapesGraph);

        ValidationReport report = ShaclValidator.get().validate(shapes, dataGraph);
        ShLib.printReport(report);
        System.out.println();
        RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
    }
}

package com.dke.app;

import com.dke.app.State.MockStates;
import com.dke.app.State.RealStates;
import com.dke.app.State.StateService;
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

import java.util.Scanner;

public class App 
{
    public static void main( String[] args )
    {
        Scanner scanner = new Scanner(System.in);
        // check if the states should be read through real or mock data
        System.out.print("Should real or mock data be used? Type in m for mock or r for real: ");
        String input = scanner.nextLine();
        while (!input.equals("r") && !input.equals("m")) {
            System.out.print("Not a valid option. Type in m for mock data or r for real data: ");
            input = scanner.nextLine();
        }
        boolean mockData = input.equals("m");

        // inform the user about the mode
        if(mockData) {
            System.out.println("Mock data gets used");
        } else {
            System.out.println("Real data gets used");
        }

        // read in the static data and store it to the knowledge graph
        System.out.println("Reading the static data");
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


        // let the user read in new data or terminate the application
        askForNewStates(mockData, scanner);
    }

    private static void askForNewStates(boolean mockData, Scanner scanner) {
        while(true) {
            System.out.print("Enter r to read new states or e to exit: ");
            String input = scanner.nextLine();
            if(input.equals("r")){
                System.out.println("Reading new states");
                storeStates(mockData);
                System.out.println("New states got stored");
            } else if (input.equals("e")){
                System.out.println("Exiting the application");
                break;
            } else {
                System.out.println("Symbol not found");
            }
        }
    }

    private static void storeStates(boolean mockData){
        StateService stateService;
        if(mockData) {
            stateService = new MockStates();
        } else {
            stateService = new RealStates();
        }
        StorageService.storeStates(stateService.getStates());
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
                    .addProperty(model.createProperty(PROPERTY_URL+"#Manufacturer"),model.createResource(MANUFACTURER_URL+a[2])
                            .addProperty(model.createProperty(PROPERTY_URL+"#ManufacturerIcao"),a[2])
                            .addProperty(model.createProperty(PROPERTY_URL+"#ManufacturerName"),a[3]));

            /*Resource manufacturer= model.createResource(MANUFACTURER_URL+a[2])
                    .addProperty(model.createProperty(PROPERTY_URL+"#ManufacturerIcao"),a[2])
                    .addProperty(model.createProperty(PROPERTY_URL+"#ManufacturerName"),a[3]);

            model.add(model.createStatement(airplane, model.createProperty(PROPERTY_URL+"#Manufacturer"),manufacturer));*/
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

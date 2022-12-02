package com.dke.app;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AircraftService {

    public static List<Model> getAircrafts() {
        // getting a list of state vectors depending on the implementation in the concrete class
        return getStaticData().stream()
                // convert each element in the stream to a graph
                .map(AircraftService::convertToModel)
                // validate each element in the stream with the shacl shape and only use return the valid ones
                .filter(ValidationService::validateAircraft)
                // convert stream to list
                .collect(Collectors.toList());
    }


    private static List<String[]> getStaticData (){
        List<String[]> result= new LinkedList<>();
            try {
                FileReader filereader = new FileReader("dke_pr/staticData/aircraftDatabase.csv");
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withSkipLines(1)
                        .build();
                List<String[]> allData = csvReader.readAll();

                for(String[] data : allData){
                    if(data != null && data[1]!=null &&data[1].length()!=0){
                        if((data[1].charAt(0)=='D'&&data[1].charAt(1)=='-')||(data[1].charAt(0)=='O'&&data[1].charAt(1)=='E'&&data[1].charAt(2)=='-')||(data[1].charAt(0)=='C'&&data[1].charAt(1)=='H'&&data[1].charAt(2)=='-')){
                            data[2]= data[2].replace(" ", "");data[2]= data[2].replace("'", "");
                            data[2]= data[2].replace("&", "AND");data[2]= data[2].replace("(", "");
                            data[2]= data[2].replace(")", "");
                            String[] values = new String[14];
                            for(int l=0; l<7;l++) {
                                values[l] = data[l];
                            }
                            values[7] = data[8];
                            values[8] = data[13];
                            values[9] = data[15];
                            values[10] = data[16];
                            values[11] = data[18];
                            values[12] = data[21];
                            values[13] = data[26];
                            result.add(values);
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return result;
    }

    private static Model convertToModel(String[] r){
        Model model = ModelFactory.createDefaultModel();
        RDFService.setPrefixes(model);
        Resource aircraft = model.createResource(RDFService.AIRCRAFT_URL+r[0])
                    .addProperty(RDF.type, model.createProperty(RDFService.EX_URL + "Flight"));
        RDFService.setProperty(aircraft, model, "icao24",r[0]);
        RDFService.setProperty(aircraft, model, "registration",r[1]);
        RDFService.setProperty(aircraft, model, "model",r[4]);
        RDFService.setProperty(aircraft, model, "typecode",r[5]);
        RDFService.setProperty(aircraft, model, "serialnumber",r[6]);
        RDFService.setProperty(aircraft, model, "icaoAircraftType",r[7]);
        RDFService.setProperty(aircraft, model, "owner",r[8]);
        RDFService.setProperty(aircraft, model, "registeredSince",r[9]);
        RDFService.setProperty(aircraft, model, "registeredUntil",r[10]);
        RDFService.setProperty(aircraft, model, "wasBuilt",r[11]);
        RDFService.setProperty(aircraft, model, "engine",r[12]);
        RDFService.setProperty(aircraft, model, "description",r[13]);
        if(r[2] != null && !r[2].equals("")) {
            Resource manufacturer = model.createResource(RDFService.MANUFACTURER_URL+r[2])
                    .addProperty(RDF.type, model.createProperty(RDFService.EX_URL + "Manufacturer"));
            RDFService.setProperty(manufacturer, model, "manufacturerIcao", r[2]);
            RDFService.setProperty(manufacturer, model, "manufacturerName", r[3]);
            model.add(model.createStatement(aircraft, model.createProperty(RDFService.PROPERTY_URL + "manufacturer"), manufacturer));
        }

        return model;
    }

    // function to test out the composition of flight and state
    public static List<Model> getMockFlight() {
        List<Model> models = new LinkedList<>();
        Model model = ModelFactory.createDefaultModel();

        RDFService.setPrefixes(model);

        model.createResource(RDFService.AIRCRAFT_URL+ "111111")
                .addProperty(RDF.type, model.createProperty(RDFService.EX_URL + "Flight"))
                .addProperty(model.createProperty(RDFService.PROPERTY_URL+"Icao24"),"111111")
                .addProperty(model.createProperty(RDFService.PROPERTY_URL+"Registration"),"whatever")
                .addProperty(model.createProperty(RDFService.PROPERTY_URL+"Model"),"some Model")
                .addProperty(model.createProperty(RDFService.PROPERTY_URL+"Typecode"), "111111");
        models.add(model);
        return  models;
    }
}

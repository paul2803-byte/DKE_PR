package com.dke.app;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.io.FileReader;
import java.io.IOException;
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

    private static List<String[]> getStaticData () {
        List<String[]> r = new LinkedList<>();
        ;
        try (CSVReader reader = new CSVReader(new FileReader("dke_pr/staticData/aircraftDatabase.csv"))) {
            /*int row_count=0;
            CSVReader reader2=reader;
            while(reader.readNext() != null){
                row_count++;
                reader2.readNext();
            }
            System.out.println(row_count+" reader");*/
            while (reader.readNext() != null) {
                String[] i = reader.readNext();
                if (i != null && i[0]!= null && i[1] != null && i[1].length() !=0) {
                            if ((i[1].charAt(0)=='D' && i[1].charAt(1) == '-')||(i[1].charAt(0) == 'O' && i[1].charAt(1) == 'E' && i[1].charAt(2) == '-') || (i[1].charAt(0) == 'C' && i[1].charAt(1) == 'H' && i[1].charAt(2) == '-')) {
                                String[] values = new String[14];
                                i[2]= i[2].replace(" ", "");
                                i[2]= i[2].replace("'", "");
                                for (int j = 0; j < 7; j++) {
                                    values[j] = i[j];
                                }
                                values[7] = i[8];
                                values[8] = i[13];
                                values[9] = i[15];
                                values[10] = i[16];
                                values[11] = i[18];
                                values[12] = i[21];
                                values[13] = i[26];
                                r.add(values);
                            }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }
        System.out.println(r.size());
        return r;
    }


    private static Model convertToModel(String[] r){
        Model model = ModelFactory.createDefaultModel();
        RDFService.setPrefixes(model);
        Resource aircraft = model.createResource(RDFService.FLIGHT_URL+r[0])
                    .addProperty(RDF.type, model.createProperty(RDFService.EX_URL + "Flight"));
        RDFService.setProperty(aircraft, model, "Icao24",r[0]);
        RDFService.setProperty(aircraft, model, "Registration",r[1]);
        RDFService.setProperty(aircraft, model, "Model",r[4]);
        RDFService.setProperty(aircraft, model, "Typecode",r[5]);
        RDFService.setProperty(aircraft, model, "Serialnumber",r[6]);
        RDFService.setProperty(aircraft, model, "IcaoAircraftType",r[7]);
        RDFService.setProperty(aircraft, model, "Owner",r[8]);
        RDFService.setProperty(aircraft, model, "RegisteredSince",r[9]);
        RDFService.setProperty(aircraft, model, "RegisteredUntil",r[10]);
        RDFService.setProperty(aircraft, model, "WasBuilt",r[11]);
        RDFService.setProperty(aircraft, model, "Engine",r[12]);
        RDFService.setProperty(aircraft, model, "Description",r[13]);
        if(r[2] != null && !r[2].equals("")) {
            Resource manufacturer = model.createResource(RDFService.MANUFACTURER_URL+r[2])
                    .addProperty(RDF.type, model.createProperty(RDFService.EX_URL + "Manufacturer"));
            RDFService.setProperty(manufacturer, model, "ManufacturerIcao", r[2]);
            RDFService.setProperty(manufacturer, model, "ManufacturerName", r[3]);
            model.add(model.createStatement(aircraft, model.createProperty(RDFService.PROPERTY_URL +"Manufacturer"), manufacturer));
        }

        return model;
    }

    // function to test out the composition of flight and state
    public static List<Model> getMockFlight() {
        List<Model> models = new LinkedList<>();
        Model model = ModelFactory.createDefaultModel();

        RDFService.setPrefixes(model);

        model.createResource(RDFService.FLIGHT_URL+ "111111")
                .addProperty(RDF.type, model.createProperty(RDFService.EX_URL + "Flight"))
                .addProperty(model.createProperty(RDFService.PROPERTY_URL+"Icao24"),"111111")
                .addProperty(model.createProperty(RDFService.PROPERTY_URL+"Registration"),"whatever")
                .addProperty(model.createProperty(RDFService.PROPERTY_URL+"Model"),"some Model")
                .addProperty(model.createProperty(RDFService.PROPERTY_URL+"Typecode"), "111111");
        models.add(model);
        return  models;
    }
}

package com.dke.app;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AircraftService {
    private static final String EX_URL = "http://www.dke.uni-linz.ac.at/pr-dke/";

    public List<Model> getAircrafts() {
        // getting a list of state vectors depending on the implementation in the concrete class
        return getStaticData().stream()
                // convert each element in the stream to a graph
                .map(AircraftService::storeFlights)
                // validate each element in the stream with the shacl shape and only use return the valid ones
                .filter(ValidationService::validateState)
                // convert stream to list
                .collect(Collectors.toList());
    }

    private static List<String[]> getStaticData () {
        List<String[]> r = new LinkedList<>();
        try (CSVReader reader = new CSVReader(new FileReader("dke_pr/staticData/aircraftDatabase.csv"))) {
            while (reader.readNext() != null) {
                String[] i = reader.readNext();
                if (i != null) {
                    if (i[1] != null) {
                        if (i[1].length() != 0) {
                            if (/*i[1].charAt(0)=='D'||*/(i[1].charAt(0) == 'O' && i[1].charAt(1) == 'E') || (i[1].charAt(0) == 'C' && i[1].charAt(1) == 'H')) {
                                //r.add(i);
                                String[] values = new String[14];
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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }
        return r;
    }

    private static Model storeFlights(String[] r){
        Model model = ModelFactory.createDefaultModel();
        final String PROPERTY_URL= "http://www.dke.uni-linz.ac.at/pr-dke/";
        final String AIRCRAFT_URL= "http://www.dke.uni-linz.ac.at/pr-dke/aircraft/";
        final String MANUFACTURER_URL= "http://www.dke.uni-linz.ac.at/pr-dke/manufacturer/";
        Resource airplane = model.createResource(AIRCRAFT_URL+r[0])
                //TODO addProperty through method call
                    .addProperty(model.createProperty(PROPERTY_URL+"#Icao24"),r[0])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Registration"),r[1])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Model"),r[4])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Typecode"),r[5])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Serialnumber"),r[6])
                    .addProperty(model.createProperty(PROPERTY_URL+"#IcaoAircraftType"),r[7])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Owner"), r[8])
                    .addProperty(model.createProperty(PROPERTY_URL+"#RegisteredSince"),r[9])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Reguntil"),r[10])
                    .addProperty(model.createProperty(PROPERTY_URL+"#WasBuilt"),r[11])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Engine"),r[12])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Description"),r[13])
                    .addProperty(model.createProperty(PROPERTY_URL+"#Manufacturer"),model.createResource(MANUFACTURER_URL+r[2])
                            .addProperty(model.createProperty(PROPERTY_URL+"#ManufacturerIcao"),r[2])
                            .addProperty(model.createProperty(PROPERTY_URL+"#ManufacturerName"),r[3]));

            /*Resource manufacturer= model.createResource(MANUFACTURER_URL+a[2])
                    .addProperty(model.createProperty(PROPERTY_URL+"#ManufacturerIcao"),a[2])
                    .addProperty(model.createProperty(PROPERTY_URL+"#ManufacturerName"),a[3]);

            model.add(model.createStatement(airplane, model.createProperty(PROPERTY_URL+"#Manufacturer"),manufacturer));*/
        //model.write(System.out, "Turtle");
        return model;
    }

    private void setProperty(Resource resource, Model model, String name, String value) {
        if(value != null && !value.equals("") && !value.equals("null")) {
            resource.addProperty(
                    model.createProperty(EX_URL + name),
                    value
            );
        }
    }

}

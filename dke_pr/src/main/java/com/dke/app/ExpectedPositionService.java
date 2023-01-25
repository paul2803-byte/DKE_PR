package com.dke.app;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.topbraid.shacl.rules.RuleUtil;

import java.util.List;

public class ExpectedPositionService {

    public static Model checkOld(List<Model> modelList){

        Model shapesModel = RDFDataMgr.loadModel("dke_pr/shacl_shapes/expected_position.ttl");
        System.out.println(modelList.size());
        // RDFDataMgr.write(System.out, shapesModel, Lang.TTL);
        Model dataModel = ModelFactory.createDefaultModel();
        modelList.stream().forEach(model -> dataModel.add(model));

        Model report = RuleUtil.executeRules(dataModel, shapesModel, null, null);

        // Print violations
        RDFDataMgr.write(System.out, report, Lang.TTL);

        return report;
    }
}

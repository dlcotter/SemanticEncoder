package encoder;

import ca.uhn.fhir.context.FhirContext;
import org.apache.jena.rdf.model.Model;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.ArrayList;
import java.util.List;

public class FHIREncounterEncoder extends Encoder {
    public FHIREncounterEncoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    public List<Model> buildModel(String message) {
        FhirContext ctx = FhirContext.forDstu3();
        IBaseResource resource = ctx.newXmlParser().parseResource(message);

        if (!(resource instanceof Encounter))
            return new ArrayList<>();

        ArrayList<Model> models = new ArrayList<>();

        Encounter FHIREncounter = (Encounter) resource;
        domain.Encounter commonEncounter = new domain.Encounter();
        commonEncounter.identifier = FHIREncounter.getIdentifier().get(0).getValue();
        models.add(encodeEncounter(commonEncounter));

        return models;
    }
}

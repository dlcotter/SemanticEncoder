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
        ArrayList<Model> models = new ArrayList<>();
        FhirContext ctx = FhirContext.forDstu3();
        IBaseResource resource = ctx.newXmlParser().parseResource(message);

        if (!(resource instanceof Encounter))
            return models;

        Encounter encounterResource = (Encounter) resource;
        domain.Encounter encounter = new domain.Encounter();
        encounter.identifier = encounterResource.getIdentifier().get(0).getValue();
        encounter.patientIdentifier = encounterResource.getSubject().getReference();

        models.add(encodeEncounter(encounter));

        return models;
    }
}

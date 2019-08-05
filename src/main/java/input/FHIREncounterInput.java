package input;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;

import java.util.Date;
import java.util.List;

public class FHIREncounterInput extends Input {
    private List<domain.Encounter> sampleEncounters;
    private int sampleEncounterCount;

    public FHIREncounterInput(String outputTopicName) {
        super(outputTopicName);

        // Initialize sample patients list and counter
        sampleEncounters = domain.Encounter.getSampleEncounters();
        sampleEncounterCount = sampleEncounters.size();

        // Iterate through list, i.e. repeat, until list is exhausted
        this.setRepeat(true);
    }

    @Override
    public String getNextMessage() {
        // Stop after the sample list of ten patients runs out
        if (sampleEncounterCount == 0)
            return "";

        String message = "";
        try {
            domain.Encounter encounter = sampleEncounters.get(sampleEncounterCount-1);
            sampleEncounterCount--;
            message = this.generateEncounterFHIR(encounter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    private String generateEncounterFHIR(domain.Encounter encounter) {
        Encounter encounterResource = new Encounter();
        encounterResource.setId(encounter.identifier);
        encounterResource.addIdentifier()
                    .setSystem("FDS-EncounterId")
                    .setValue(encounter.identifier);
        encounterResource.setStatus(Encounter.EncounterStatus.INPROGRESS);
        encounterResource.setClass_(new Coding("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP", "inpatient encounter"));
        encounterResource.getPeriod()
                    .setStart(new Date())
                    .setEnd(new Date());
        encounterResource.setSubject(new Reference("Patient/" + encounter.patientIdentifier));

        // Serialize the message
        FhirContext ctx = FhirContext.forDstu3();
        String output = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(encounterResource);

        return output;
    }
}

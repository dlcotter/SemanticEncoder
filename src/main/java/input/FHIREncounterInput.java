package input;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.Encounter;

import java.util.Date;

public class FHIREncounterInput extends Input {
    public FHIREncounterInput(String outputTopicName) {
        super(outputTopicName);
    }

    @Override
    public String getNextMessage() {
        Encounter encounter = new Encounter();
        encounter.setId("1961851");
        encounter.addIdentifier()
                    .setSystem("FDS-EncounterId")
                    .setValue("0123456789");
        encounter.setStatus(Encounter.EncounterStatus.FINISHED);
        encounter.addType()
                    .setText("Encounter for problem (procedure)")
                    .addCoding()
                        .setSystem("http://snomed.info/sct")
                        .setCode("185347001")
                        .setDisplay("Encounter for problem (procedure)");
        encounter.getSubject()
                    .setReference("Patient/1961849")
                    .setDisplay("Sample Test");
        encounter.addParticipant()
                    .getIndividual()
                        .setReference("Practitioner/1961850")
                        .setDisplay("Amanda Applegate");
        encounter.getPeriod()
                    .setStart(new Date("06/14/2019"))
                    .setEnd(new Date("06/14/2019"));
        encounter.addReason()
                .setText("Medication education (procedure)")
                .addCoding()
                    .setSystem("http://snomed.info/sct")
                    .setCode("967006")
                    .setDisplay("Medication education (procedure)");

        // Serialize the message
        FhirContext ctx = FhirContext.forDstu3();
        String output = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(encounter);

        return output;
    }
}

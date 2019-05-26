package input;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_PATIENT;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_PATIENT_RESULT;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_VISIT;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.PV1;

import java.io.IOException;
import java.time.LocalDateTime;

public class HL7VitalSignsInput extends Input {
    public HL7VitalSignsInput(String outputTopicName) {
        super(outputTopicName);
    }

    @Override
    String getNextMessage() {
        String message = "";

        try {
            message = generateMessage();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    /**
     * We are going to create an ORU_R01 message, for the purpose of demonstrating the creation and
     * population of an OBX segment.
     *
     * The following message snippet is drawn (and modified for simplicity)
     * from section 7.4.2.4 of the HL7 2.5 specification.
     *
     * <code>
     * OBR|1||1234^LAB|88304
     * OBX|1|CE|88304|1|T57000^GALLBLADDER^SNM
     * OBX|2|TX|88304|1|THIS IS A NORMAL GALLBLADDER
     * OBX|3|TX|88304&MDT|1|MICROSCOPIC EXAM SHOWS HISTOLOGICALLY NORMAL GALLBLADDER TISSUE
     * </code>
     *
     * The following code attempts to generate this message structure.
     *
     * The HL7 spec defines the following structure for an ORU^R01 message, represented in HAPI by
     * the segment group:
     *
     * <code>
     *                     ORDER_OBSERVATION start
     *       {
     *       [ ORC ]
     *       OBR
     *       [ { NTE } ]
     *                     TIMING_QTY start
     *          [{
     *          TQ1
     *          [ { TQ2 } ]
     *          }]
     *                     TIMING_QTY end
     *       [ CTD ]
     *                     OBSERVATION start
     *          [{
     *          OBX
     *          [ { NTE } ]
     *          }]
     *                     OBSERVATION end
     *       [ { FT1 } ]
     *       [ { CTI } ]
     *                     SPECIMEN start
     *          [{
     *          SPM
     *          [ { OBX } ]
     *          }]
     *                     SPECIMEN end
     *       }
     *                     ORDER_OBSERVATION end
     * </code>
     *
     * @throws HL7Exception
     *             If any processing problem occurs
     * @throws IOException
     *             If any processing problem occurs
     */
    private String generateMessage() throws HL7Exception, IOException {
        // First, a message object is constructed. The initQuickstart method
        // populates all of the mandatory fields in the MSH segment of the
        // message, including the message type, the timestamp, and the control ID.
        ORU_R01 message = new ORU_R01();
        message.initQuickstart("ORU", "R01", "T");

        ORU_R01_PATIENT_RESULT patientResultGroup = message.getPATIENT_RESULT();
        ORU_R01_PATIENT patientGroup = patientResultGroup.getPATIENT();

        PID pidSegment = patientGroup.getPID();
        pidSegment.getPid1_SetIDPID().setValue("123456789");
        pidSegment.getPid3_PatientIdentifierList(0).getCx1_IDNumber().setValue("123456789");
        pidSegment.getPid5_PatientName(0).getXpn1_FamilyName().getFn1_Surname().setValue("Candy");
        pidSegment.getPid5_PatientName(0).getXpn2_GivenName().setValue("John");
        pidSegment.getPid7_DateTimeOfBirth().getTs1_Time().setValue("19501031");
        pidSegment.getPid8_AdministrativeSex().setValue("male");

        ORU_R01_VISIT visitGroup = patientGroup.getVISIT();
        PV1 pv1Segment = visitGroup.getPV1();
        pv1Segment.getPv13_AssignedPatientLocation().getPl3_Bed().setValue("121A");
        pv1Segment.getPv13_AssignedPatientLocation().getPl7_Building().setValue("A. B. Chandler");
        pv1Segment.getPv13_AssignedPatientLocation().getPl8_Floor().setValue("8");

        /*
         * The OBR segment is contained within a group called ORDER_OBSERVATION,
         * which is itself in a group called PATIENT_RESULT. These groups are
         * reached using named accessors.
         */
        ORU_R01_ORDER_OBSERVATION orderObservationGroup = patientResultGroup.getORDER_OBSERVATION();

        // Populate the OBR
        OBR obrSegment = orderObservationGroup.getOBR();
        obrSegment.getObr1_SetIDOBR().setValue("1376793");
        obrSegment.getObr4_UniversalServiceIdentifier().getCe1_Identifier().setValue("85354-9");
        obrSegment.getObr4_UniversalServiceIdentifier().getCe2_Text().setValue("Blood pressure panel with all children optional");
        obrSegment.getObr4_UniversalServiceIdentifier().getCe3_NameOfCodingSystem().setValue("LN");
        obrSegment.getObr7_ObservationDateTime().getTs1_Time().setValue(LocalDateTime.now().toString()); // .format(DateTimeFormatter.ofPattern("YYYYMMDDHHMM") wasn't working

        /*
         * The OBX segment is in a repeating group called OBSERVATION. You can
         * use a named accessor which takes an index to access a specific
         * repetition. You can ask for an index which is equal to the
         * current number of repetitions,and a new repetition will be created.
         */

        OBX obxSegment1 = orderObservationGroup.getOBSERVATION(0).getOBX();
        obxSegment1.getObx1_SetIDOBX().setValue("1");
        obxSegment1.getObx2_ValueType().setValue("CE"); // Coded Element
        obxSegment1.getObx3_ObservationIdentifier().getCe1_Identifier().setValue("8480-6");
        obxSegment1.getObx3_ObservationIdentifier().getCe2_Text().setValue("Systolic blood pressure");
        obxSegment1.getObx3_ObservationIdentifier().getCe3_NameOfCodingSystem().setValue("LN");

        CE codedElement1 = new CE(message);
        codedElement1.getCe1_Identifier().setValue("107");
        codedElement1.getCe2_Text().setValue("mm[Hg]");
        codedElement1.getCe3_NameOfCodingSystem().setValue("UOM"); // http://unitsofmeasure.org
        obxSegment1.getObx5_ObservationValue(0).setData(codedElement1);

        OBX obxSegment2 = orderObservationGroup.getOBSERVATION(1).getOBX();
        obxSegment2.getObx1_SetIDOBX().setValue("2");
        obxSegment2.getObx2_ValueType().setValue("CE"); // Coded Element
        obxSegment2.getObx3_ObservationIdentifier().getCe1_Identifier().setValue("8462-4");
        obxSegment2.getObx3_ObservationIdentifier().getCe2_Text().setValue("Diastolic blood pressure");
        obxSegment2.getObx3_ObservationIdentifier().getCe3_NameOfCodingSystem().setValue("LN");

        CE codedElement2 = new CE(message);
        codedElement2.getIdentifier().setValue("60");
        codedElement2.getText().setValue("mm[Hg]");
        codedElement2.getNameOfCodingSystem().setValue("UOM"); // http://unitsofmeasure.org
        obxSegment2.getObx5_ObservationValue(0).setData(codedElement2);

        // Print the message (remember, the MSH segment was not fully or correctly populated)
        return message.encode();
    }
}

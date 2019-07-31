package input;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
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
import domain.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class HL7VitalSignsInput extends Input {
    public enum SimulationMode { NORMAL, HYPOTENSION, HYPOTHERMIA }

    SimulationMode simulationMode;

    public HL7VitalSignsInput(SimulationMode simulationMode, String outputTopicName) {
        super(outputTopicName);
        this.simulationMode = simulationMode;
    }

    @Override
    String getNextMessage() {
        String message = "";

        try {
            ORU_R01 oru_r01 = buildVitalSignsORU_R01(new VitalSignSet(simulationMode));
            message = oru_r01.encode();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    private ORU_R01 buildVitalSignsORU_R01(VitalSignSet vitals) throws HL7Exception, IOException {
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

        // First, a message object is constructed. The initQuickstart method
        // populates all of the mandatory fields in the MSH segment of the
        // message, including the message type, the timestamp, and the control ID.
        ORU_R01 message = new ORU_R01();
        message.initQuickstart("ORU", "R01", "T");

        ORU_R01_PATIENT_RESULT patientResultGroup = message.getPATIENT_RESULT();
        ORU_R01_PATIENT patientGroup = patientResultGroup.getPATIENT();

        int patientIndex = new Random().nextInt(10);
        Patient patient = Patient.getSamplePatients().get(patientIndex);
        Encounter encounter = Encounter.getSampleEncounters().get(patientIndex); // uses same index as patient because encounter n belongs to patient n

        PID pidSegment = patientGroup.getPID();
        pidSegment.getPid1_SetIDPID().setValue(patient.identifier);
        pidSegment.getPid3_PatientIdentifierList(0).getCx1_IDNumber().setValue(patient.identifier);
        pidSegment.getPid5_PatientName(0).getXpn1_FamilyName().getFn1_Surname().setValue(patient.name.split(",")[0]);
        pidSegment.getPid5_PatientName(0).getXpn2_GivenName().setValue(patient.name.split(",")[1]);
        pidSegment.getPid7_DateTimeOfBirth().getTs1_Time().setValue(patient.birthDate);
        pidSegment.getPid8_AdministrativeSex().setValue(patient.gender);

        ORU_R01_VISIT visitGroup = patientGroup.getVISIT();
        PV1 pv1Segment = visitGroup.getPV1();
        pv1Segment.getPv119_VisitNumber().getCx1_IDNumber().setValue(encounter.identifier);
        pv1Segment.getPv13_AssignedPatientLocation().getPl3_Bed().setValue(Utils.randomNumericIdentifier(2) + "-" + Utils.randomAlphaIdentifier(1));
        pv1Segment.getPv13_AssignedPatientLocation().getPl7_Building().setValue(new String[] { "PavA", "Chandler", "GoodSamaritan" }[new Random().nextInt(3)]);
        pv1Segment.getPv13_AssignedPatientLocation().getPl8_Floor().setValue(Utils.randomNumericIdentifier(1));

        /*
         * The OBR segment is contained within a group called ORDER_OBSERVATION,
         * which is itself in a group called PATIENT_RESULT. These groups are
         * reached using named accessors.
         */
        ORU_R01_ORDER_OBSERVATION orderObservationGroup = patientResultGroup.getORDER_OBSERVATION();

        // Populate the OBR
        OBR obrSegment = orderObservationGroup.getOBR();
        obrSegment.getObr1_SetIDOBR().setValue(Utils.randomAlphaIdentifier(10));
        obrSegment.getObr4_UniversalServiceIdentifier().getCe1_Identifier().setValue("28562-7");
        obrSegment.getObr4_UniversalServiceIdentifier().getCe2_Text().setValue("Vital Signs");
        obrSegment.getObr4_UniversalServiceIdentifier().getCe3_NameOfCodingSystem().setValue("LN");
        String localDateTime = DateTimeFormatter.ofPattern("yyyyMMddhhmmss", Locale.ENGLISH).format(LocalDateTime.now());
        obrSegment.getObr7_ObservationDateTime().getTs1_Time().setValue(localDateTime);

        /*
         * The OBX segment is in a repeating group called OBSERVATION. You can
         * use a named accessor which takes an index to access a specific
         * repetition. You can ask for an index which is equal to the
         * current number of repetitions,and a new repetition will be created.
         */

        buildVitalSignOBX(message, orderObservationGroup, vitals.getSystolicBloodPressure(), 0);
        buildVitalSignOBX(message, orderObservationGroup, vitals.getDiastolicBloodPressure(), 1);
        buildVitalSignOBX(message, orderObservationGroup, vitals.getMeanBloodPressure(), 2);
        buildVitalSignOBX(message, orderObservationGroup, vitals.getBodyTemperature(), 3);
        buildVitalSignOBX(message, orderObservationGroup, vitals.getPulseRate(), 4);
        buildVitalSignOBX(message, orderObservationGroup, vitals.getSPO2(), 5);

        // Return the message (remember, the MSH segment was not fully or correctly populated)
        return message;
    }

    private void buildVitalSignOBX(ORU_R01 message, ORU_R01_ORDER_OBSERVATION orderObservationGroup, VitalSign vitalSign, int rep) throws DataTypeException {
        OBX obxSegment2 = orderObservationGroup.getOBSERVATION(rep).getOBX();
        obxSegment2.getObx1_SetIDOBX().setValue(String.valueOf(rep+1));
        obxSegment2.getObx2_ValueType().setValue("CE"); // Coded Element
        obxSegment2.getObx3_ObservationIdentifier().getCe1_Identifier().setValue(vitalSign.getCode());
        obxSegment2.getObx3_ObservationIdentifier().getCe2_Text().setValue(vitalSign.getName());
        obxSegment2.getObx3_ObservationIdentifier().getCe3_NameOfCodingSystem().setValue("SNOMED-CT");

        CE codedElement2 = new CE(message);
        codedElement2.getIdentifier().setValue(String.valueOf(vitalSign.getValue()));
        codedElement2.getText().setValue("mm[Hg]");
        codedElement2.getNameOfCodingSystem().setValue("UOM"); // http://unitsofmeasure.org
        obxSegment2.getObx5_ObservationValue(0).setData(codedElement2);
    }
}

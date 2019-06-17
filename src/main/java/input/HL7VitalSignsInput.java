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
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HL7VitalSignsInput extends Input {
    public enum SimulationMode { NORMAL, HYPOTENSION, HYPOTHERMIA }
    private enum VitalSignType { SYSTOLIC_BLOOD_PRESSURE, DIASTOLIC_BLOOD_PRESSURE, MEAN_BLOOD_PRESSURE, BODY_TEMPERATURE, PULSE_RATE, SPO2 }
    private enum VitalSignRange { LOW, NORMAL, HIGH }
    private static class VitalSign {
        VitalSignType type;
        VitalSignRange range;
        String name, code;
        double minimumNormal, maximumNormal;

        public VitalSign(@NotNull VitalSignType type, VitalSignRange range) {
            this.type = type;
            this.range = range;

            switch (type) {
                case SYSTOLIC_BLOOD_PRESSURE:
                    this.name = "Systolic blood pressure";
                    this.code = "271649006";
                    this.minimumNormal = 90;
                    this.maximumNormal = 120;
                    break;

                case DIASTOLIC_BLOOD_PRESSURE:
                    this.name = "Diastolic blood pressure";
                    this.code = "271650006";
                    this.minimumNormal = 60;
                    this.maximumNormal = 80;
                    break;

                case MEAN_BLOOD_PRESSURE:
                    this.name = "Mean blood pressure";
                    this.code = "6797001";
                    this.minimumNormal = 92;
                    this.maximumNormal = 96;
                    break;

                case BODY_TEMPERATURE:
                    this.name = "Body temperature";
                    this.code = "386725007";
                    this.minimumNormal = 36.1;
                    this.maximumNormal = 37.2;
                    break;

                case PULSE_RATE:
                    this.name = "Pulse rate";
                    this.code = "78564009";
                    this.minimumNormal = 60;
                    this.maximumNormal = 100;
                    break;

                case SPO2:
                    this.name = "SpO2";
                    this.code = "431314004";
                    this.minimumNormal = 94;
                    this.maximumNormal = 100;
                    break;
            }
        }

        public VitalSignType getType() {
            return this.type;
        }

        public String getName() {
            return this.name;
        }

        public String getCode() {
            return this.code;
        }

        double low() {
            return minimumNormal - (Math.random()*(maximumNormal - minimumNormal));
        }

        double normal() {
            return minimumNormal + (Math.random()*(maximumNormal - minimumNormal));
        }

        double high() {
            return maximumNormal + (Math.random()*(maximumNormal - minimumNormal));
        }

        public double getValue() {
            switch (range) {
                case LOW:
                    return this.low();
                case HIGH:
                    return this.high();
                case NORMAL:
                    return this.normal();
            }

            return this.normal();
        }
    }
    private static class VitalSignSet {
        VitalSign systolicBloodPressure, diastolicBloodPressure, meanBloodPressure, bodyTemperature, pulseRate, SPO2;

        public VitalSignSet(SimulationMode simulationMode) {
            switch (simulationMode) {
                case NORMAL:
                    this.systolicBloodPressure = new VitalSign(VitalSignType.SYSTOLIC_BLOOD_PRESSURE, VitalSignRange.NORMAL);
                    this.diastolicBloodPressure = new VitalSign(VitalSignType.DIASTOLIC_BLOOD_PRESSURE, VitalSignRange.NORMAL);
                    this.meanBloodPressure = new VitalSign(VitalSignType.MEAN_BLOOD_PRESSURE, VitalSignRange.NORMAL);
                    this.bodyTemperature = new VitalSign(VitalSignType.BODY_TEMPERATURE, VitalSignRange.NORMAL);
                    this.pulseRate = new VitalSign(VitalSignType.PULSE_RATE, VitalSignRange.NORMAL);
                    this.SPO2 = new VitalSign(VitalSignType.SPO2, VitalSignRange.NORMAL);
                    break;

                case HYPOTENSION:
                    this.systolicBloodPressure = new VitalSign(VitalSignType.SYSTOLIC_BLOOD_PRESSURE, VitalSignRange.LOW);
                    this.diastolicBloodPressure = new VitalSign(VitalSignType.DIASTOLIC_BLOOD_PRESSURE, VitalSignRange.LOW);
                    this.meanBloodPressure = new VitalSign(VitalSignType.MEAN_BLOOD_PRESSURE, VitalSignRange.LOW);
                    this.bodyTemperature = new VitalSign(VitalSignType.BODY_TEMPERATURE, Math.random() > 0.5 ? VitalSignRange.LOW : VitalSignRange.NORMAL);
                    this.pulseRate = new VitalSign(VitalSignType.PULSE_RATE, VitalSignRange.HIGH);
                    this.SPO2 = new VitalSign(VitalSignType.SPO2, VitalSignRange.LOW);
                    break;

                case HYPOTHERMIA:
                    this.systolicBloodPressure = new VitalSign(VitalSignType.SYSTOLIC_BLOOD_PRESSURE, VitalSignRange.HIGH);
                    this.diastolicBloodPressure = new VitalSign(VitalSignType.DIASTOLIC_BLOOD_PRESSURE, VitalSignRange.HIGH);
                    this.meanBloodPressure = new VitalSign(VitalSignType.MEAN_BLOOD_PRESSURE, VitalSignRange.HIGH);
                    this.bodyTemperature = new VitalSign(VitalSignType.BODY_TEMPERATURE, VitalSignRange.LOW);
                    this.pulseRate = new VitalSign(VitalSignType.PULSE_RATE, VitalSignRange.HIGH);
                    this.SPO2 = new VitalSign(VitalSignType.SPO2, VitalSignRange.NORMAL);
                    break;
            }
        }
    }

    SimulationMode simulationMode;

    public HL7VitalSignsInput(String outputTopicName, SimulationMode simulationMode) {
        super(outputTopicName);

        this.simulationMode = simulationMode;
    }

    @Override
    String getNextMessage() {
        String message = "";

        try {
            message = buildVitalSignsORU_R01(new VitalSignSet(simulationMode));
        } catch(Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    private String buildVitalSignsORU_R01(VitalSignSet vitals)
        throws HL7Exception, IOException {

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

        PID pidSegment = patientGroup.getPID();
        pidSegment.getPid1_SetIDPID().setValue("0123456789");

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

        buildVitalSignOBX(message, orderObservationGroup, vitals.systolicBloodPressure, 0);
        buildVitalSignOBX(message, orderObservationGroup, vitals.diastolicBloodPressure, 1);
        buildVitalSignOBX(message, orderObservationGroup, vitals.meanBloodPressure, 2);
        buildVitalSignOBX(message, orderObservationGroup, vitals.bodyTemperature, 3);
        buildVitalSignOBX(message, orderObservationGroup, vitals.pulseRate, 4);
        buildVitalSignOBX(message, orderObservationGroup, vitals.SPO2, 5);

        // Print the message (remember, the MSH segment was not fully or correctly populated)
        return message.encode();
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

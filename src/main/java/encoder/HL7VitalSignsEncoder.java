package encoder;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.group.*;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.PV1;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.validation.ValidationContext;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import domain.*;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.List;

public class HL7VitalSignsEncoder extends Encoder {
    public HL7VitalSignsEncoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    public List<Model> buildModel(String messageText) throws HL7Exception {
        // HL7 message structure:
        // Message -> Segment -> Field -> Component -> Subcomponent

        // Interface inheritance hierarchy:
        //           Structure
        //              /\
        //         Group  Segment
        //           /      \
        //      Message   GenericSegment
        //         /
        //  GenericMessage
        //       /
        // GenericMessage.*

        HapiContext hapiContext = new DefaultHapiContext();
        hapiContext.setValidationContext((ValidationContext) ValidationContextFactory.noValidation());
        Parser parser = hapiContext.getGenericParser();
        Message message = parser.parse(messageText);
        List<Model> models = new ArrayList<>();

        if (message == null || message.isEmpty())
            return models;

        // Encoding rules:
        //  -for singular elements, use a single variable
        //  -for repeating elements ({}), enter a loop
        //  -for optional elements ([]), check that they're there first

        // { PATIENT_RESULT
        for (Structure structure : message.getAll("PATIENT_RESULT")) {
            if (structure == null || structure.isEmpty())
                continue;

            ORU_R01_PATIENT_RESULT patientResultGroup = (ORU_R01_PATIENT_RESULT)structure;

            // [ PATIENT
            ORU_R01_PATIENT patientGroup = patientResultGroup.getPATIENT();
            if (patientGroup != null && !patientGroup.isEmpty()) {
                PID pidSegment = patientGroup.getPID();

                Patient patient = new Patient();
                patient.identifier = pidSegment.getPid3_PatientIdentifierList(0).getCx1_IDNumber().toString();
                patient.name = pidSegment.getPid5_PatientName(0).getXpn1_FamilyName().getFn1_Surname() + ", " + pidSegment.getPid5_PatientName(0).getXpn2_GivenName();
                patient.birthDate = pidSegment.getPid7_DateTimeOfBirth().getTs1_Time().getValue();
                patient.gender = pidSegment.getPid8_AdministrativeSex().getValue();
                models.add(encodePatient(patient));

                // [ VISIT
                ORU_R01_VISIT visitGroup = patientGroup.getVISIT();
                if (visitGroup != null && !visitGroup.isEmpty()) {
                    PV1 pv1Segment = visitGroup.getPV1();

                    Encounter encounter = new Encounter();
                    models.add(encodeEncounter(encounter));
                    // VISIT ]
                }
            // PATIENT ]
            }

            // { ORDER_OBSERVATION
            for (ORU_R01_ORDER_OBSERVATION orderObservationGroup : patientResultGroup.getORDER_OBSERVATIONAll()) {
                OBR obr = orderObservationGroup.getOBR();

                Observation observation = new Observation();
                observation.observationID = obr.getObr1_SetIDOBR().getValue();
                observation.observationDateTime =  obr.getObr7_ObservationDateTime().encode();

                observation.code = new CodedElement();
                observation.code.observationIdentifier = obr.getObr4_UniversalServiceIdentifier().getCe1_Identifier().getValue();
                observation.code.displayText = obr.getObr4_UniversalServiceIdentifier().getCe2_Text().getValue();
                observation.code.nameOfCodingSystem = obr.getObr4_UniversalServiceIdentifier().getCe3_NameOfCodingSystem().getValue();

                int observationReps = orderObservationGroup.getOBSERVATIONReps();
                observation.components = new CodedElement[observationReps];
                observation.quantities = new Quantity[observationReps];

                for (int i = 0; i < observationReps; i++) {
                    ORU_R01_OBSERVATION observationGroup = orderObservationGroup.getOBSERVATION(i);
                    OBX obx = observationGroup.getOBX();

                    // This code assumes the value in OBX-3 is of data type CE (Coded Element)
                    if (!obx.getObx2_ValueType().getValue().equals("CE"))
                        continue;

                    CE obxIdentifier = obx.getObx3_ObservationIdentifier();
                    observation.components[i] = new CodedElement();
                    observation.components[i].observationIdentifier = obxIdentifier.getCe1_Identifier().getValue();
                    observation.components[i].displayText           = obxIdentifier.getCe2_Text().getValue();
                    observation.components[i].nameOfCodingSystem    = obxIdentifier.getCe3_NameOfCodingSystem().getValue();

                    CE obxData = (CE) obx.getObx5_ObservationValue(0).getData();
                    observation.quantities[i] = new Quantity();
                    observation.quantities[i].value  = obxData.getCe1_Identifier().getValue();
                    observation.quantities[i].unit   = obxData.getCe2_Text().getValue();
                    observation.quantities[i].system = obxData.getCe3_NameOfCodingSystem().getValue();
                }

                models.add(encodeObservation(observation));
            }
            // ORDER_OBSERVATION }
        }
        // [{ OBSERVATION

        return models;
    }
}

/*
 The HL7 spec defines the following structure for an ORU^R01 message, represented in HAPI by the segment group:
 Curly braces - repeated group; straight brackets - optional

 <code>
                     ORDER_OBSERVATION start
       {
       [ ORC ]
       OBR
       [ { NTE } ]
                     TIMING_QTY start
          [{
          TQ1
          [ { TQ2 } ]
          }]
                     TIMING_QTY end
       [ CTD ]
                     OBSERVATION start
          [{
          OBX
          [ { NTE } ]
          }]
                     OBSERVATION end
       [ { FT1 } ]
       [ { CTI } ]
                     SPECIMEN start
          [{
          SPM
          [ { OBX } ]
          }]
                     SPECIMEN end
       }
                     ORDER_OBSERVATION end
 </code>
*/

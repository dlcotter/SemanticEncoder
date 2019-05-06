package encoder;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_PATIENT;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_PATIENT_RESULT;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_VISIT;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.validation.ValidationContext;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class HL7_ORU_R01_Encoder extends Encoder {
    private String FHIR  = "http://hl7.org/fhir/";
    private String LOINC = "http://loinc.org/rdf#";
    private String OWL   = "http://www.w3.org/2002/07/owl#";
    private String RDFS  = "http://www.w3.org/2000/01/rdf-schema#";
    private String SCT   = "http://snomed.info/id/";
    private String XSD   = "http://www.w3.org/2001/XMLSchema#";

    public HL7_ORU_R01_Encoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    Model buildModel(String messageText) throws HL7Exception {
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

        if (message == null || message.isEmpty())
            return ModelFactory.createDefaultModel();

        // Encoding rules:
        //  -for singular elements, use a single variable
        //  -for repeating elements, enter a loop and

        // MSH begin
        MSH msh = (MSH)message.get("MSH");

        // { PATIENT_RESULT
        ORU_R01_PATIENT_RESULT patient_result = (ORU_R01_PATIENT_RESULT)message.get("PATIENT_RESULT");

        // [ PATIENT
        ORU_R01_PATIENT patient = patient_result.getPATIENT();
        PID pid = patient.getPID();

        // [ VISIT
        ORU_R01_VISIT visit = patient.getVISIT();
        // VISIT ]

        // PATIENT ]

        // { ORDER_OBSERVATION
        ORU_R01_ORDER_OBSERVATION order_observation = patient_result.getORDER_OBSERVATION();
        // ORDER_OBSERVATION }

        OBR obr = order_observation.getOBR();

        // [{ OBSERVATION

        // Build the FHIR RDF (hardcoded example to test)
        // Using the Turtle template from http://www.hl7.org/fhir/observation.html
        // Encoding the observation at http://hapi.fhir.org/baseDstu3/Observation/1376793/_history/1?_format=html/xml

        Model model = ModelFactory.createDefaultModel();

        return model;
    }
}

/* The HL7 spec defines the following structure for an ORU^R01 message, represented in HAPI by the segment group:
 * Curly braces - repeated group; straight brackets - optional
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
 */

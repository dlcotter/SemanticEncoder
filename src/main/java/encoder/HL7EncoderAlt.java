package encoder;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.validation.ValidationContext;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

// Refactored from code at https://github.com/housseindh/Hl7ToRDF
public class HL7EncoderAlt extends Encoder {
    private static String HL7_URI = "http://www.HL7.org/segment#";

    public HL7EncoderAlt(String inputTopicName, String outputTopicName) {
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
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource();

        if (message == null || message.isEmpty())
            return model;

        getStructures(message);

        return model;
    }

    private void getStructures(Group group) throws HL7Exception {
        if (group == null || group.isEmpty())
            return;

        for (String name : group.getNames()) {
            for (Structure structure : group.getAll(name)) {
                // A structure can be either a group or a segment (see class hierachy above).
                // If it is a group, we recurse; if a segment, we build the resource.
                System.out.println(name);

                if (group.isGroup(name) && structure instanceof Group)
                    getStructures((Group)structure);

                if (structure instanceof Segment)
                    getSegment((Segment) structure);
            }
        }
    }

    private void getSegment(Segment segment) throws HL7Exception {
        if (segment == null || segment.isEmpty())
            return;

        String name = segment.getName();
        //            convertHL7ToRDF(model, segmentKey, segment);
    }

    // Idea: Move the common functions used in encoding HL7 as RDF to the superclass (HL7EncoderGeneric)
    // and subclass the encoders for specific FHIR resources from this class, using the superclass's
    // functions to build the RDF model for the subclass.

}



// Note to self: This algorithm produces a generic RDF model based on the underlying structures
// of the HL7 in the message. This is fine, but if I am to produce specific types of RDF models
// such as observations, patients, encounters, etc. then I may need to write custom functions
// per entity type, like ConvertORU_R01_toPatientEncounterAndObservation(). The obvious thing
// to do would be to write out FHIR RDF, esp. since this means the FHIR-to-RDF conversion is
// trivial.


// Idea: Use functions from generic HL7 encoder to encode specific FHIR entities in RDF

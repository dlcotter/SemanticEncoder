package encoder;

import ca.uhn.hl7v2.HL7Exception;
import comm.ActiveMQBidirectional;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.ByteArrayOutputStream;
import java.util.List;

abstract class Encoder extends ActiveMQBidirectional implements IEncoder {
    private String FHIR  = "http://hl7.org/fhir/";
//    String LOINC = "http://loinc.org/rdf#";
//    String OWL   = "http://www.w3.org/2002/07/owl#";
//    String RDFS  = "http://www.w3.org/2000/01/rdf-schema#";
//    String SCT   = "http://snomed.info/id/";
//    String XSD   = "http://www.w3.org/2001/XMLSchema#";

    Encoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);

        try {
            this.setMessageListener();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    class Patient {
        String identifier, name, birthDate, gender;
    }

    class Encounter { }

    class Observation {
        String observationID, observationDateTime;
        CodedElement code;
        CodedElement[] components;
        Quantity[] quantities;
    }

    class CodedElement {
         String observationIdentifier, displayText, nameOfCodingSystem;
    }

    class Quantity {
        String value, unit, system;
    }

    Model encodePatient(Patient patient) {
        Model model = ModelFactory.createDefaultModel();

        Resource root = model.getResource(FHIR + "Patient");
        root.addProperty(model.createProperty(FHIR + "Patient.identifier"), patient.identifier);
        root.addProperty(model.createProperty(FHIR + "Patient.name"), patient.name);
        root.addProperty(model.createProperty(FHIR + "Patient.birthDate"), patient.birthDate);
        root.addProperty(model.createProperty(FHIR + "Patient.gender"), patient.gender);

        return model;
    }

    Model encodeEncounter(Encounter encounter) {
        Model model = ModelFactory.createDefaultModel();

        // Fill in later when there's a need for it
//        Resource root = model.getResource(FHIR + "Visit");
//        root.addProperty(model.createProperty(FHIR + "xxxlocationxxx"), "here");
//        root.addProperty(model.createProperty(FHIR + "Encounter.location")                 [ # 0..* List of locations where the patient has been
//        root.addProperty(model.createProperty(FHIR + "Encounter.location.location")        [ Reference(Location) ]; # 1..1 Location the encounter takes place
//        root.addProperty(model.createProperty(FHIR + "Encounter.location.status")          [ code ]; # 0..1 planned | active | reserved | completed
//        root.addProperty(model.createProperty(FHIR + "Encounter.location.physicalType")    [ CodeableConcept ]; # 0..1 The physical type of the location (usually the level in the location hierachy - bed room ward etc.)
//        root.addProperty(model.createProperty(FHIR + "Encounter.location.period")          [ Period ]; # 0..1 Time period during which the patient was present at the location

        return model;
    }

    Model encodeObservation(Observation observation) {
        Model model = ModelFactory.createDefaultModel();

        Resource root = model.getResource(FHIR + "Observation");
        root.addProperty(model.createProperty(FHIR + "Resource.id"), observation.observationID);

        root.addProperty(
                model.createProperty(FHIR + "Resource.meta"),
                model.createResource()
                        .addProperty(model.createProperty(FHIR + "Meta.versionId"), "1")
                        .addProperty(model.createProperty(FHIR + "Meta.lastUpdated"), observation.observationDateTime));

        root.addProperty(model.createProperty(FHIR + "Observation.status"), "final"); // shouldn't be hardcoded

        root.addProperty(
                model.createProperty(FHIR + "Observation.code"),
                model.createResource()
                        .addProperty(
                                model.createProperty(FHIR + "CodeableConcept.coding"),
                                model.createResource()
                                        .addProperty(model.createProperty(FHIR + "index"), "0") //change to i
                                        .addProperty(model.createProperty(FHIR + "Coding.system"), observation.code.nameOfCodingSystem == null ? "" : observation.code.nameOfCodingSystem)
                                        .addProperty(model.createProperty(FHIR + "Coding.code"), observation.code.observationIdentifier == null ? "" : observation.code.observationIdentifier)
                                        .addProperty(model.createProperty(FHIR + "Coding.display"), observation.code.displayText == null ? "" : observation.code.displayText)));

        for (int i = 0; i < observation.components.length; i++) {
            root.addProperty(
                    model.createProperty(FHIR + "Observation.component.code"),
                    model.createResource()
                            .addProperty(
                                    model.createProperty(FHIR + "CodeableConcept.coding"),
                                    model.createResource()
                                            .addProperty(model.createProperty(FHIR + "index"), ((Integer) i).toString())
                                            .addProperty(model.createProperty(FHIR + "Coding.system"), observation.components[i].nameOfCodingSystem == null ? "" : observation.components[i].nameOfCodingSystem)
                                            .addProperty(model.createProperty(FHIR + "Coding.code"), observation.components[i].observationIdentifier == null ? "" : observation.components[i].observationIdentifier)
                                            .addProperty(model.createProperty(FHIR + "Coding.display"), observation.components[i].displayText == null ? "" : observation.components[i].displayText)));

            root.addProperty(
                    model.createProperty(FHIR + "Observation.component.valueQuantity"),
                    model.createResource()
                            .addProperty(
                                    model.createProperty(FHIR + "Observation.component.valueQuantity"),
                                    model.createResource()
                                            .addProperty(model.createProperty(FHIR + "index"), ((Integer) i).toString())
                                            .addProperty(model.createProperty(FHIR + "Quantity.value"), observation.quantities[i].value)
                                            .addProperty(model.createProperty(FHIR + "Quantity.unit"), observation.quantities[i].unit)
                                            .addProperty(model.createProperty(FHIR + "Quantity.system"), observation.quantities[i].system)));
        }

        return model;
    }

    private void setMessageListener() throws JMSException {
        consumer.setMessageListener((inMessage) -> {
            System.out.println(this.getClass() + " caught one.");
            if (!(inMessage instanceof TextMessage))
                return;

            String inMessageText = "";
            try {
                inMessageText = ((TextMessage) inMessage).getText();
            } catch(JMSException e) {
                e.printStackTrace();
            }
            if (inMessageText.isEmpty())
                return;

            List<Model> models;
            try {
                models = this.buildModel(inMessageText);
            } catch (HL7Exception e) {
                e.printStackTrace();
                return;
            }

            for (Model model : models) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                model.write(byteArrayOutputStream, "TTL"); // TTL = Turtle, RDF shorthand
                String outMessageText = new String(byteArrayOutputStream.toByteArray());
                if (outMessageText.isEmpty())
                    continue;

                TextMessage outMessage;
                try {
                    outMessage = session.createTextMessage(outMessageText);
                    producer.send(outMessage);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public abstract List<Model> buildModel(String message) throws HL7Exception;
}

/*
Jena writer name 	RIOT RDFFormat
"TURTLE" 	        TURTLE
"TTL" 	            TURTLE
"Turtle" 	        TURTLE
"N-TRIPLES"         NTRIPLES
"N-TRIPLE" 	        NTRIPLES
"NT" 	            NTRIPLES
"JSON-LD" 	        JSONLD
"RDF/XML-ABBREV" 	RDFXML
"RDF/XML" 	        RDFXML_PLAIN
"N3" 	            N3
"RDF/JSON" 	        RDFJSON
 */
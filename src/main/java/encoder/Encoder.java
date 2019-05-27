package encoder;

import ca.uhn.hl7v2.HL7Exception;
import comm.ActiveMQEnabled;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

abstract class Encoder extends ActiveMQEnabled implements IEncoder {
    Dataset dataset;

    private String FHIR  = "http://hl7.org/fhir/";
//    String LOINC = "http://loinc.org/rdf#";
//    String OWL   = "http://www.w3.org/2002/07/owl#";
//    String RDFS  = "http://www.w3.org/2000/01/rdf-schema#";
//    String SCT   = "http://snomed.info/id/";
//    String XSD   = "http://www.w3.org/2001/XMLSchema#";

    Encoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
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
        model.setNsPrefix("fhir","http://hl7.org/fhir/");
        model.createProperty("a","fhir:Patient");
        model.createProperty("fhir:nodeRole","fhir:treeRoot");

        Resource root = model.getResource("fhir:Patient");
        root.addProperty(model.createProperty("fhir:Patient.identifier"), patient.identifier);
        root.addProperty(model.createProperty("fhir:Patient.name"      ), patient.name);
        root.addProperty(model.createProperty("fhir:Patient.birthDate" ), patient.birthDate);
        root.addProperty(model.createProperty("fhir:Patient.gender"    ), patient.gender);

        return model;
    }

    Model encodeEncounter(Encounter encounter) {
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("fhir","http://hl7.org/fhir/");

        // Fill in later when there's a need for it
//        Resource root = model.getResource("Visit");
//        root.addProperty(model.createProperty("fhir:xxxlocationxxx"), "here");
//        root.addProperty(model.createProperty("fhir:Encounter.location")                 [ # 0..* List of locations where the patient has been
//        root.addProperty(model.createProperty("fhir:Encounter.location.location")        [ Reference(Location) ]; # 1..1 Location the encounter takes place
//        root.addProperty(model.createProperty("fhir:Encounter.location.status")          [ code ]; # 0..1 planned | active | reserved | completed
//        root.addProperty(model.createProperty("fhir:Encounter.location.physicalType")    [ CodeableConcept ]; # 0..1 The physical type of the location (usually the level in the location hierachy - bed room ward etc.)
//        root.addProperty(model.createProperty("fhir:Encounter.location.period")          [ Period ]; # 0..1 Time period during which the patient was present at the location

        return model;
    }

    Model encodeObservation(Observation observation) {
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("fhir","http://hl7.org/fhir/");

        Resource root = model.getResource("fhir:Observation");
        root.addProperty(model.createProperty("fhir:Resource.id"), observation.observationID);

        root.addProperty(
                model.createProperty("fhir:Resource.meta"),
                model.createResource()
                        .addProperty(model.createProperty("fhir:Meta.versionId"), "1")
                        .addProperty(model.createProperty("fhir:Meta.lastUpdated"), observation.observationDateTime));

        root.addProperty(model.createProperty("fhir:Observation.status"), "final"); // shouldn't be hardcoded

        root.addProperty(
                model.createProperty("fhir:Observation.code"),
                model.createResource()
                        .addProperty(
                                model.createProperty("fhir:CodeableConcept.coding"),
                                model.createResource()
                                        .addProperty(model.createProperty("fhir:index"), "0") //change to i
                                        .addProperty(model.createProperty("fhir:Coding.system"), observation.code.nameOfCodingSystem == null ? "" : observation.code.nameOfCodingSystem)
                                        .addProperty(model.createProperty("fhir:Coding.code"), observation.code.observationIdentifier == null ? "" : observation.code.observationIdentifier)
                                        .addProperty(model.createProperty("fhir:Coding.display"), observation.code.displayText == null ? "" : observation.code.displayText)));

        for (int i = 0; i < observation.components.length; i++) {
            root.addProperty(
                    model.createProperty("fhir:Observation.component.code"),
                    model.createResource()
                            .addProperty(
                                    model.createProperty("fhir:CodeableConcept.coding"),
                                    model.createResource()
                                            .addProperty(model.createProperty("fhir:index"), ((Integer) i).toString())
                                            .addProperty(model.createProperty("fhir:Coding.system"), observation.components[i].nameOfCodingSystem == null ? "" : observation.components[i].nameOfCodingSystem)
                                            .addProperty(model.createProperty("fhir:Coding.code"), observation.components[i].observationIdentifier == null ? "" : observation.components[i].observationIdentifier)
                                            .addProperty(model.createProperty("fhir:Coding.display"), observation.components[i].displayText == null ? "" : observation.components[i].displayText)));

            root.addProperty(
                    model.createProperty("fhir:Observation.component.valueQuantity"),
                    model.createResource()
                            .addProperty(
                                    model.createProperty("fhir:Observation.component.valueQuantity"),
                                    model.createResource()
                                            .addProperty(model.createProperty("fhir:index"), ((Integer) i).toString())
                                            .addProperty(model.createProperty("fhir:Quantity.value"), observation.quantities[i].value)
                                            .addProperty(model.createProperty("fhir:Quantity.unit"), observation.quantities[i].unit)
                                            .addProperty(model.createProperty("fhir:Quantity.system"), observation.quantities[i].system)));
        }

        return model;
    }

    public abstract List<Model> buildModel(String message) throws HL7Exception;

    @Override
    protected List<String> processInputText(String inputMessageText) {
        // Build the Jena RDF models based on the incoming message
        List<Model> models = new ArrayList<>();
        try {
            models = this.buildModel(inputMessageText);
        } catch (HL7Exception e) {
            e.printStackTrace();
        }

        List<String> outputMessageTexts = new ArrayList<>();
        for (Model model : models) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            model.write(byteArrayOutputStream, "TTL");
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
            String outputMessageText = new String(byteArrayOutputStream.toByteArray());

            if (outputMessageText.isEmpty())
                continue;

            outputMessageTexts.add(outputMessageText);
        }

        return outputMessageTexts;
    }
}


package encoder;

import ca.uhn.hl7v2.HL7Exception;
import common.ActiveMQEnabled;
import common.Encounter;
import common.Observation;
import common.Patient;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract class Encoder extends ActiveMQEnabled implements IEncoder {
    // The child classes of encoder are expected to build the domain objects
    // patient, encounter, and/or observation, then call encodeXXX and add
    // the resulting Model object to the list of models returned by buildModel().
    // The current design, in which the superclass encodes the model to RDF,
    // abstracts away the encoding from the child classes, so that there is more
    // separation of concerns and less repetitive code.

    HashMap<String,String> prefixes = new HashMap<>();

    Encoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);

        // Initialize prefix hashmap
        prefixes.put("RDF"   ,"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixes.put("RDFS"  ,"http://www.w3.org/2000/01/rdf-schema#");
        prefixes.put("OWL"   ,"http://www.w3.org/2002/07/owl#");
        prefixes.put("XSD"   ,"http://www.w3.org/2001/XMLSchema#");
        prefixes.put("FHIR"  ,"http://hl7.org/fhir#");
        prefixes.put("LOINC" ,"http://loinc.org/rdf#)");
        prefixes.put("SCT"   ,"http://snomed.info/id#");
    }

    // This function adds the required namespaces to the Jena model (used in the encodeXXX() functions)
    private Model getEncoderModel() {
        Model model = ModelFactory.createDefaultModel();

        for (String key : prefixes.keySet())
            model.setNsPrefix(key, prefixes.get(key));

        return model;
    }

    Model encodePatient(@NotNull Patient patient) {
        Model model = this.getEncoderModel();

        Resource root = model.getResource("fhir:Patient");
        root.addProperty(model.createProperty("rdf:type"),"fhir:Patient");
        root.addProperty(model.createProperty("fhir:nodeRole"),"fhir:treeRoot");
        root.addProperty(model.createProperty("fhir:Resource.id"), patient.identifier);

        root.addProperty(model.createProperty("fhir:Patient.identifier"), patient.identifier);
        root.addProperty(model.createProperty("fhir:Patient.name"      ), patient.name);
        root.addProperty(model.createProperty("fhir:Patient.birthDate" ), patient.birthDate);
        root.addProperty(model.createProperty("fhir:Patient.gender"    ), patient.gender);

        return model;
    }

    Model encodeEncounter(@NotNull Encounter encounter) {
        Model model = this.getEncoderModel();

        Resource root = model.getResource("fhir:Observation");
        root.addProperty(model.createProperty("rdf:type"),"fhir:Encounter");
        root.addProperty(model.createProperty("fhir:nodeRole"),"fhir:treeRoot");
//        root.addProperty(model.createProperty("fhir:Resource.id"), encounter.identifier);

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

    Model encodeObservation(@NotNull Observation observation) {
        Model model = this.getEncoderModel();

        Resource root = model.getResource("fhir:Observation");
        root.addProperty(model.createProperty("rdf:type"),"fhir:Observation");
        root.addProperty(model.createProperty("fhir:nodeRole"),"fhir:treeRoot");
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


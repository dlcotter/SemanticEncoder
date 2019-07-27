package encoder;

import ca.uhn.hl7v2.HL7Exception;
import common.ActiveMQEnabled;
import domain.Encounter;
import domain.Observation;
import domain.Patient;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class Encoder extends ActiveMQEnabled {
    // The child classes of encoder are expected to build the domain objects
    // patient, encounter, and/or observation, then call encodeXXX and add
    // the resulting Model object to the list of models returned by buildModel().
    // The current design, in which the superclass encodes the model to RDF, rather
    // than the child class doing it directly, abstracts away the encoding from the
    // child classes, so that there is more separation of concerns and less repetitive code.

    Encoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    // This function sets up a common model that all the subsequent encoder functions use
    private Model getEncoderModel() {
        Model model = ModelFactory.createDefaultModel();

        // Add the required namespaces to the Jena model (used in the encodeXXX() functions)
        for (String key : prefixes.keySet())
            model.setNsPrefix(key, prefixes.get(key));

        return model;
    }

    Model encodePatient(@NotNull Patient patient) {
        Model model = this.getEncoderModel();

        Resource root = model.getResource(model.expandPrefix("fhir:Patient/" + patient.identifier));
        root.addProperty(model.createProperty(model.expandPrefix("fhir:nodeRole")),"fhir:treeRoot");
        root.addProperty(model.createProperty(model.expandPrefix("fhir:Resource.id")), patient.identifier);
        root.addProperty(model.createProperty(model.expandPrefix("rdf:type")), model.expandPrefix("fhir:Patient"));

        root.addProperty(
            model.createProperty(model.expandPrefix("fhir:Patient.identifier")),
            model.createResource()
                    .addProperty(model.createProperty(model.expandPrefix("fhir:index")), "0")
                    .addProperty(
                            model.createProperty(model.expandPrefix("fhir:Identifier.value")),
                            model.createResource()
                                    .addProperty(model.createProperty(model.expandPrefix("fhir:index")), "0")
                                        .addProperty(model.createProperty(model.expandPrefix("fhir:value")), patient.identifier)));

        root.addProperty(
            model.createProperty(model.expandPrefix("fhir:Patient.name")),
            model.createResource()
                    .addProperty(model.createProperty(model.expandPrefix("fhir:index")), "0")
                    .addProperty(
                            model.createProperty(model.expandPrefix("fhir:HumanName.given")),
                            model.createResource()
                                    .addProperty(model.createProperty(model.expandPrefix("fhir:index")), "0")
                                    .addProperty(model.createProperty(model.expandPrefix("fhir:value")), patient.name)));

        root.addProperty(
            model.createProperty(model.expandPrefix("fhir:Patient.birthDate")),
            model.createResource()
                    .addProperty(model.createProperty(model.expandPrefix("fhir:value")), "\"" + patient.birthDate + "\"^^xsd:date"));

        root.addProperty(
            model.createProperty(model.expandPrefix("fhir:Patient.gender")),
            model.createResource()
                    .addProperty(model.createProperty(model.expandPrefix("fhir:value")), patient.gender));

        return model;
    }

    Model encodeEncounter(@NotNull Encounter encounter) {
        Model model = this.getEncoderModel();

        Resource root = model.getResource(model.expandPrefix("fhir:Encounter"));
        root.addProperty(model.createProperty(model.expandPrefix("rdf:type")),model.expandPrefix("fhir:Encounter"));
        root.addProperty(model.createProperty(model.expandPrefix("fhir:nodeRole")),model.expandPrefix("fhir:treeRoot"));
        root.addProperty(model.createProperty(model.expandPrefix("fhir:Resource.id")), encounter.identifier);
        root.addProperty(
                model.createProperty(model.expandPrefix("fhir:Encounter.location")),
                model.createResource()
                        .addProperty(model.createProperty(model.expandPrefix("fhir:index")), "0") //change to i
                        .addProperty(
                                model.createProperty(model.expandPrefix("fhir:location.location")),
                                model.createResource()
                                    .addProperty(model.createProperty(model.expandPrefix("fhir:Reference.reference")), encounter.building + "-" + encounter.floor + encounter.bed)
                                    .addProperty(model.createProperty(model.expandPrefix("fhir:Reference.display")), encounter.building + "-" + encounter.floor + encounter.bed)));

        return model;
    }

    Model encodeObservation(@NotNull Observation observation) {
        Model model = this.getEncoderModel();

        Resource root = model.getResource(model.expandPrefix("fhir:Observation/" + observation.observationID));

        root.addProperty(model.createProperty(model.expandPrefix("rdf:type")), model.expandPrefix("fhir:Observation"));
        root.addProperty(model.createProperty(model.expandPrefix("fhir:nodeRole")), model.expandPrefix("fhir:treeRoot"));
        root.addProperty(model.createProperty(model.expandPrefix("fhir:Resource.id")), observation.observationID);

        root.addProperty(
                model.createProperty(model.expandPrefix("fhir:Resource.meta")),
                model.createResource()
                        .addProperty(model.createProperty(model.expandPrefix("fhir:Meta.versionId")), "1")
                        .addProperty(model.createProperty(model.expandPrefix("fhir:Meta.lastUpdated")), observation.observationDateTime));

        root.addProperty(model.createProperty(model.expandPrefix("fhir:Observation.status")), "final"); // shouldn't be hardcoded

        root.addProperty(
                model.createProperty(model.expandPrefix("fhir:Observation.code")),
                model.createResource()
                        .addProperty(
                                model.createProperty(model.expandPrefix("fhir:CodeableConcept.coding")),
                                model.createResource()
                                        .addProperty(model.createProperty(model.expandPrefix("fhir:index")), "0") //change to i
                                        .addProperty(model.createProperty(model.expandPrefix("fhir:Coding.system")), observation.code.nameOfCodingSystem == null ? "" : observation.code.nameOfCodingSystem)
                                        .addProperty(model.createProperty(model.expandPrefix("fhir:Coding.code")), observation.code.observationIdentifier == null ? "" : observation.code.observationIdentifier)
                                        .addProperty(model.createProperty(model.expandPrefix("fhir:Coding.display")), observation.code.displayText == null ? "" : observation.code.displayText)));

        for (int i = 0; i < observation.components.length; i++) {
            root.addProperty(
                    model.createProperty(model.expandPrefix("fhir:Observation.component.code")),
                    model.createResource()
                            .addProperty(
                                    model.createProperty(model.expandPrefix("fhir:CodeableConcept.coding")),
                                    model.createResource()
                                            .addProperty(model.createProperty(model.expandPrefix("fhir:index")), ((Integer) i).toString())
                                            .addProperty(model.createProperty(model.expandPrefix("fhir:Coding.system")), observation.components[i].nameOfCodingSystem == null ? "" : observation.components[i].nameOfCodingSystem)
                                            .addProperty(model.createProperty(model.expandPrefix("fhir:Coding.code")), observation.components[i].observationIdentifier == null ? "" : observation.components[i].observationIdentifier)
                                            .addProperty(model.createProperty(model.expandPrefix("fhir:Coding.display")), observation.components[i].displayText == null ? "" : observation.components[i].displayText)));

            root.addProperty(
                    model.createProperty(model.expandPrefix("fhir:Observation.component.valueQuantity")),
                    model.createResource()
                            .addProperty(model.createProperty(model.expandPrefix("fhir:index")), ((Integer) i).toString())
                            .addProperty(model.createProperty(model.expandPrefix("fhir:Quantity.value")), observation.quantities[i].value)
                            .addProperty(model.createProperty(model.expandPrefix("fhir:Quantity.unit")), observation.quantities[i].unit)
                            .addProperty(model.createProperty(model.expandPrefix("fhir:Quantity.system")), observation.quantities[i].system));
        }

        return model;
    }

    // This method is overridden in child classes, which handle different types of inputs, i.e. HL7v2, FHIR, CSV, etc.
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
            RDFDataMgr.write(byteArrayOutputStream, model, RDFFormat.TURTLE);
            String outputMessageText = new String(byteArrayOutputStream.toByteArray());

            if (outputMessageText.isEmpty())
                continue;

            outputMessageTexts.add(outputMessageText);
        }

        return outputMessageTexts;
    }
}


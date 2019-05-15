package encoder;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import java.util.ArrayList;
import java.util.List;

public class HL7VitalSignsEncoderHardcoded extends Encoder implements IEncoder {
    private String FHIR  = "http://hl7.org/fhir/";
    private String LOINC = "http://loinc.org/rdf#";
    private String OWL   = "http://www.w3.org/2002/07/owl#";
    private String RDFS  = "http://www.w3.org/2000/01/rdf-schema#";
    private String SCT   = "http://snomed.info/id/";
    private String XSD   = "http://www.w3.org/2001/XMLSchema#";

    public HL7VitalSignsEncoderHardcoded(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    public List<Model> buildModel(String messageText) throws HL7Exception {
        // Build the FHIR RDF (hardcoded example to test)
        // Using the Turtle template from http://www.hl7.org/fhir/observation.html
        // Encoding the observation at http://hapi.fhir.org/baseDstu3/Observation/1376793/_history/1?_format=html/xml
        // Verifying RDF output using example at http://www.hl7.org/fhir/observation-example-bloodpressure.ttl.html

        List<Model> models = new ArrayList<>();
        Model model = ModelFactory.createDefaultModel();

        // <Observation xmlns="http://hl7.org/fhir">
        // (mandatory)
        Resource root = model.getResource(FHIR + "Observation");

        // <id value="1376793"/>
        // (optional)
        root.addProperty(model.createProperty(FHIR + "Resource.id"), "1376793");

        // <meta>
        //    <versionId value="1"/>
        //    <lastUpdated value="2019-02-18T14:10:29.115+00:00"/>
        // </meta>
        // (optional)
        root.addProperty(
                model.createProperty(FHIR + "Resource.meta"),
                model.createResource()
                        .addProperty(model.createProperty(FHIR + "Meta.versionId"),"1")
                        .addProperty(model.createProperty(FHIR + "Meta.lastUpdated"),"2019-02-18T14:10:29.115+00:00"));

        // <status value="final"/>
        // (mandatory)
        root.addProperty(model.createProperty(FHIR + "Observation.status"),"final");

        // <code>
        //    <coding>
        //       <system value="http://loinc.org"/>
        //       <code value="85354-9"/>
        //       <display value="Blood pressure panel with all children optional"/>
        //    </coding>
        //    <text value="Blood pressure systolic &amp; diastolic"/>
        // </code>
        // (mandatory)
        root.addProperty(
                model.createProperty(FHIR + "Observation.code"),
                model.createResource()
                        .addProperty(model.createProperty(FHIR + "CodeableConcept.text"),"Blood pressure systolic & diastolic")
                        .addProperty(
                                model.createProperty(FHIR + "CodeableConcept.coding"),
                                model.createResource()
                                        .addProperty(model.createProperty(FHIR + "index"),"0")
                                        .addProperty(model.createProperty(FHIR + "Coding.system"),"http://loinc.org")
                                        .addProperty(model.createProperty(FHIR + "Coding.code"),"85354-9")
                                        .addProperty(model.createProperty(FHIR + "Coding.display"),"Blood pressure panel with all children optional")));

        // <effectiveDateTime value="2019-02-18"/>
        // (optional)
        root.addProperty(model.createProperty(FHIR + "Observation.effectiveDateTime"),"2019-02-18");

        // <interpretation>
        //    <coding>
        //       <system value="http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation"/>
        //       <code value="L"/>
        //       <display value="low"/>
        //    </coding>
        //    <text value="Below low normal"/>
        // </interpretation>
        // (optional)
        root.addProperty(
                model.createProperty(FHIR + "Observation.interpretation"),
                model.createResource()
                        .addProperty(
                                model.createProperty(FHIR + "CodeableConcept.coding"),
                                model.createResource()
                                        .addProperty(model.createProperty(FHIR + "Coding.system"), "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation")
                                        .addProperty(model.createProperty(FHIR + "Coding.code"), "L")
                                        .addProperty(model.createProperty(FHIR + "Coding.display"), "low")));

        // <bodySite>
        //   <coding>
        //      <system value="http://snomed.info/sct"/>
        //      <code value="368209003"/>
        //      <display value="Right arm"/>
        //   </coding>
        // </bodySite>
        // (optional)
        root.addProperty(
                model.createProperty(FHIR + "Observation.bodySite"),
                model.createResource()
                        .addProperty(
                                model.createProperty(FHIR + "CodeableConcept.coding"),
                                model.createResource()
                                        .addProperty(model.createProperty(FHIR + "Coding.system"), "http://snomed.info/sct")
                                        .addProperty(model.createProperty(FHIR + "Coding.code"), "368209003")
                                        .addProperty(model.createProperty(FHIR + "Coding.display"), "Right arm")));

        // <component>
        //   <code>
        //      <coding>
        //         <system value="http://loinc.org"/>
        //         <code value="8480-6"/>
        //         <display value="Systolic blood pressure"/>
        //      </coding>
        //      <coding>
        //         <system value="http://snomed.info/sct"/>
        //         <code value="271649006"/>
        //         <display value="Systolic blood pressure"/>
        //      </coding>
        //      <coding>
        //         <system value="http://acme.org/devices/clinical-codes"/>
        //         <code value="bp-s"/>
        //         <display value="Systolic Blood pressure"/>
        //      </coding>
        //   </code>
        //   <valueQuantity>
        //      <value value="107"/>
        //      <unit value="mmHg"/>
        //      <system value="http://unitsofmeasure.org"/>
        //      <code value="mm[Hg]"/>
        //   </valueQuantity>
        //   <interpretation>
        //      <coding>
        //         <system value="http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation"/>
        //         <code value="N"/>
        //         <display value="normal"/>
        //      </coding>
        //      <text value="Normal"/>
        //   </interpretation>
        // </component>
        // (optional)
        root.addProperty(
                model.createProperty(FHIR + "Observation.component"),
                model.createResource()
                        .addProperty(model.createProperty(FHIR + "index"),"0")
                        .addProperty(
                                model.createProperty(FHIR + "Observation.component.code"),
                                model.createResource()
                                        .addProperty(
                                                model.createProperty(FHIR + "CodeableConcept.coding"),
                                                model.createResource()
                                                        .addProperty(model.createProperty(FHIR + "index"),"0")
                                                        .addProperty(model.createProperty(FHIR + "Coding.system"), "http://loinc.org")
                                                        .addProperty(model.createProperty(FHIR + "Coding.code"), "8480-6")
                                                        .addProperty(model.createProperty(FHIR + "Coding.display"), "Systolic blood pressure"))
                                        .addProperty(
                                                model.createProperty(FHIR + "CodeableConcept.coding"),
                                                model.createResource()
                                                        .addProperty(model.createProperty(FHIR + "index"),"1")
                                                        .addProperty(model.createProperty(FHIR + "Coding.system"), "http://snomed.info/sct")
                                                        .addProperty(model.createProperty(FHIR + "Coding.code"), "271649006")
                                                        .addProperty(model.createProperty(FHIR + "Coding.display"), "Systolic blood pressure"))
                                        .addProperty(
                                                model.createProperty(FHIR + "CodeableConcept.coding"),
                                                model.createResource()
                                                        .addProperty(model.createProperty(FHIR + "index"),"2")
                                                        .addProperty(model.createProperty(FHIR + "Coding.system"), "http://acme.org/devices/clinical-codes")
                                                        .addProperty(model.createProperty(FHIR + "Coding.code"), "bp-s")
                                                        .addProperty(model.createProperty(FHIR + "Coding.display"), "Systolic blood pressure")))
                        .addProperty(
                                model.createProperty(FHIR + "Observation.component.valueQuantity"),
                                model.createResource()
                                        .addProperty(model.createProperty(FHIR + "Quantity.value") , "107")
                                        .addProperty(model.createProperty(FHIR + "Quantity.unit")  , "mmHg")
                                        .addProperty(model.createProperty(FHIR + "Quantity.system"), "http://unitsofmeasure.org")
                                        .addProperty(model.createProperty(FHIR + "Quantity.code")  , "mm[Hg]"))
                        .addProperty(
                                model.createProperty(FHIR + "Observation.component.interpretation"),
                                model.createResource()
                                        .addProperty(model.createProperty(FHIR + "index"),"0")
                                        .addProperty(
                                                model.createProperty(FHIR + "CodeableConcept.coding"),
                                                model.createResource()
                                                        .addProperty(model.createProperty(FHIR + "Coding.system"), "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation")
                                                        .addProperty(model.createProperty(FHIR + "Coding.code"), "N")
                                                        .addProperty(model.createProperty(FHIR + "Coding.display"), "normal"))
                                        .addProperty(model.createProperty(FHIR + "CodeableConcept.text"), "Normal")));

        //    <component>
        //      <code>
        //         <coding>
        //            <system value="http://loinc.org"/>
        //            <code value="8462-4"/>
        //            <display value="Diastolic blood pressure"/>
        //         </coding>
        //      </code>
        //      <valueQuantity>
        //         <value value="60"/>
        //         <unit value="mmHg"/>
        //         <system value="http://unitsofmeasure.org"/>
        //         <code value="mm[Hg]"/>
        //      </valueQuantity>
        //      <interpretation>
        //         <coding>
        //            <system value="http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation"/>
        //            <code value="L"/>
        //            <display value="low"/>
        //         </coding>
        //         <text value="Below low normal"/>
        //      </interpretation>
        //   </component>
        // (optional)
        root.addProperty(
                model.createProperty(FHIR + "Observation.component"),
                model.createResource()
                        .addProperty(
                                model.createProperty(FHIR + "Observation.component.code"),
                                model.createResource()
                                        .addProperty(
                                                model.createProperty(FHIR + "CodeableConcept.coding"),
                                                model.createResource()
                                                        // Do I need to add indices here?
                                                        .addProperty(model.createProperty(FHIR + "Coding.system"), "http://loinc.org")
                                                        .addProperty(model.createProperty(FHIR + "Coding.code"), "8462-4")
                                                        .addProperty(model.createProperty(FHIR + "Coding.display"), "Diastolic blood pressure")))
                        .addProperty(
                                model.createProperty(FHIR + "Observation.component.valueQuantity"),
                                model.createResource()
                                        .addProperty(model.createProperty(FHIR + "Quantity.value") , "60")
                                        .addProperty(model.createProperty(FHIR + "Quantity.unit")  , "mmHg")
                                        .addProperty(model.createProperty(FHIR + "Quantity.system"), "http://unitsofmeasure.org")
                                        .addProperty(model.createProperty(FHIR + "Quantity.code")  , "mm[Hg]"))
                        .addProperty(
                                model.createProperty(FHIR + "Observation.component.interpretation"),
                                model.createResource()
                                        .addProperty(
                                                model.createProperty(FHIR + "CodeableConcept.coding"),
                                                model.createResource()
                                                        .addProperty(model.createProperty(FHIR + "Coding.system"), "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation")
                                                        .addProperty(model.createProperty(FHIR + "Coding.code"), "L")
                                                        .addProperty(model.createProperty(FHIR + "Coding.display"), "low"))
                                        .addProperty(model.createProperty(FHIR + "CodeableConcept.text"), "Below low normal")));
    models.add(model);

    return models;
    }


}

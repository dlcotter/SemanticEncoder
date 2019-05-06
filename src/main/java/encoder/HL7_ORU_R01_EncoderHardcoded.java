package encoder;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

public class HL7_ORU_R01_EncoderHardcoded extends Encoder {
    private String FHIR  = "http://hl7.org/fhir/";
    private String LOINC = "http://loinc.org/rdf#";
    private String OWL   = "http://www.w3.org/2002/07/owl#";
    private String RDFS  = "http://www.w3.org/2000/01/rdf-schema#";
    private String SCT   = "http://snomed.info/id/";
    private String XSD   = "http://www.w3.org/2001/XMLSchema#";

    public HL7_ORU_R01_EncoderHardcoded(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    Model buildModel(String messageText) throws HL7Exception {
        Model model = ModelFactory.createDefaultModel();

        // <Observation xmlns="http://hl7.org/fhir">
        Resource root = model.getResource(FHIR + "Observation");

        // <id value="1376793"/>
        root.addProperty(model.createProperty(FHIR + "Resource.id"), "1376793");

        // <meta>
        //    <versionId value="1"/>
        //    <lastUpdated value="2019-02-18T14:10:29.115+00:00"/>
        // </meta>
        root.addProperty(
            model.createProperty(FHIR + "Resource.meta"),
                model.createResource()
                .addProperty(model.createProperty(FHIR + "Meta.versionId"),"1")
                .addProperty(model.createProperty(FHIR + "Meta.lastUpdated"),"2019-02-18T14:10:29.115+00:00")
        );

        // <status value="final"/>
        root.addProperty(model.createProperty(FHIR + "Observation.status"),"final");

        // <code>
        //    <coding>
        //       <system value="http://loinc.org"/>
        //       <code value="85354-9"/>
        //       <display value="Blood pressure panel with all children optional"/>
        //    </coding>
        //    <text value="Blood pressure systolic &amp; diastolic"/>
        // </code>
        root.addProperty(
            model.createProperty(FHIR + "Observation.code"),
            model.createResource()
                .addProperty(
                    model.createProperty(FHIR + "CodeableConcept.coding"),
                    model.createResource()
                        .addProperty(model.createProperty(FHIR + "Coding.system"),"http://loinc.org")
                        .addProperty(model.createProperty(FHIR + "Coding.code"),"85354-9")
                        .addProperty(model.createProperty(FHIR + "Coding.display"),"Blood pressure panel with all children optional")));

        // <effectiveDateTime value="2019-02-18"/>
        root.addProperty(model.createProperty(FHIR + "Observation.effectiveDateTime"),"2019-02-18");

        // <interpretation>
        //    <coding>
        //       <system value="http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation"/>
        //       <code value="L"/>
        //       <display value="low"/>
        //    </coding>
        //    <text value="Below low normal"/>
        // </interpretation>
        root.addProperty(
            model.createProperty(FHIR + "Observation.interpretation"),
                model.createResource()
                .addProperty(
                    model.createProperty(FHIR + "CodeableConcept.coding"),
                        model.createResource()
                        .addProperty(model.createProperty(FHIR + "Coding.system"), "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation")
                        .addProperty(model.createProperty(FHIR + "Coding.code"), "L")
                        .addProperty(model.createProperty(FHIR + "Coding.display"), "low")));

        return model;
    }


}

/*
<Observation xmlns="http://hl7.org/fhir">
   <id value="1376793"/>
   <meta>
      <versionId value="1"/>
      <lastUpdated value="2019-02-18T14:10:29.115+00:00"/>
   </meta>
   <status value="final"/>
   <code>
      <coding>
         <system value="http://loinc.org"/>
         <code value="85354-9"/>
         <display value="Blood pressure panel with all children optional"/>
      </coding>
      <text value="Blood pressure systolic &amp; diastolic"/>
   </code>
   <effectiveDateTime value="2019-02-18"/>
   <interpretation>
      <coding>
         <system value="http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation"/>
         <code value="L"/>
         <display value="low"/>
      </coding>
      <text value="Below low normal"/>
   </interpretation>
   <bodySite>
      <coding>
         <system value="http://snomed.info/sct"/>
         <code value="368209003"/>
         <display value="Right arm"/>
      </coding>
   </bodySite>
   <component>
      <code>
         <coding>
            <system value="http://loinc.org"/>
            <code value="8480-6"/>
            <display value="Systolic blood pressure"/>
         </coding>
         <coding>
            <system value="http://snomed.info/sct"/>
            <code value="271649006"/>
            <display value="Systolic blood pressure"/>
         </coding>
         <coding>
            <system value="http://acme.org/devices/clinical-codes"/>
            <code value="bp-s"/>
            <display value="Systolic Blood pressure"/>
         </coding>
      </code>
      <valueQuantity>
         <value value="107"/>
         <unit value="mmHg"/>
         <system value="http://unitsofmeasure.org"/>
         <code value="mm[Hg]"/>
      </valueQuantity>
      <interpretation>
         <coding>
            <system value="http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation"/>
            <code value="N"/>
            <display value="normal"/>
         </coding>
         <text value="Normal"/>
      </interpretation>
   </component>
   <component>
      <code>
         <coding>
            <system value="http://loinc.org"/>
            <code value="8462-4"/>
            <display value="Diastolic blood pressure"/>
         </coding>
      </code>
      <valueQuantity>
         <value value="60"/>
         <unit value="mmHg"/>
         <system value="http://unitsofmeasure.org"/>
         <code value="mm[Hg]"/>
      </valueQuantity>
      <interpretation>
         <coding>
            <system value="http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation"/>
            <code value="L"/>
            <display value="low"/>
         </coding>
         <text value="Below low normal"/>
      </interpretation>
   </component>
</Observation>

 */
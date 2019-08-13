package query;

import common.Utils;

// Search for blood pressure readings over 120
public class HighBloodPressureQuery extends Query {
    public HighBloodPressureQuery(String outputTopicName) {
        super(outputTopicName);

        query = "PREFIX fhir:  <http://hl7.org/fhir/>\n" +
                "PREFIX loinc: <http://loinc.org/rdf#>\n" +
                "PREFIX owl:   <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX sct:   <http://snomed.info/id#>\n" +
                "PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>\n" +
                "\n" +
                "CONSTRUCT {\n" +
                "  ?observation a fhir:Observation .\n" +
                "\n" +
                "  ?observation fhir:Observation.component.valueQuantity ?observation_component_value_quantity .\n" +
                "    ?observation_component_value_quantity fhir:Quantity.system ?quantity_system .\n" +
                "      ?quantity_system fhir:value \"UOM\" .\n" +
                "    ?observation_component_value_quantity fhir:Quantity.unit ?quantity_unit .\n" +
                "      ?quantity_unit fhir:value \"mm[Hg]\" .\n" +
                "    ?observation_component_value_quantity fhir:Quantity.value ?quantity_value .\n" +
                "      ?quantity_value fhir:value ?quantity_value_value .\n" +
                "    ?observation_component_value_quantity fhir:index ?observation_component_value_quantity_index .\n" +
                "\n" +
                "  ?observation fhir:Observation.effectiveDateTime ?observation_effective_date_time .\n" +
                "    ?observation_effective_date_time fhir:value ?observation_effective_date_time_value .\n" +
                "\n" +
                "  ?observation fhir:Observation.subject ?observation_subject .\n" +
                "    ?observation_subject fhir:Reference.reference ?observation_subject_reference_reference .\n" +
                "      ?observation_subject_reference_reference fhir:value ?observation_subject_reference_reference_value .\n" +
                "}\n" +
                "WHERE {\n" +
                "  ?observation a fhir:Observation ." +
                "\n" +
                "  ?observation fhir:Observation.component.valueQuantity ?observation_component_value_quantity .\n" +
                "    ?observation_component_value_quantity fhir:Quantity.system ?quantity_system .\n" +
                "      ?quantity_system fhir:value \"UOM\" .\n" +
                "    ?observation_component_value_quantity fhir:Quantity.unit ?quantity_unit .\n" +
                "      ?quantity_unit fhir:value \"mm[Hg]\" .\n" +
                "    ?observation_component_value_quantity fhir:Quantity.value ?quantity_value .\n" +
                "      ?quantity_value fhir:value ?quantity_value_value .\n" +
                "    ?observation_component_value_quantity fhir:index ?observation_component_value_quantity_index .\n" +
                "\n" +
                "  ?observation fhir:Observation.effectiveDateTime ?observation_effective_date_time .\n" +
                "    ?observation_effective_date_time fhir:value ?observation_effective_date_time_value .\n" +
                "\n" +
                "  ?observation fhir:Observation.subject ?observation_subject .\n" +
                "    ?observation_subject fhir:Reference.reference ?observation_subject_reference_reference .\n" +
                "      ?observation_subject_reference_reference fhir:value ?observation_subject_reference_reference_value .\n" +
                "\n" +
                "FILTER (xsd:decimal(?quantity_value_value) > 100)\n" +
                "FILTER (?observation_effective_date_time_value > \"" + Utils.nSecondsAgo(1, Utils.XSD_DATETIME_FMT) + "\"^^xsd:dateTime)\n" +
                "}";
    }
}

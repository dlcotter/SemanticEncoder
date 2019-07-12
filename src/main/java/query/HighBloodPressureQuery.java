package query;

// Search for blood pressure readings over 120
public class HighBloodPressureQuery extends Query {
    private static final String QUERY
      = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
      + "SELECT ?obs_resource ?value\n"
      + "WHERE { ?obs_resource    <rdf:type>                                 \"fhir:Observation\" .\n"
      + "        ?obs_resource    <fhir:Observation.component.code>          ?code_resource     .\n"
      + "        ?code_resource   <fhir:CodeableConcept.coding>              ?coding_resource   .\n"
      + "        ?coding_resource <fhir:Coding.system>                       \"SNOMED-CT\"        .\n"
      + "        ?coding_resource <fhir:Coding.code>                         \"271649006\"        .\n"
      + "        ?obs_resource    <fhir:Observation.component.valueQuantity> ?value_resource    .\n"
      + "        ?value_resource  <fhir:Quantity.value>                      ?value             .\n"
      + "FILTER (xsd:decimal(?value) > 120)\n"
      + "}\n";

    public HighBloodPressureQuery(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName, QUERY);
    }
}

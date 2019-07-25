package query;

public class PatientsByGenderQuery extends Query {
    private final static String query
            = "SELECT DISTINCT ?patient_resource ?gender_value ?name_value                          "
            + "WHERE { ?patient_resource <rdf:type>             \"fhir:Patient\"        .  "
            + "        ?patient_resource <fhir:Patient.gender>  ?gender_resource        .  "
            + "        ?gender_resource  <fhir:value>           ?gender_value           .  "
            + "        ?patient_resource <fhir:Patient.name>    ?name_resource          .  "
            + "        ?name_resource    <fhir:HumanName.given> ?given_resource         .  "
            + "        ?given_resource   <fhir:value>           ?name_value             . }";

    public PatientsByGenderQuery(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName, query);
    }
}

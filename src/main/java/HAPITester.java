import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.dstu3.model.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class HAPITester {
    // Create a context for DSTU2
    private FhirContext ctx = FhirContext.forDstu3();
    private String serverBase = "http://hapi.fhir.org/baseDstu3/";
    private Connection conn = null;

    public void HAPITester() {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ohdsi", "postgres", "postgres");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void GetVitalSigns() {
        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

        // Perform a search
        Bundle bundle = (Bundle) client.search().forResource(Observation.class)
                .where(new TokenClientParam("code").exactly().systemAndCode("http://loinc.org", "8480-6"))
                .include(new Include("Observation:encounter"))
                .include(new Include("Observation:patient"))
                .prettyPrint()
                .limitTo(10)
                .execute();

        if (bundle.getEntry().isEmpty())
            return;

        List<Patient> patients = new ArrayList<Patient>();
        List<Encounter> encounters = new ArrayList<Encounter>();
        List<Observation> observations = new ArrayList<Observation>();

        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            Resource resource = entry.getResource();
            switch (resource.fhirType()) {
                case "Patient":
                    patients.add((Patient) resource);
                    continue;
                case "Encounter":
                    encounters.add((Encounter) resource);
                    continue;
                case "Observation":
                    observations.add((Observation) resource);
                    continue;
            }
        }

        // Insert resources in reverse order of their dependencies, in this case Patient -> Encounter -> Observation
        for (Patient patient : patients) InsertPatient(patient);
        for (Encounter encounter : encounters) InsertEncounter(encounter);
        for (Observation observation : observations) InsertObservation(observation);
    }

    private void InsertPatient(Patient patient) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = null;
        /*
                                                                    Table "public.person"
                   Column            |            Type             | Collation | Nullable |                  Default
        -----------------------------+-----------------------------+-----------+----------+-------------------------------------------
         person_id                   | bigint                      |           | not null | nextval('person_person_id_seq'::regclass)
         gender_concept_id           | integer                     |           | not null |
         year_of_birth               | integer                     |           | not null |
         month_of_birth              | integer                     |           |          |
         day_of_birth                | integer                     |           |          |
         birth_datetime              | timestamp without time zone |           |          |
         death_datetime              | timestamp without time zone |           |          |
         race_concept_id             | integer                     |           | not null |
         ethnicity_concept_id        | integer                     |           | not null |
         location_id                 | bigint                      |           |          |
         provider_id                 | bigint                      |           |          |
         care_site_id                | bigint                      |           |          |
         person_source_value         | character varying(50)       |           |          |
         gender_source_value         | character varying(50)       |           |          |
         gender_source_concept_id    | integer                     |           | not null |
         race_source_value           | character varying(50)       |           |          |
         race_source_concept_id      | integer                     |           | not null |
         ethnicity_source_value      | character varying(50)       |           |          |
         ethnicity_source_concept_id | integer                     |           | not null |
         */
    }

    private void InsertEncounter(Encounter encounter) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = null;
        /*
                                                                  Table "public.visit_occurrence"
            Column             |            Type             | Collation | Nullable |                            Default
-------------------------------+-----------------------------+-----------+----------+---------------------------------------------------------------
 person_id                     | bigint                      |           | not null |
 visit_concept_id              | integer                     |           | not null |
 visit_start_date              | date                        |           |          |
 visit_start_datetime          | timestamp without time zone |           | not null |
 visit_end_date                | date                        |           |          |
 visit_end_datetime            | timestamp without time zone |           | not null |
 visit_type_concept_id         | integer                     |           | not null |
 provider_id                   | bigint                      |           |          |
 care_site_id                  | bigint                      |           |          |
 visit_source_value            | character varying(50)       |           |          |
 visit_source_concept_id       | integer                     |           | not null |
 admitted_from_concept_id      | integer                     |           | not null |
 admitted_from_source_value    | character varying(50)       |           |          |
 discharge_to_source_value     | character varying(50)       |           |          |
 discharge_to_concept_id       | integer                     |           | not null |
 preceding_visit_occurrence_id | bigint                      |           |          |
 visit_occurrence_id           | bigint                      |           | not null | nextval('visit_occurrence_visit_occurrence_id_seq'::regclass)

         */
    }

    private void InsertObservation(Observation observation) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Quantity quantity = (Quantity) observation.getValue();
        BigDecimal value = quantity.getValue();


        String sql = "insert into observation"
                + " (observation_id"
                + " ,person_id"
                + " ,observation_concept_id"
                + " ,observation_datetime"
                + " ,observation_type_concept_id"
                + " ,observation_date"
                + " ,value_as_number"
                + " ,value_as_string"
                + " ,observation_source_concept_id"
                + " ,obs_event_field_concept_id)"
                + " values"
                + " (1" //synthetic value
                + " ,1" //synthetic value
                + " ,4152194" // systolic blood pressure (SNOMED-CT)
                + " ,'2019-04-01 10:32:38.918051-04'"
                + " ,44819029 " // Observable Entity (concept_id from concept_class table)
                + " ,'4/1/2019'"
                + " ," + value //get value from FHIR resource
                + " ,'" + value + "'" //get value from FHIR resource
                + " ,3016833" // Chart section Set
                + " ,4152194)"; // systolic blood pressure (SNOMED-CT)

        /*
                                                                Table "public.observation"
            Column             |            Type             | Collation | Nullable |                       Default
-------------------------------+-----------------------------+-----------+----------+-----------------------------------------------------
 person_id                     | bigint                      |           | not null |
 observation_concept_id        | integer                     |           | not null |
 observation_date              | date                        |           |          |
 observation_datetime          | timestamp without time zone |           | not null |
 observation_type_concept_id   | integer                     |           | not null |
 value_as_number               | numeric                     |           |          |
 value_as_string               | character varying(60)       |           |          |
 value_as_concept_id           | integer                     |           |          |
 qualifier_concept_id          | integer                     |           |          |
 unit_concept_id               | integer                     |           |          |
 provider_id                   | bigint                      |           |          |
 visit_occurrence_id           | bigint                      |           |          |
 visit_detail_id               | bigint                      |           |          |
 observation_source_value      | character varying(50)       |           |          |
 observation_source_concept_id | integer                     |           | not null |
 unit_source_value             | character varying(50)       |           |          |
 qualifier_source_value        | character varying(50)       |           |          |
 observation_event_id          | bigint                      |           |          |
 obs_event_field_concept_id    | integer                     |           | not null |
 value_as_datetime             | timestamp without time zone |           |          |
 observation_id                | bigint                      |           | not null | nextval('observation_observation_id_seq'::regclass)
         */

        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

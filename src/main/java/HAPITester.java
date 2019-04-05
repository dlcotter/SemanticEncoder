import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.dstu3.model.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class HAPITester {
    // Create a context for DSTU2
    private FhirContext ctx = FhirContext.forDstu3();
    private String serverBase = "http://hapi.fhir.org/baseDstu3/";
    private String charset = StandardCharsets.UTF_8.name();

    public void HAPITester() {
    }

    void GetVitalSigns() {
        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

        // Perform a search
        Bundle bundle = (Bundle) client.search().forResource(Observation.class)
                .where(new TokenClientParam("code").exactly().systemAndCode("http://loinc.org", "8480-6"))
                .include(new Include("Observation:encounter"))
                .include(new Include("Observation:patient"))
                .prettyPrint()
                .limitTo(1)
                .execute();
        System.out.println("Got the bundle");

        if (bundle.getEntry().isEmpty()) {
            System.out.println("Empty bundle");
            return;
        } else {
            System.out.println("Size of bundle is " + bundle.getEntry().size());
        }

        Patient pat = new Patient();
        Encounter enc = new Encounter();
        Observation obs = new Observation();

        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            Resource resource = entry.getResource();
            switch (resource.fhirType()) {
                case "Patient":
                    pat = (Patient) resource;
                    continue;
                case "Encounter":
                    enc = (Encounter) resource;
                    continue;
                case "Observation":
                    obs = (Observation) resource;
                    continue;
            }
            System.out.println(resource.fhirType());
        }

        Quantity quantity = (Quantity) obs.getValue();
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

        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ohdsi", "postgres", "postgres");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print(sql);
        }
    }
}

package query;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.JenaTransactionException;
import org.apache.jena.tdb.TDBFactory;

import java.util.ArrayList;
import java.util.List;

// Search for blood pressure readings over 120
public class HighBloodPressureQuery extends Query {
    private Dataset dataset = TDBFactory.createDataset("./tdb/" + inputTopicName);

    public HighBloodPressureQuery(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        List<String> outputMessageTexts = new ArrayList<>();

        Model model = dataset.getDefaultModel();
        dataset.begin(ReadWrite.READ);
        try {
            String prefix = "PREFIX fhir:<http://hl7.org/fhir>\n";
            String query = "SELECT ?x WHERE { ?x <fhir:Coding.code> '85354-9' }";
            QueryExecution queryExecution = QueryExecutionFactory.create(prefix + query, dataset);
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext()) {
                QuerySolution next = resultSet.next();

                outputMessageTexts.add(next.toString());

                // temporary debugging output
                System.out.println(next.toString());
            }
        } catch (JenaTransactionException e) {
            e.printStackTrace();
        } finally {
            dataset.end();
        }

        return outputMessageTexts;
    }
}

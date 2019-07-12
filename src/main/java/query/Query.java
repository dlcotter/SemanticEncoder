package query;

import common.ActiveMQEnabled;
import org.apache.jena.query.*;
import org.apache.jena.sparql.JenaTransactionException;
import org.apache.jena.tdb.TDBFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class Query extends ActiveMQEnabled {
    Dataset dataset;
    String query;

    Query(String inputTopicName, String outputTopicName, String query) {
        this(inputTopicName, outputTopicName);

        this.query = query;
    }

    Query(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);

        this.dataset = TDBFactory.createDataset("./tdb/" + inputTopicName);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        List<String> outputMessageTexts = new ArrayList<>();

        dataset.getDefaultModel();
        dataset.begin(ReadWrite.READ);
        try {
            QueryExecution queryExecution = QueryExecutionFactory.create(query, dataset);
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext()) {
                QuerySolution next = resultSet.next();
                outputMessageTexts.add(toString());

                if (this.printMessageContents)
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

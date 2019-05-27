package query;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;

import java.util.ArrayList;
import java.util.List;

// Passes through all messages to the next message queue
public class PassThroughQuery extends Query {
    private Dataset dataset = TDBFactory.createDataset("./tdb/" + inputTopicName);

    public PassThroughQuery(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        List<String> outputMessageTexts = new ArrayList<>();

        Model model = dataset.getDefaultModel();
        dataset.begin( ReadWrite.READ );
        try {
            StmtIterator it = model.listStatements();
            while (it.hasNext()) {
                Statement stmt = it.next();
                outputMessageTexts.add(inputMessageText);
            }
            dataset.commit();
        }
        finally
        {
            dataset.end();
        }

        return outputMessageTexts;
    }
}

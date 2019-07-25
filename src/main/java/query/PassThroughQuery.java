package query;

import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;

import java.util.ArrayList;
import java.util.List;

// Passes through all messages to the next message queue
public class PassThroughQuery extends Query {
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
                outputMessageTexts.add(inputMessageText);

                // temporary debugging output
                System.out.println(inputMessageText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataset.end();
        }

        return outputMessageTexts;
    }
}

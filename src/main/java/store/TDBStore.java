package store;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TDBStore extends Store {
    private Dataset dataset = TDBFactory.createDataset("./tdb/" + outputTopicName);

    public TDBStore(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        // Open dataset transaction in write mode
        dataset.begin(ReadWrite.WRITE);

        try {
            // Deserialize message to model
            Reader messageReader = new StringReader(inputMessageText);
            Model model = dataset.getDefaultModel();
            model.read(messageReader, null, "TURTLE"); // write model out as Turtle (RDF shorthand)
            dataset.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close dataset transaction
            dataset.end();
        }

        // Send message onward
        List<String> outputMessageTexts = new ArrayList<>();
        outputMessageTexts.add(inputMessageText);

        return outputMessageTexts;
    }
}

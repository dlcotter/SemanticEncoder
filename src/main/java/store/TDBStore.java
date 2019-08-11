package store;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.shared.Lock;
import org.apache.jena.tdb.TDBFactory;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TDBStore extends Store {
    private Dataset dataset = TDBFactory.createDataset("./tdb/" + outputTopicName);

    public TDBStore(String inputTopicName) {
        super(inputTopicName, null);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        // Open data set transaction in write mode
        Model model = dataset.getDefaultModel();
        dataset.begin(ReadWrite.WRITE);
        model.enterCriticalSection(Lock.WRITE);
        try {
            // Deserialize message to model
            Reader messageReader = new StringReader(inputMessageText);
            model.read(messageReader, null, "TURTLE"); // write model out as Turtle (RDF shorthand)
            dataset.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            model.leaveCriticalSection() ;
            dataset.end();
        }

        return new ArrayList<>();
    }
}

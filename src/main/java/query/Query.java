package query;

import comm.ActiveMQEnabled;
import org.apache.jena.query.Dataset;
import org.apache.jena.tdb.TDBFactory;

import java.util.ArrayList;
import java.util.List;

abstract class Query extends ActiveMQEnabled implements IQuery {
    Dataset dataset;

    Query(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);

        dataset = TDBFactory.createDataset("./tdb/" + inputTopicName);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        List<String> outputMessageTexts = new ArrayList<>();
        outputMessageTexts.add(inputMessageText);

        return outputMessageTexts;
    }
}

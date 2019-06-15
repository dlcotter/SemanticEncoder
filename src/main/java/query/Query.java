package query;

import common.ActiveMQEnabled;
import org.apache.jena.query.Dataset;
import org.apache.jena.tdb.TDBFactory;

abstract class Query extends ActiveMQEnabled implements IQuery {
    Dataset dataset;

    Query(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);

        dataset = TDBFactory.createDataset("./tdb/" + inputTopicName);
    }
}

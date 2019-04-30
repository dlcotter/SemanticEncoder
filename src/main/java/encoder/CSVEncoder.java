package encoder;

import org.apache.jena.rdf.model.Model;

public class CSVEncoder extends Encoder {
    public CSVEncoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    Model buildModel(String message) {
        return null;
    }
}

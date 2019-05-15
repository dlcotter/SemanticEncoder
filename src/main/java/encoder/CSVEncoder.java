package encoder;

import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.List;

public class CSVEncoder extends Encoder implements IEncoder {
    public CSVEncoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    public List<Model> buildModel(String message) {
        return new ArrayList<>();
    }
}

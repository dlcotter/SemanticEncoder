package encoder;

import org.apache.jena.rdf.model.Model;

import java.util.List;

public class FHIREncoder extends Encoder implements IEncoder {
    public FHIREncoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    public List<Model> buildModel(String message) {
        return null;
    }
}

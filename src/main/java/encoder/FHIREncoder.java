package encoder;

import org.apache.jena.rdf.model.Model;

public class FHIREncoder extends Encoder {
    public FHIREncoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    Model buildModel(String message) {
        return null;
    }
}

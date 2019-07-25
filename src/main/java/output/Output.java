package output;

import common.ActiveMQEnabled;

import java.util.ArrayList;
import java.util.List;

public abstract class Output extends ActiveMQEnabled {
    protected static final String OUTPUT_DIRECTORY = "./output/";

    Output(String inputTopicName) {
        super(inputTopicName, null);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        // An output doesn't actually do anything to the input text, but it needs to return something in order to conform
        // to the expected pattern used by the superclass common.ActiveMQEnabled, so we'll just return an empty List<String>
        return new ArrayList<>();
    }
}

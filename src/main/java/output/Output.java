package output;

import common.ActiveMQEnabled;

import java.util.ArrayList;
import java.util.List;

public abstract class Output extends ActiveMQEnabled {
    Output(String inputTopicName) {
        super(inputTopicName, null);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        return new ArrayList<>();
    }
}

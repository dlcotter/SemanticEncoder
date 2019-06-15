package output;

import common.ActiveMQEnabled;

import java.util.ArrayList;
import java.util.List;

abstract class Output extends ActiveMQEnabled implements IOutput {
    Output(String inputTopicName) {
        super(inputTopicName, null);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        return new ArrayList<>();
    }
}

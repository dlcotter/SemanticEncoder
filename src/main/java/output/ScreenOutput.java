package output;

import common.ActiveMQEnabled;

import java.util.List;

public class ScreenOutput extends ActiveMQEnabled {
    public ScreenOutput(String inputTopicName) {
        super(inputTopicName, null);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        System.out.print(inputMessageText);

        return super.processInputText(inputMessageText);
    }
}
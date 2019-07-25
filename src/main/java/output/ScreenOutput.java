package output;

import java.util.ArrayList;
import java.util.List;

public class ScreenOutput extends Output {
    public ScreenOutput(String inputTopicName) {
        super(inputTopicName);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        System.out.print(inputMessageText);

        return new ArrayList<>();
    }
}
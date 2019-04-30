package output;

public class ScreenOutput extends Output {
    public ScreenOutput(String inputTopicName) {
        super(inputTopicName);
    }

    @Override
    void handleMessage(String inMessageText) {
        System.out.print(inMessageText);
    }
}

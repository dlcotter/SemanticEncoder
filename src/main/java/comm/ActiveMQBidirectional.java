package comm;

public abstract class ActiveMQBidirectional extends ActiveMQEnabled {
    public ActiveMQBidirectional(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }
}
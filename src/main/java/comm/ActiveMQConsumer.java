package comm;

public abstract class ActiveMQConsumer extends ActiveMQEnabled {
    public ActiveMQConsumer(String inputTopicName){
        super(inputTopicName, null);
    }
}
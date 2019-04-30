package comm;

public abstract class ActiveMQProducer extends ActiveMQEnabled {
    public ActiveMQProducer(String outputTopicName){
        super(null, outputTopicName);
    }
}
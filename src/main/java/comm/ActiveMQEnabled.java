package comm;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public abstract class ActiveMQEnabled {
    private ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    private Connection connection;
    protected Session session;
    protected MessageConsumer consumer;
    protected MessageProducer producer;
    protected String inputTopicName, outputTopicName;
    private Destination inputDestination, outputDestination;

    ActiveMQEnabled(String inputTopicName, String outputTopicName){
        if (inputTopicName == null && outputTopicName == null)
            return; // should throw bad input exception here

        this.inputTopicName = inputTopicName;
        this.outputTopicName = outputTopicName;

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false /* ? */, Session.AUTO_ACKNOWLEDGE);

            if (inputTopicName != null) {
                inputDestination = session.createTopic(inputTopicName);
                consumer = session.createConsumer(inputDestination);
            }

            if (outputTopicName != null) {
                outputDestination = session.createTopic(outputTopicName);
                producer = session.createProducer(outputDestination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            }
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
}

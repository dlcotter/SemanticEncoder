package comm;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Date;
import java.util.List;

public abstract class ActiveMQEnabled {
    private ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    private Connection connection;
    protected Session session;
    protected MessageConsumer consumer;
    protected MessageProducer producer;
    protected String inputTopicName, outputTopicName;
    private Destination inputDestination, outputDestination;
    protected boolean debug = true;

    public ActiveMQEnabled(String inputTopicName, String outputTopicName){
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
                consumer.setMessageListener(incomingMessageHandler);
            }

            if (outputTopicName != null) {
                outputDestination = session.createTopic(outputTopicName);
                producer = session.createProducer(outputDestination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void printMessageDebugInfo(TextMessage textMessage) {
        if (debug) {
            System.out.println("Time: " + new Date());
            System.out.println("Input topic: " + inputTopicName);
            System.out.println("Output topic: " + outputTopicName);
            System.out.println("Class: " + this.getClass());
            System.out.println("Message hash: " + textMessage.hashCode());
            System.out.println("Current thread: " + Thread.currentThread().getName());
            System.out.println();
        }
    }

    // To be implemented by child classes for use in incomingMessageHandler (below)
    protected abstract List<String> processInputText(String inputMessageText);

    private MessageListener incomingMessageHandler = new MessageListener() {
        @Override
        public void onMessage(Message inputMessage) {
            if (inputMessage == null || !(inputMessage instanceof TextMessage))
                return;

            // Process incoming message
            String inputMessageText = "";
            try {
                inputMessageText = ((TextMessage) inputMessage).getText();
            } catch(JMSException e) {
                e.printStackTrace();
            }

            if (inputMessageText.isEmpty())
                return;

            // Call child method to produce output messages from input
            List<String> outputMessageTexts = processInputText(inputMessageText);

            // Send output message(s)
            for (String outputMessageText : outputMessageTexts) {
                if (outputMessageText.isEmpty())
                    continue;

                TextMessage outMessage;
                try {
                    outMessage = session.createTextMessage(outputMessageText);
                    producer.send(outMessage);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}

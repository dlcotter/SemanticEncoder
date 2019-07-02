package common;

import logging.Logger;
import logging.VoidLogger;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.LocalTime;
import java.util.List;

public abstract class ActiveMQEnabled implements ILoggable {
    /* FIELDS */
    private ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    private Connection connection;
    protected Session session;
    protected MessageConsumer consumer;
    protected MessageProducer producer;
    protected String inputTopicName, outputTopicName;
    private Destination inputDestination, outputDestination;
    boolean logDebugInfo = true, printDebugInfo = false, includeMessageContents = false;
    private Logger logger = new VoidLogger();

    /* CONSTRUCTORS */
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* METHODS */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void logMessage(TextMessage textMessage, String receiptMode) {
        // Get the message ID
        String messageID;
        try {
             messageID = textMessage.getJMSMessageID();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Format the log entry
        String[] fields = new String[] {
                 String.valueOf(LocalTime.now())        // TIME
                ,this.getClass().getName()              // CLASS
                ,receiptMode                            // EVENT
                ,inputTopicName                         // INPUT_TOPIC
                ,outputTopicName                        // OUTPUT_TOPIC
                ,messageID                              // MSG_HASH
                ,Thread.currentThread().getName()       // THREAD
                //textMessage.getText()                 // message contents - not sure how to enable multiline message content debugging
        };
        String logInfo = String.join("|", fields);

        // Log to the subscribed destinations
        if (logDebugInfo)
            logger.info(logInfo);

        if (printDebugInfo)
            System.out.println(logInfo);
    }

    private MessageListener incomingMessageHandler = new MessageListener() {
        @Override
        public void onMessage(Message inputMessage) {
            // Die if wrong type of message
            if (!(inputMessage instanceof TextMessage))
                return;

            // Process incoming message
            String inputMessageText = "";
            try {
                inputMessageText = ((TextMessage) inputMessage).getText();
                logMessage((TextMessage)inputMessage, "recd");
            } catch(JMSException e) {
                e.printStackTrace();
            }

            // Die if empty message
            if (inputMessageText.isEmpty())
                return;

            // Call child method to produce output messages from input
            List<String> outputMessageTexts = processInputText(inputMessageText);

            // Send output message(s)
            for (String outputMessageText : outputMessageTexts) {
                if (outputMessageText.isEmpty())
                    continue;

                // Build output message
                TextMessage outputMessage;
                try {
                    outputMessage = session.createTextMessage(outputMessageText);
                    producer.send(outputMessage);
                    logMessage(outputMessage, "sent");
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    protected abstract List<String> processInputText(String inputMessageText); // to be implemented by child classes for use in incomingMessageHandler
}

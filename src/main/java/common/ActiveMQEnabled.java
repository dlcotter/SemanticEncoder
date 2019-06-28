package common;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public abstract class ActiveMQEnabled {
    private ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    private Connection connection;
    protected Session session;
    protected MessageConsumer consumer;
    protected MessageProducer producer;
    protected String inputTopicName, outputTopicName;
    private Destination inputDestination, outputDestination;
    private static final String logDirectory = "/home/dcotter/mscs/610-masters-project/logs/";
    boolean logDebugInfo = true, printDebugInfo = false, includeMessageContents = false;
    private FileHandler fh;
    private Logger logger;

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

        // Configure the file handler and text formatter
        String thisClassName = this.getClass().getName();
        try {
            fh = new FileHandler(logDirectory + thisClassName + ".txt", true /* append */);
            fh.setFormatter(new SimpleFormatter());
            logger = Logger.getLogger(thisClassName);
            logger.addHandler(fh);
            logger.info("TIME|CLASS|EVENT|INPUT_TOPIC|OUTPUT_TOPIC|MSG_HASH|THREAD");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    protected void finalize() {
        fh.close();
    }

    protected void logMessage(TextMessage textMessage, String receiptMode) {
        // Format the log entry
        String[] fields = new String[] {
                 String.valueOf(new Date())             // TIME
                ,this.getClass().getName()              // CLASS
                ,receiptMode                            // EVENT
                ,inputTopicName                         // INPUT_TOPIC
                ,outputTopicName                        // OUTPUT_TOPIC
                ,String.valueOf(textMessage.hashCode()) // MSG_HASH
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

    // To be implemented by child classes for use in incomingMessageHandler (below)
    protected abstract List<String> processInputText(String inputMessageText);

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
                logMessage((TextMessage)inputMessage, "caught");
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
}

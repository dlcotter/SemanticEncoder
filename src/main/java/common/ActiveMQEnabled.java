package common;

import logging.Logger;
import logging.VoidLogger;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.jetbrains.annotations.NotNull;

import javax.jms.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ActiveMQEnabled implements ILoggable {
    private ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    private Connection connection;
    protected Session session;
    private MessageConsumer consumer;
    protected MessageProducer producer;
    protected String inputTopicName, outputTopicName;
    private Destination inputDestination, outputDestination;
    private boolean logDebugInfo = true, printDebugInfo = false, printMessageContents = false;
    private Logger logger = new VoidLogger();
    protected HashMap<String,String> prefixes = new HashMap<>();

    public ActiveMQEnabled(String inputTopicName, String outputTopicName){
        if (inputTopicName == null && outputTopicName == null)
            return; // should throw bad input exception here

        this.inputTopicName = inputTopicName;
        this.outputTopicName = outputTopicName;

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false /* ? */, Session.AUTO_ACKNOWLEDGE);

            if (!(inputTopicName == null || inputTopicName.isEmpty())) {
                inputDestination = session.createTopic(inputTopicName);
                consumer = session.createConsumer(inputDestination);
                consumer.setMessageListener(incomingMessageHandler);
            }

            if (!(outputTopicName == null || outputTopicName.isEmpty())) {
                outputDestination = session.createTopic(outputTopicName);
                producer = session.createProducer(outputDestination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize prefix hash
        prefixes.put("rdf"   ,"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixes.put("rdfs"  ,"http://www.w3.org/2000/01/rdf-schema#");
        prefixes.put("owl"   ,"http://www.w3.org/2002/07/owl#");
        prefixes.put("xsd"   ,"http://www.w3.org/2001/XMLSchema#");
        prefixes.put("fhir"  ,"http://hl7.org/fhir/");
        prefixes.put("loinc" ,"http://loinc.org/rdf#");
        prefixes.put("sct"   ,"http://snomed.info/id#");
    }

    public void setLogger(Logger logger) {
        // Replaces the default placeholder VoidLogger with a logger that actually does something
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

            // Die if no output topic
            if (outputTopicName == null || outputTopicName.isEmpty())
                return;

            // Die if producer has not been initialized
            if (producer == null)
                return;

            // Send output messages
            try {
                sendOutputMessages(outputMessageTexts);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    protected void sendOutputMessages(@NotNull List<String> outputMessageTexts) throws Exception {
        if (session == null)
            throw new Exception("Session cannot be null");

        if (producer == null)
            throw new Exception("Producer cannot be null");

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

    // Default implementation; intended to be overridden by child classes for use in incomingMessageHandler
    protected List<String> processInputText(String inputMessageText) {
        return new ArrayList<>();
    }

    public boolean getLogDebugInfo() {
        return logDebugInfo;
    }

    public void setLogDebugInfo(boolean logDebugInfo) {
        this.logDebugInfo = logDebugInfo;
    }

    public boolean getPrintDebugInfo() {
        return printDebugInfo;
    }

    public void setPrintDebugInfo(boolean printDebugInfo) {
        this.printDebugInfo = printDebugInfo;
    }

    public boolean getPrintMessageContents() {
        return printMessageContents;
    }

    public void setPrintMessageContents(boolean printMessageContents) {
        this.printMessageContents = printMessageContents;
    }
}

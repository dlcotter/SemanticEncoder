package query;

import comm.ActiveMQBidirectional;

import javax.jms.JMSException;
import javax.jms.TextMessage;

abstract class Query extends ActiveMQBidirectional {
    Query(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);

        try {
            setMessageListener();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void setMessageListener() throws JMSException {
        consumer.setMessageListener((inMessage) -> {
            System.out.println(this.getClass() + " caught one.");
            if (!(inMessage instanceof TextMessage))
                return;

            String inMessageText = "";
            try {
                inMessageText = ((TextMessage) inMessage).getText();
            } catch(JMSException e) {
                e.printStackTrace();
            }
            if (inMessageText.isEmpty())
                return;

            /* Replace with message handler to be set by child classes */
            String outMessageText = inMessageText;

            TextMessage outMessage;
            try {
                outMessage = session.createTextMessage(outMessageText);
                producer.send(outMessage);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

}

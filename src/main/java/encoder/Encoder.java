package encoder;

import comm.ActiveMQBidirectional;
import org.apache.jena.rdf.model.Model;

import javax.jms.*;
import java.io.ByteArrayOutputStream;

abstract class Encoder extends ActiveMQBidirectional {
    private Model model;

    Encoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);

        try {
            this.setMessageListener();
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

            this.model = this.buildModel(inMessageText);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            this.model.write(byteArrayOutputStream);
            String outMessageText = new String(byteArrayOutputStream.toByteArray());
            if (outMessageText.isEmpty())
                return;

            TextMessage outMessage;
            try {
                outMessage = session.createTextMessage(outMessageText);
                producer.send(outMessage);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    abstract Model buildModel(String message);
}

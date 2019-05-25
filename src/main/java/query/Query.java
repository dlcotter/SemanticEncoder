package query;

import comm.ActiveMQBidirectional;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.tdb.TDBFactory;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.Reader;
import java.io.StringReader;

abstract class Query extends ActiveMQBidirectional {
    Dataset dataset;

    Query(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);

        dataset = TDBFactory.createDataset("./tdb");

        try {
            setMessageListener();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void setMessageListener() throws JMSException {
        consumer.setMessageListener((inMessage) -> {
//            dataset.begin(ReadWrite.WRITE);

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

            Model model = ModelFactory.createDefaultModel();
            Reader reader = new StringReader(inMessageText);
            model.read(reader, "TTL");

            /* Replace with message handler to be set by child classes */
            String outMessageText = inMessageText;

            TextMessage outMessage;
            try {
                outMessage = session.createTextMessage(outMessageText);
                producer.send(outMessage);
            } catch (JMSException e) {
                e.printStackTrace();
            }

//            dataset.commit();
//            dataset.end();
        });
    }

}

package query;

import comm.ActiveMQBidirectional;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
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

            Model model = null;
//            Model model = ModelFactory.createDefaultModel();
            model = dataset.getNamedModel( "m" );

            Reader reader = new StringReader(inMessageText);
            try {
                model.read(reader, "TTL");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // TDB transaction
            dataset.begin( ReadWrite.WRITE );
            try
            {
//                model = dataset.getNamedModel( "m" );
//
//                Statement stmt = model.createStatement
//                        (
//                                model.createResource( "s" ),
//                                model.createProperty( "p" ),
//                                model.createResource( "o" )
//                        );

//                model.add( stmt );
                dataset.commit();
            } catch(Exception e) {
                e.printStackTrace();
            }
            finally
            {
                dataset.end();
//                if( model != null ) model.close();

            }

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

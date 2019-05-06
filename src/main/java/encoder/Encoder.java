package encoder;

import ca.uhn.hl7v2.HL7Exception;
import comm.ActiveMQBidirectional;
import org.apache.jena.rdf.model.Model;

import javax.jms.*;
import java.io.ByteArrayOutputStream;

abstract class Encoder extends ActiveMQBidirectional {
    Encoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);

        try {
            this.setMessageListener();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    abstract Model buildModel(String message) throws HL7Exception;

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

            Model model;
            try {
                model = this.buildModel(inMessageText);
            } catch (HL7Exception e) {
                e.printStackTrace();
                return;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            model.write(byteArrayOutputStream, "TTL");
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
}

/*
Jena writer name 	RIOT RDFFormat
"TURTLE" 	        TURTLE
"TTL" 	            TURTLE
"Turtle" 	        TURTLE
"N-TRIPLES"         NTRIPLES
"N-TRIPLE" 	        NTRIPLES
"NT" 	            NTRIPLES
"JSON-LD" 	        JSONLD
"RDF/XML-ABBREV" 	RDFXML
"RDF/XML" 	        RDFXML_PLAIN
"N3" 	            N3
"RDF/JSON" 	        RDFJSON
 */
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import javax.jms.*;

class SemanticEncoder {
    private ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    private Connection connection;
    private Session session;
    private MessageProducer producer;

    // A semantic encoder has inputs and outputs. The inputs are ActiveMQ topics.
    // The outputs are also ActiveMQ topics. An input or output is registered by
    // calling the public methods addHL7Consumer() or addCEPProducer() with parameters indicating the format
    // of the stream (HL7v2 or FHIR) and the URL.
    public SemanticEncoder() {
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false /* ? */, Session.AUTO_ACKNOWLEDGE);
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public void addHL7Consumer() {
        try {
            Destination destination = session.createTopic("HL7");
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener((message) -> {
                if (!(message instanceof TextMessage))
                    return;
                TextMessage textMessage = (TextMessage) message;

                Model model;
                try {
                    model = ModelFactory.createDefaultModel();
                    HL7Converter converter = new HL7Converter(model);
                    converter.ConvertToRDF(textMessage.getText());
                    model.write(System.out);
                } catch (Exception e) {
                    System.out.println("Caught: " + e);
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public void addCEPProducer() {
        try {
            Destination destination = session.createTopic("CEP");
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            System.out.println("SemanticEncoder.addCEPProducer() successful");
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
}

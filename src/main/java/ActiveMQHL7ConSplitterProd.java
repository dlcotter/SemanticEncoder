//package Comm;
//
//import hl7.HL7Utils;
//import org.apache.activemq.ActiveMQConnectionFactory;
//
//import javax.jms.*;
//import java.util.List;
//
//public class ActiveMQHL7ConSplitterProd {
//
//    private HL7Utils hl7Utils = new HL7Utils();
//
//    public void start() {
//        try {
//            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
//            Connection connection = connectionFactory.createConnection();
//            connection.start();
//            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//
//            Destination source_destination = session.createTopic("HL7.VITALS");
//            Destination split_destination = session.createTopic("HL7.VITAL");
//
//            MessageProducer producer = session.createProducer(split_destination);
//            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
//
//            MessageConsumer consumer = session.createConsumer(source_destination);
//            consumer.setMessageListener(message -> {
//                try {
//                    if (message instanceof TextMessage) {
//                        TextMessage textMessage = (TextMessage) message;
//                        String hl7ORUString = textMessage.getText();
//                        List<String> oruStringList = hl7Utils.parseORURecordList(hl7ORUString);
//
//                        for(String oruString : oruStringList) {
//                            TextMessage out_message = session.createTextMessage(oruString);
//                            producer.send(out_message);
//                        }
//                    }
//                } catch(Exception ex) {
//                    ex.printStackTrace();
//                }
//            });
//        } catch (Exception e) {
//            System.out.println("Caught: " + e);
//            e.printStackTrace();
//        }
//    }
//
//}
//
//

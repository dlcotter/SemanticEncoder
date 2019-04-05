import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

class HL7Producer {
    private ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer producer;
    private HL7Generator hl7Generator = new HL7Generator();

    public HL7Producer() {
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false /* ? */, Session.AUTO_ACKNOWLEDGE);
            destination = session.createTopic("HL7");
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public void start() {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                try {
                    // Create HL7 message and add MD5 checksum as property
                    String hl7Message = hl7Generator.getRandomMessage();
                    TextMessage message = session.createTextMessage(hl7Message);
                    String md5Hash = getMD5Checksum(hl7Message);
                    message.setStringProperty("md5", md5Hash);

                    // Tell the producer to send the message
                    System.out.println("HL7Producer(): [Sent message] \n\t" + message.hashCode() + " : " + Thread.currentThread().getName() + " " + new Date());
                    producer.send(message);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        long delay  = 1000L;
        long period = 5000L;
        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    private String getMD5Checksum(String checkString) throws Exception {
        byte[] b = checkString.getBytes(Charset.forName("UTF-8"));
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
}
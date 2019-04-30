package input;

import comm.ActiveMQProducer;

import javax.jms.TextMessage;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

abstract class Input extends ActiveMQProducer {
    Input(String outputTopicName) {
        super(outputTopicName);
    }

    abstract String getNextMessage();

    public void start() {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                try {
                    // Create message and add MD5 checksum as property
                    String message = getNextMessage();
                    TextMessage textMessage = session.createTextMessage(message);
                    String md5Hash = getMD5Checksum(message);
                    textMessage.setStringProperty("md5", md5Hash);

                    // Tell the producer to send the message
                    System.out.println(outputTopicName + "Producer(): [Sent message] \n\t" + textMessage.hashCode() + " : " + Thread.currentThread().getName() + " " + new Date());
                    producer.send(textMessage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        /* parameterize these variables */
        long delay  = 1000L;
        long period = 5000L;
        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    private String getMD5Checksum(String checkString) {
        byte[] b = checkString.getBytes(Charset.forName("UTF-8"));
        StringBuilder result = new StringBuilder();

        for (byte value : b) {
            result.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
}

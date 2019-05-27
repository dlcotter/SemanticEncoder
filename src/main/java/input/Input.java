package input;

import comm.ActiveMQEnabled;

import javax.jms.TextMessage;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

abstract class Input extends ActiveMQEnabled implements IInput {
    Input(String outputTopicName) {
        super(null, outputTopicName);
    }

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
                    producer.send(textMessage);

                    // Print debug info
                    printMessageDebugInfo(textMessage, "sent");
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

    abstract String getNextMessage();

    @Override
    protected List<String> processInputText(String inputMessageText) {
        // Inputs never catch incoming messages, so they can just return an empty list
        return new ArrayList<>();
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

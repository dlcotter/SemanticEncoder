package input;


import common.ActiveMQEnabled;

import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Input extends ActiveMQEnabled {
    /* FIELDS */
    private boolean repeat = true;
    private long delay = 1000L, period = 1000L;

    /* CONSTRUCTORS */
    Input(String outputTopicName) {
        super(null, outputTopicName);
    }

    /* METHODS */
    public void start() {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                try {
                    // Create message
                    String message = getNextMessage();
                    if (message.isEmpty())
                        return;

                    // Tell the producer to send the message
                    TextMessage textMessage = session.createTextMessage(message);
                    producer.send(textMessage);

                    // Log debug info (not handled in superclass because there is no "message received" event to trigger handler
                    logMessage(textMessage, "sent");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        Timer timer = new Timer("Timer");
        if (repeat)
            timer.scheduleAtFixedRate(repeatedTask, delay, period);
        else
            timer.schedule(repeatedTask, delay);
    }

    abstract String getNextMessage();

    @Override
    protected List<String> processInputText(String inputMessageText) {
        // Inputs never catch incoming messages, so they can just return an empty list
        return new ArrayList<>();
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setPeriod(long period) {
        this.period = period;
    }
}

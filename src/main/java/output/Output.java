package output;

import comm.ActiveMQConsumer;

import javax.jms.JMSException;
import javax.jms.TextMessage;

abstract class Output extends ActiveMQConsumer {
    Output(String inputTopicName) {
        super(inputTopicName);

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

            handleMessage(inMessageText);
        });


    }

    abstract void handleMessage(String inMessageText);
}

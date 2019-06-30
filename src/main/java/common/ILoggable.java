package common;

import logging.Logger;

import javax.jms.TextMessage;

public interface ILoggable {
    void setLogger(Logger logger);
    void logMessage(TextMessage textMessage, String receiptMode);
}

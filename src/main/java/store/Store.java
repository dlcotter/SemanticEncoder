package store;

import common.ActiveMQEnabled;

public abstract class Store extends ActiveMQEnabled implements IStore {
    Store(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }
}

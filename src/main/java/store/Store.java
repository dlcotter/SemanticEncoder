package store;

import comm.ActiveMQEnabled;

import java.util.ArrayList;
import java.util.List;

public abstract class Store extends ActiveMQEnabled implements IStore {
    Store(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        return new ArrayList<>();
    }

    abstract void write();
}

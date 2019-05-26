package query;

public class PassthroughQuery extends Query {
    public PassthroughQuery(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }
}

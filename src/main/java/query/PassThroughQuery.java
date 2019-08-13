package query;

// Passes through all messages to the next message queue
public class PassThroughQuery extends Query {
    public PassThroughQuery(String outputTopicName) {
        super(outputTopicName);

        query = "SELECT * WHERE { ?s ?o ?p } LIMIT 100 OFFSET 0"; // + offset (define event handler in superclass to be overridden
    }
}

package query;

import common.ActiveMQEnabled;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.shared.Lock;
import org.apache.jena.tdb.TDBFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Query extends ActiveMQEnabled {
    protected String query;
    protected Dataset dataset = TDBFactory.createDataset("./tdb/");
    protected boolean repeat = true;
    protected long delay = 1000L, period = 1000L;

    Query(String outputTopicName) {
        super(null, outputTopicName);
    }

    public void start() {
        if (query == null || query.isEmpty())
            return;

        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                List<String> outputMessageTexts = new ArrayList<>();

                Model datasetModel = dataset.getDefaultModel();
                dataset.begin(ReadWrite.READ);
                datasetModel.enterCriticalSection(Lock.READ);
                try {
                    QueryExecution queryExecution = QueryExecutionFactory.create(query, dataset);
                    Model resultModel = queryExecution.execConstruct();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    RDFDataMgr.write(byteArrayOutputStream, resultModel, RDFFormat.TURTLE);
                    outputMessageTexts.add(new String(byteArrayOutputStream.toByteArray()));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    datasetModel.leaveCriticalSection() ;
                    dataset.end();
                }

                try {
                    sendOutputMessages(outputMessageTexts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Timer timer = new Timer("Timer");
        if (repeat)
            timer.scheduleAtFixedRate(repeatedTask, delay, period);
        else
            timer.schedule(repeatedTask, delay);
    }
}

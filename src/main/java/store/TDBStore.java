package store;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.tdb.TDBFactory;

import java.util.ArrayList;
import java.util.List;

public class TDBStore extends Store {
    private Dataset dataset = TDBFactory.createDataset("./tdb/" + outputTopicName);

    public TDBStore(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getDefaultModel();

            Resource s = model.createResource("s");
            Property p = model.createProperty("p");
            Resource o = model.createResource("o");

            Statement stmt = model.createStatement(s,p,o);
            model.add(stmt);

            dataset.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataset.end();
        }

        List<String> outputMessageTexts = new ArrayList<>();
        outputMessageTexts.add(inputMessageText);

        return outputMessageTexts;
    }
//          1. Set up the model
//            Model model = null;

//          a) From a factory method - working
//            model = ModelFactory.createDefaultModel();

//          b) From the dataset - working
//          model = dataset.getNamedModel("m");

//          2. Set up the reader
//              a) From a file - working
//            Reader fileReader = null;
//            try {
//                fileReader = new FileReader("observation-example-heart-rate.ttl");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }

//              b) From a message - working
//            Reader messageReader = null;
//            try {
//                messageReader = new StringReader(inMessageText);
//            } catch(Exception e) {
//                e.printStackTrace();
//            }

//          3. Populate the model
//              a) By direct construction - working
//            Statement stmt = model.createStatement
//                    (
//                            model.createResource( "s" ),
//                            model.createProperty( "p" ),
//                            model.createResource( "o" )
//                    );
//            model.add(stmt);

//              b) From a FileReader - not working
//          try {
//              model.read(fileReader, "XML");
//          } catch (Exception e) {
//              e.printStackTrace(); // ERROR | [line: 1, col: 1 ] Content is not allowed in prolog.
//          }

//          c) From a StringReader
//          try {
//              model.read(messageReader, "XML");
//          } catch (Exception e) {
//              e.printStackTrace(); // ERROR | [line: 1, col: 1 ] Content is not allowed in prolog.
//          }


}

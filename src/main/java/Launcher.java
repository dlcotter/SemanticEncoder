import encoder.HL7VitalSignsEncoder;
import encoder.IEncoder;
import input.HL7VitalSignsInput;
import input.IInput;
import output.IOutput;
import output.ScreenOutput;
import query.IQuery;
import query.PassthroughQuery;
import store.IStore;
import store.TDBStore;

public class Launcher {
    public static void main(String[]  args) {
        /* INPUTS */
        IInput p1 = new HL7VitalSignsInput("HL7");

        // Add HL7 producer p1
//        HL7Input p1 = new HL7Input("HL7");

        // Add FHIR producer p2
        // * Use the HAPITester class in the examples package to download FHIR
        // messages from the test server and post them to the message queue.

        /* ENCODERS */
        // Add HL7 encoder
//        HL7EncoderGeneric e1 = new HL7EncoderGeneric("HL7","EXCHANGE");
        IEncoder e1 = new HL7VitalSignsEncoder("HL7","TDB");

        /* STORE */
        IStore s1 = new TDBStore("TDB","PASSTHROUGH");

        /* QUERY ENGINE */
        // * Add Jena query engine
        IQuery q1 = new PassthroughQuery("PASSTHROUGH","SCREEN");

        /* OUTPUTS */
        // * Add screen consumer
        IOutput o1 = new ScreenOutput("SCREEN");

        // * Add CEP output
        // * Add OMOP output

        // Start sending messages after the pipeline is set up
        p1.start();
    }
}

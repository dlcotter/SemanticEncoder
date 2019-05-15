import encoder.HL7VitalSignsEncoder;
import encoder.IEncoder;
import input.HL7VitalSignsInput;
import output.ScreenOutput;
import query.Q1;

public class Launcher {
    public static void main(String[]  args) {
        /* INPUTS */
        HL7VitalSignsInput p1 = new HL7VitalSignsInput("HL7");

        // Add HL7 producer p1
//        HL7Input p1 = new HL7Input("HL7");

        // Add FHIR producer p2
        // * Use the HAPITester class in the examples package to download FHIR
        // messages from the test server and post them to the message queue.

        /* ENCODERS */
        // Add HL7 encoder
//        HL7EncoderGeneric e1 = new HL7EncoderGeneric("HL7","EXCHANGE");
        IEncoder e1 = new HL7VitalSignsEncoder("HL7","EXCHANGE");

        /* QUERY ENGINE */
        // * Add Jena query engine
        Q1 q1 = new Q1("EXCHANGE","SCREEN");

        /* OUTPUTS */
        // * Add screen consumer
        ScreenOutput screenOutput = new ScreenOutput("SCREEN");

        // * Add CEP consumer
        // * Add OMOP consumer

        // Start sending messages after the pipeline is set up
        p1.start();
    }
}

import input.HL7Input;
import encoder.HL7Encoder;
import query.Q1;
import output.ScreenOutput;

public class Launcher {
    public static void main(String[]  args) {
        /* INPUTS */

        // OBXSegmentInput p1 = new OBXSegmentInput("HL7");
        // * Construct an input message representing vital signs using HAPI

        // I couldn't get the OBX Segment input to work, so I'm using a simpler HL7 message
        // copied off of the HL7toRDF repo's README page. It works, but I would like to come
        // back to the OBX Segment input and get it working.

        // Add HL7 producer p1
        HL7Input p1 = new HL7Input("HL7");

        // Add FHIR producer p2
        // * Use the HAPITester class in the examples package to download FHIR
        // messages from the test server and post them to the message queue.

        /* ENCODERS */
        // Add HL7 encoder
        HL7Encoder e1 = new HL7Encoder("HL7","EXCHANGE");

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

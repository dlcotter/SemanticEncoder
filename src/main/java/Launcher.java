import encoder.HL7VitalSignsEncoder;
import encoder.IEncoder;
import input.HL7VitalSignsInput;
import input.IInput;
import output.IOutput;
import output.ScreenOutput;
import query.HighBloodPressureQuery;
import query.IQuery;
import store.IStore;
import store.TDBStore;

import java.util.ArrayList;
import java.util.List;

public class Launcher {
    public static void main(String[]  args) {
        /* INPUTS */
        List<IInput> inputs = new ArrayList<>();
        inputs.add(new HL7VitalSignsInput("INPUT.VITALS.HL7", HL7VitalSignsInput.SimulationMode.HYPOTENSION));
//        IInput input2 = new PipeDelimitedPatientsInput("INPUT.PATIENTS.CSV");
//        IInput input3 = new FHIREncounterInput("INPUT.ENCOUNTERS.FHIR");

        // Add FHIR producer p2
        // * Use the HAPITester class in the examples package to download FHIR
        // messages from the test server and post them to the message queue.

        /* ENCODERS */
        IEncoder e1 = new HL7VitalSignsEncoder("INPUT.VITALS.HL7","STORE.TDB");
//        IEncoder e2 = new PipeDelimitedPatientsEncoder("INPUT.PATIENTS.CSV","STORE.TDB");
//        IEncoder e3 = new FHIREncounterEncoder("INPUT.ENCOUNTERS.FHIR","STORE.TDB");

        /* STORE */
        IStore s1 = new TDBStore("STORE.TDB","QUERY");

        /* QUERY ENGINE */
//        IQuery q1 = new PassThroughQuery("QUERY","QUERY.PASSTHROUGH");
        IQuery q2 = new HighBloodPressureQuery("QUERY","QUERY.HIGH_BP");

        /* OUTPUTS */
//        IOutput o1 = new ScreenOutput("QUERY.PASSTHROUGH");
        IOutput o2 = new ScreenOutput("QUERY.HIGH_BP");
        // * Add file output
        // * Add CEP output
        // * Add OMOP output

        // Start sending messages after the pipeline is set up
//        input1.start();
        for (IInput input : inputs)
            input.start();
    }
}

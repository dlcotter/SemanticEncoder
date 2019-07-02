import common.ActiveMQEnabled;
import encoder.HL7VitalSignsEncoder;
import encoder.PipeDelimitedPatientsEncoder;
import input.HL7VitalSignsInput;
import input.Input;
import input.PipeDelimitedPatientsInput;
import logging.CSVLogger;
import logging.Logger;
import store.TDBStore;

import java.util.ArrayList;
import java.util.List;

public class Launcher {
    public static void main(String[]  args) {
        /* INPUTS */
        List<ActiveMQEnabled> components = new ArrayList<>();
        components.add(new PipeDelimitedPatientsInput("INPUT.PATIENTS.CSV"));
        //components.add(new FHIREncounterInput("INPUT.ENCOUNTERS.FHIR")); // Use  HAPITester class in the examples package to download FHIR messages from the test server and post them to the message queue.
        components.add(new HL7VitalSignsInput("INPUT.VITALS.HL7", HL7VitalSignsInput.SimulationMode.HYPOTENSION));

        /* ENCODERS */
        components.add(new PipeDelimitedPatientsEncoder("INPUT.PATIENTS.CSV","STORE.TDB"));
//        components.add(new FHIREncounterEncoder("INPUT.ENCOUNTERS.FHIR","STORE.TDB"));
        components.add(new HL7VitalSignsEncoder("INPUT.VITALS.HL7","STORE.TDB"));

        /* STORE */
        components.add(new TDBStore("STORE.TDB","QUERY"));

        /* QUERY ENGINE */
//        components.add(new PassThroughQuery("QUERY","QUERY.PASSTHROUGH"));
//        components.add(new HighBloodPressureQuery("QUERY","QUERY.HIGH_BP"));

        /* OUTPUTS */
//        components.add(new ScreenOutput("QUERY.PASSTHROUGH"));
//        components.add(new ScreenOutput("QUERY.HIGH_BP"));
        // * Add file output
        // * Add CEP output
        // * Add OMOP output

        // Set up loggers - passing the same logger w/same file handler to each component so that they log to a single file
        final String logDirectory = "/home/dcotter/mscs/610-masters-project/logs/";
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%6$s%n"); // make output one liner  %1$tF %1$tT %4$s %2$s
        Logger commonLogger = new CSVLogger("CommonLogger", logDirectory + "common.log");
        commonLogger.info("TIME|CLASS|EVENT|INPUT_TOPIC|OUTPUT_TOPIC|MSG_HASH|THREAD");

        for (ActiveMQEnabled component : components)
            component.setLogger(commonLogger);

        // Start sending messages after the pipeline is set up (not before)
        for (ActiveMQEnabled component : components)
            if (component instanceof Input)
                ((Input)component).start();
    }
}

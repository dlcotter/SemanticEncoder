import applications.EsperListener;
import common.ActiveMQEnabled;
import domain.Utils;
import encoder.FHIREncounterEncoder;
import encoder.HL7VitalSignsEncoder;
import encoder.PipeDelimitedPatientsEncoder;
import input.FHIREncounterInput;
import input.HL7VitalSignsInput;
import input.Input;
import input.PipeDelimitedPatientsInput;
import logging.CSVLogger;
import logging.Logger;
import output.FileOutput;
import query.HighBloodPressureQuery;
import query.PassThroughQuery;
import store.TDBStore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Launcher {
    private static final String LOG_DIRECTORY = "./logs/";
    private static final String LOG_LINE_FORMAT = "%5$s%6$s%n"; // make output one liner  %1$tF %1$tT %4$s %2$s

    public Launcher() {
    }

    public static void main(String[] args) {
        List<ActiveMQEnabled> components = new ArrayList<>();

        int option = 8;
        String mode = "common";

        switch (option) {
            // INPUT TESTS
            case 0:
                components = testPipeDelimitedPatientInput();
                mode = "testPipeDelimitedPatientInput";
                break;

            case 1:
                components = testFHIREncounterInput();
                mode = "testFHIREncounterInput";
                break;

            case 2:
                components = testHL7VitalSignsInput();
                mode = "testHL7VitalSignsInput";
                break;

            // ENCODER TESTS
            case 3:
                components = testPipeDelimitedPatientEncoder();
                mode = "testHL7VitalSignsInput";
                break;

            case 4:
                components = testFHIREncounterEncoder();
                mode = "testFHIREncounterEncoder";
                break;

            case 5:
                components = testHL7VitalSignsEncoder();
                mode = "testHL7VitalSignsEncoder";
                break;

            case 6:
                components = testTDBStore();
                mode = "testTDBStore";
                break;

            case 7:
                components = testPassthroughQuery();
                mode = "testPassthroughQuery";
                break;

            case 8:
                components = testHighBloodPressureQuery();
                mode = "testHighBloodPressureQuery";
                break;
        }

        // Attach loggers - passing the same logger w/same file handler to each component so that they log to a single file
        System.setProperty("java.util.logging.SimpleFormatter.format", LOG_LINE_FORMAT);
        Logger commonLogger = new CSVLogger("CommonLogger", LOG_DIRECTORY + Utils.now("yyyyMMddhhmmss") + "." + mode  + ".log");
        commonLogger.info("TIME|CLASS|EVENT|INPUT_TOPIC|OUTPUT_TOPIC|MSG_HASH|THREAD");
        for (ActiveMQEnabled component : components)
            component.setLogger(commonLogger);

        // Start sending messages after the pipeline is set up (not before)
        for (ActiveMQEnabled component : components)
            if (component instanceof Input)
                ((Input)component).start();
    }

    // INPUT TESTS
    private static List<ActiveMQEnabled> testPipeDelimitedPatientInput() {
        List<ActiveMQEnabled> components = new ArrayList<>();
        components.add(new PipeDelimitedPatientsInput("INPUT.PATIENTS.PIPE_DELIMITED"));
        components.add(new FileOutput("INPUT.PATIENTS.PIPE_DELIMITED"));

        return components;
    }

    private static List<ActiveMQEnabled> testFHIREncounterInput() {
        List<ActiveMQEnabled> components = new ArrayList<>();
        components.add(new FHIREncounterInput("INPUT.ENCOUNTERS.FHIR"));
        components.add(new FileOutput("INPUT.ENCOUNTERS.FHIR"));

        return components;
    }

    private static List<ActiveMQEnabled> testHL7VitalSignsInput() {
        List<ActiveMQEnabled> components = new ArrayList<>();
        components.add(new HL7VitalSignsInput(HL7VitalSignsInput.SimulationMode.NORMAL, "INPUT.OBSERVATIONS.HL7"));
        components.add(new FileOutput("INPUT.OBSERVATIONS.HL7"));

        return components;
    }

    // ENCODER TESTS
    private static List<ActiveMQEnabled> testPipeDelimitedPatientEncoder() {
        List<ActiveMQEnabled> components = new ArrayList<>();
        components.add(new PipeDelimitedPatientsInput("INPUT.PATIENTS.PIPE_DELIMITED"));
        components.add(new PipeDelimitedPatientsEncoder("INPUT.PATIENTS.PIPE_DELIMITED","ENCODED.PATIENTS"));
        components.add(new FileOutput("ENCODED.PATIENTS"));

        return components;
    }

    private static List<ActiveMQEnabled> testFHIREncounterEncoder() {
        List<ActiveMQEnabled> components = new ArrayList<>();
        components.add(new FHIREncounterInput("INPUT.ENCOUNTERS.FHIR"));
        components.add(new FHIREncounterEncoder("INPUT.ENCOUNTERS.FHIR","ENCODED.ENCOUNTERS"));
        components.add(new FileOutput("ENCODED.ENCOUNTERS"));

        return components;
    }

    private static List<ActiveMQEnabled> testHL7VitalSignsEncoder() {
        List<ActiveMQEnabled> components = new ArrayList<>();
        components.add(new HL7VitalSignsInput(HL7VitalSignsInput.SimulationMode.NORMAL, "INPUT.OBSERVATIONS.HL7"));
        components.add(new HL7VitalSignsEncoder("INPUT.OBSERVATIONS.HL7","ENCODED.VITALS"));
        components.add(new FileOutput("ENCODED.VITALS"));

        return components;
    }

    // STORE/QUERY TESTS
    private static List<ActiveMQEnabled> testTDBStore() {
        List<ActiveMQEnabled> components = new ArrayList<>();
        components.add(new HL7VitalSignsInput(HL7VitalSignsInput.SimulationMode.NORMAL, "INPUT.OBSERVATIONS.HL7"));
        components.add(new HL7VitalSignsEncoder("INPUT.OBSERVATIONS.HL7","ENCODED.VITALS"));
        components.add(new TDBStore("ENCODED.VITALS", "QUERY"));
        components.add(new FileOutput("QUERY"));

        return components;
    }

    private static List<ActiveMQEnabled> testPassthroughQuery() {
        List<ActiveMQEnabled> components = new ArrayList<>();
        // The patients list, which is only ten elements, makes for a better test data set than, say, observations
        // which continually grows and thus overwhelms the capacity of the "return all" query to handle its volume.
        components.add(new PipeDelimitedPatientsInput("INPUT.PATIENTS.PIPE_DELIMITED"));
        components.add(new PipeDelimitedPatientsEncoder("INPUT.PATIENTS.PIPE_DELIMITED","ENCODED.PATIENTS"));
        components.add(new TDBStore("ENCODED.PATIENTS", "QUERY"));
        components.add(new PassThroughQuery("QUERY", "RESULTS.ALL"));
        components.add(new FileOutput("RESULTS.ALL"));

        return components;
    }

    private static List<ActiveMQEnabled> testHighBloodPressureQuery() {
        List<ActiveMQEnabled> components = new ArrayList<>();
        components.add(new HL7VitalSignsInput(HL7VitalSignsInput.SimulationMode.HYPOTENSION, "INPUT.OBSERVATIONS.HL7"));
        components.add(new HL7VitalSignsEncoder("INPUT.OBSERVATIONS.HL7","ENCODED.VITALS"));
        components.add(new TDBStore("ENCODED.VITALS", "QUERY"));
        components.add(new HighBloodPressureQuery("QUERY", "RESULTS.HIGH_BP"));
        components.add(new FileOutput("RESULTS.HIGH_BP"));

        return components;
    }

    // FULL PIPELINE TESTS
    private static List<ActiveMQEnabled> runCEPPipeline() {
        List<ActiveMQEnabled> components = new ArrayList<>();
        components.add(new HL7VitalSignsInput(HL7VitalSignsInput.SimulationMode.HYPOTENSION, "INPUT.VITALS.HL7"));
        components.add(new HL7VitalSignsEncoder("INPUT.VITALS.HL7","STORE.TDB"));
        components.add(new TDBStore("STORE.TDB","QUERY"));
        components.add(new HighBloodPressureQuery("QUERY","QUERY.HIGH_BP"));
        components.add(new EsperListener("QUERY.HIGH_BP", "OUTPUTS.HIGH_BP"));

        return components;
    }
}

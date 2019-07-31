import applications.EsperListener;
import common.ActiveMQEnabled;
import encoder.HL7VitalSignsEncoder;
import input.FHIREncounterInput;
import input.HL7VitalSignsInput;
import input.Input;
import input.PipeDelimitedPatientsInput;
import logging.CSVLogger;
import logging.Logger;
import output.FileOutput;
import query.HighBloodPressureQuery;
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

        int option = 1;
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
                components = exportFHIRforValidation();
                mode = "exportFHIRforValidation";
                break;
            case 4:
                components = runCEPPipeline();
                mode = "runCEPPipeline";
                break;
        }

        // Attach loggers - passing the same logger w/same file handler to each component so that they log to a single file
        System.setProperty("java.util.logging.SimpleFormatter.format", LOG_LINE_FORMAT);
        String localDateTime = DateTimeFormatter.ofPattern("yyyyMMddhhmmss", Locale.ENGLISH).format(LocalDateTime.now());
        Logger commonLogger = new CSVLogger("CommonLogger", LOG_DIRECTORY + localDateTime + "." + mode  + ".log");
        commonLogger.info("TIME|CLASS|EVENT|INPUT_TOPIC|OUTPUT_TOPIC|MSG_HASH|THREAD");
        for (ActiveMQEnabled component : components)
            component.setLogger(commonLogger);

        // Start sending messages after the pipeline is set up (not before)
        for (ActiveMQEnabled component : components)
            if (component instanceof Input)
                ((Input)component).start();
    }

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

    private static List<ActiveMQEnabled> exportFHIRforValidation() {
        List<ActiveMQEnabled> components = new ArrayList<>();
        HL7VitalSignsInput hl7VitalSignsInput = new HL7VitalSignsInput(HL7VitalSignsInput.SimulationMode.NORMAL, "INPUT.VITALS.HL7");
        hl7VitalSignsInput.setRepeat(false);
        components.add(hl7VitalSignsInput);
        components.add(new HL7VitalSignsEncoder("INPUT.VITALS.HL7","OUTPUT.FILE"));
        components.add(new FileOutput("OUTPUT.FILE"));

        return components;
    }

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

//        components.add(new PipeDelimitedPatientsInput("INPUT.PATIENTS.CSV"));
//        components.add(new FHIREncounterInput("INPUT.ENCOUNTERS.FHIR")); // Use  HAPITester class in the examples package to download FHIR messages from the test server and post them to the message queue.
//         idea: pick the simulation randomly (or based on CLI args) and detect via CEP app
//        components.add(new PipeDelimitedPatientsEncoder("INPUT.PATIENTS.CSV","STORE.TDB"));
//        components.add(new FHIREncounterEncoder("INPUT.ENCOUNTERS.FHIR","STORE.TDB"));
//        components.add(new PassThroughQuery("QUERY","QUERY.PASSTHROUGH"));
//            components.add(new PatientsByGenderQuery("QUERY","QUERY.PATIENTS_BY_GENDER"));
// OMOP application
/* OUTPUTS - still needed??? seems duplicate of logging functionality */
//        components.add(new ScreenOutput("OUTPUTS.HIGH_BP"));

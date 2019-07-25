package input;

import domain.Patient;

import java.util.List;

public class PipeDelimitedPatientsInput extends Input {
    private List<Patient> samplePatients;
    private int samplePatientCount;

    public PipeDelimitedPatientsInput(String outputTopicName) {
        super(outputTopicName);

        // Initialize sample patients list and counter
        samplePatients = Patient.getSamplePatients();
        samplePatientCount = samplePatients.size();

        // Iterate through list, i.e. repeat, until list is exhausted
        this.setRepeat(true);
    }

    @Override
    public String getNextMessage() {
        // Stop after the sample list of ten patients runs out
        if (samplePatientCount == 0)
            return "";

        String message = "";
        try {
            Patient patient = samplePatients.get(samplePatientCount-1);
            samplePatientCount--;
            message = patient.identifier + "|" + patient.name + "|" + patient.birthDate + "|" + patient.gender;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }
}
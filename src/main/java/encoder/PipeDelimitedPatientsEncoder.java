package encoder;

import domain.Patient;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.List;

public class PipeDelimitedPatientsEncoder extends Encoder {
    public PipeDelimitedPatientsEncoder(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
    }

    @Override
    public List<Model> buildModel(String message) {
        List<Model> models = new ArrayList<>();

        if (message == null || message.isEmpty())
            return models;

        String[] fields = message.split("|");

        Patient patient = new Patient() {{ identifier = fields[0]; name = fields[1]; birthDate = fields[2]; gender = fields[3]; }};
        models.add(encodePatient(patient));

        return models;
    }
}

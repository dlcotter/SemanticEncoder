package output;

import domain.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class FileOutput extends Output {
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;

    public FileOutput(String inputTopicName) {
        super(inputTopicName);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        // local date/time needs to be declared here, rather than in class fields, so that it changes
        // each time it receives a message and therefore writes a unique file per resource
        try {
            fileWriter = new FileWriter(OUTPUT_DIRECTORY + Utils.now("A") + "." + inputTopicName + ".ttl", true /* append */);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(inputMessageText);
            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return super.processInputText(inputMessageText);
    }
}
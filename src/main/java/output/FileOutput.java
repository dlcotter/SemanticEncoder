package output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        String localDateTime = DateTimeFormatter.ofPattern("A" /* ms of day */, Locale.ENGLISH).format(LocalDateTime.now());

        try {
            fileWriter = new FileWriter(OUTPUT_DIRECTORY + localDateTime + "." + inputTopicName + ".ttl", true /* append */);
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
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
    private String localDateTime = DateTimeFormatter.ofPattern("yyyyMMddhhmmss", Locale.ENGLISH).format(LocalDateTime.now());

    public FileOutput(String inputTopicName) {
        super(inputTopicName);
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
        try {
            fileWriter = new FileWriter(OUTPUT_DIRECTORY + localDateTime + "." + inputTopicName + ".txt", true /* append */);
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
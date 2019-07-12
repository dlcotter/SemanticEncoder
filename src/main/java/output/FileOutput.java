package output;

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
        try {
            fileWriter = new FileWriter("./output.txt");
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
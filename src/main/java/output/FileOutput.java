package output;

import common.ActiveMQEnabled;
import common.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class FileOutput extends ActiveMQEnabled {
    private static final String OUTPUT_DIRECTORY = "./output/";
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;

    public FileOutput(String inputTopicName) {
        super(inputTopicName, null);
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
        }

        return super.processInputText(inputMessageText);
    }
}
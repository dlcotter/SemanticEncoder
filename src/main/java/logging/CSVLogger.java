package logging;

import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class CSVLogger extends Logger {
    java.util.logging.Logger logger;
    FileHandler fileHandler;

    public CSVLogger(String loggerName, String logFilePath) {
        try {
            fileHandler = new FileHandler(logFilePath, true /* append */);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        fileHandler.setFormatter(new SimpleFormatter());
        logger = java.util.logging.Logger.getLogger(loggerName);
        logger.addHandler(fileHandler);
    }

    @Override
    public void info(String infoString) {
        logger.info(infoString);
    }

    protected void finalize() {
        fileHandler.close();
    }
}

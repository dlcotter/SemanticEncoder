package logging;

// Just a placeholder for classes that don't want to implement logging, since ActiveMQEnabled calls logging
public class VoidLogger extends Logger {
    @Override
    public void info(String infoString) {
        return;
    }
}

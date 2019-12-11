package utils;

public class Logger {
    private static Logger logger = new Logger();

    public static Logger inst() {
        return logger;
    }

    public void debug(String text) {
        System.out.println(text);
    }

    public void info(String text) {
        System.out.println(text);
    }

    public void error(String text) {
        System.err.println(text);
    }
}

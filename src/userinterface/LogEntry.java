package userinterface;

public class LogEntry {

    // The local time that the log was created.
    public final long time;
    // The full log entry as a single line where the field values are separated by commas.
    public final String entry;

    public LogEntry(long time, String entry) {
        this.time = time;
        this.entry = entry;
    }
}

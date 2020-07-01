package misc;

import network.Request;
import network.Response;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the logging mechanism. In the network stack, each layer is supplied with a logger
 * that it uses to log the particular events happening at that layer. Each layer needs to be supplied
 * with the same Logger instance for this to work correctly.
 */
public class Logger {

    public enum Event {

        SENT("sent"),
        RECEIVED("received"),
        PROCESS("process"),
        LOCATION("location");

        private final String str;
        Event(String str) {
            this.str = str;
        }
        @Override
        public String toString() {
            return str;
        }
    }

    public enum Mode {

        AUTH("authenticated"),
        UNAUTH("unauthenticated");

        private final String str;

        Mode(String str) {
            this.str = str;
        }
        @Override
        public String toString() {
            return str;
        }
    }

    public enum Phase {

        REGISTRATION("registration"),
        CONSTRUCTION("construction"),
        GUARD_ASSIGNMENT("guard_assignment"),
        SEARCH("search"),
        UNKNOWN("unknown");

        private final String str;

        Phase(String str) {
            this.str = str;
        }
        @Override
        public String toString() {
            return str;
        }
    }

    private BufferedWriter writer;
    // Whether this logger is invalid or not. An invalid logger does not write anything to the disk.
    private boolean invalid;
    // Maps a local event type to its phase.
    private final Map<String, Phase> typePhases;
    // Maps a local event type to its mode.
    private final Map<String, Mode> typeModes;
    // The local address in which the logger is constructed.
    private final String address;

    public Logger(String address) {
        typePhases = new HashMap<>();
        typeModes = new HashMap<>();
        this.address = address;
        invalid = false;
        try {
            // Get the appropriate log file.
            File file = new File("logs/" + address.replace(':', '_') + "_logs.csv");
            // Create the logs directory if not present.
            file.getParentFile().mkdirs();
            // Create the file writer.
            FileWriter fileWriter = new FileWriter(file, false);
            // Create a buffered writer from the file writer.
            writer = new BufferedWriter(fileWriter);
            // Put the column titles.
            writeLogToFile("msg_id;event;address;mode;phase;type;msg_size;time");
        } catch (Exception e) {
            // Disable the logger.
            invalid = true;
            System.err.println("[Logger] Error while creating the logger. Logging is disabled.");
            e.printStackTrace();
        }
    }

    /**
     * Used to construct an invalid logger. An invalid logger discards the
     * received logs as opposed to writing it to a file.
     */
    public Logger() {
        invalid = true;
        typeModes = null;
        typePhases = null;
        address = null;
    }

    public void logRequestSent(Request request) {
        if(invalid) return;
        String messageType = request.type.str + "_request";
        long messageSize = 0;
        logMessage(request.id, Event.SENT, request.sourceAddress, (request.auth) ? Mode.AUTH : Mode.UNAUTH,
                request.phase, messageType, messageSize, System.currentTimeMillis());
    }

    public void logRequestReceived(Request request) {
        if(invalid) return;
        String messageType = request.type.str + "_request";
        long messageSize = 0;
        logMessage(request.id, Event.RECEIVED, request.destinationAddress, (request.auth) ? Mode.AUTH : Mode.UNAUTH,
                request.phase, messageType, messageSize, System.currentTimeMillis());
    }

    public void logResponseSent(Response response, Request request) {
        if(invalid) return;
        String messageType = request.type.str + "_response";
        long messageSize = 0;
        logMessage(response.id, Event.SENT, response.sourceAddress, (request.auth) ? Mode.AUTH : Mode.UNAUTH,
                request.phase, messageType, messageSize, System.currentTimeMillis());
    }

    public void logResponseReceived(Response response, Request request) {
        if(invalid) return;
        String messageType = request.type.str + "_response";
        long messageSize = 0;
        logMessage(response.id, Event.RECEIVED, response.destinationAddress, (request.auth) ? Mode.AUTH : Mode.UNAUTH,
                request.phase, messageType, messageSize, System.currentTimeMillis());
    }

    /**
     * Maps a local event type to its mode and phase. During the logging of local events, modes and phases
     * will not need to be provided.
     * @param type the new event type.
     * @param mode the mode corresponding to the event.
     * @param phase the phase corresponding to the event.
     */
    public void registerLocalEvent(String type, Mode mode, Phase phase) {
        if(invalid) return;
        typePhases.put(type, phase);
        typeModes.put(type, mode);
    }

    public int logProcessStart(String type) {
        if(invalid) return -1;
        int processID = GlobalRand.rand.nextInt();
        String messageType =  type + "_start";
        Phase phase = typePhases.get(type);
        Mode mode = typeModes.get(type);
        if(phase == null || mode == null) {
            System.err.println("[Logger] Local event type " + type + " not registered.");
            return -1;
        }
        logMessage(processID, Event.PROCESS, address, mode, phase, messageType, 0, System.currentTimeMillis());
        return processID;
    }

    public void logProcessEnd(String type, int processID) {
        if(invalid) return;
        String messageType =  type + "_end";
        Phase phase = typePhases.get(type);
        Mode mode = typeModes.get(type);
        if(phase == null || mode == null) {
            System.err.println("[Logger] Local event type " + type + " not registered.");
            return;
        }
        logMessage(processID, Event.PROCESS, address, mode, phase, messageType, 0, System.currentTimeMillis());
    }

    private void logMessage(long msgID, Event event, String address, Mode mode, Phase phase, String type, long msgSize, long time) {
        if(invalid) return;
        String log = String.join(";", String.valueOf(msgID), event.toString(), address, mode.toString(),
                phase.toString(), type, String.valueOf(msgSize), String.valueOf(time));
        writeLogToFile(log);
    }

    private synchronized void writeLogToFile(String log) {
        if(invalid) return;
        try {
            writer.write(log);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[Logger] Could not write to the log file.");
            e.printStackTrace();
        }
    }

    public void close() {
        if(invalid) return;
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.err.println("[Logger] Could not close the log file.");
            e.printStackTrace();
        }
        // Invalidate the logger.
        invalid = true;
    }
}

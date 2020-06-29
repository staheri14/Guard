package misc;

import network.Request;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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

    public Logger(String address) {
        try {
            writer = new BufferedWriter(new FileWriter(address.replace(':', '_') + "_logs.csv", false));
            writeLogToFile("msg_id;event;address;mode;phase;type;msg_size;time");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logRequestSent(Request request) {
        String logType = request.type.str + "_sent";
        //logMessage(request.id, Event.SENT, request.senderAddress, );
    }

    public void logRequestReceived(Request request) {

    }

    public void logResponseSent(Request request) {

    }

    public void logResponseReceived(Request request) {

    }

    public void logProcessStart(String type) {

    }

    public void logProcessEnd(String type) {

    }

    private void logMessage(int msgID, Event event, String address, Mode mode, Phase phase, String type, long msgSize, long time) {
        String log = String.join(";", String.valueOf(msgID), event.toString(), address, mode.toString(),
                phase.toString(), type, String.valueOf(msgSize), String.valueOf(time));
        writeLogToFile(log);
    }

    private synchronized void writeLogToFile(String log) {
        try {
            writer.write(log);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

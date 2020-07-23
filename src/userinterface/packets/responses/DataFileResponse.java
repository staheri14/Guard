package userinterface.packets.responses;

import network.Response;

public class DataFileResponse extends Response {

    public final byte[] fileBytes;

    public DataFileResponse(String errorMessage, byte[] fileBytes) {
        super(errorMessage);
        this.fileBytes = fileBytes;
    }
}

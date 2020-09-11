package userinterface.workers;

import network.Layer;
import network.Response;
import userinterface.packets.requests.ReceiveDataRequest;
import userinterface.packets.responses.DataFileResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Retrieves the log file from the given target node and writes it into the given file path.
 */
public class RetrieveFileWorker implements Runnable {

    // The messages are sent through the invoker layer.
    public final Layer invoker;
    public final String filePath;
    public final String targetAddress;

    public RetrieveFileWorker(Layer invoker, String filePath, String targetAddress) {
        this.invoker = invoker;
        this.filePath = filePath;
        this.targetAddress = targetAddress;
    }

    @Override
    public void run() {
        System.out.println("Receiving logs from " + targetAddress + "...");
        // Acquire the log file from the target node.
        Response r = invoker.send(targetAddress, new ReceiveDataRequest());
        if(r.isError()) {
            System.err.println("Error during data retrieval: " + r.errorMessage + " from " + targetAddress);
        } else {
            // If acquired successfully,
            DataFileResponse dataFileResp = (DataFileResponse) r;
            System.out.println("Received logs from " + targetAddress + "!");
            try {
                File file = new File(filePath);
                file.getParentFile().mkdirs();
                Files.write(file.toPath(), dataFileResp.fileBytes);
            } catch (IOException e) {
                System.err.println("Could not write the file to the disk.");
                e.printStackTrace();
            }
        }
    }
}

package network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Packet {

    /**
     * Returns the size of the given serializable object in bytes.
     * @param obj the serializable object.
     * @return the size of the object in bytes.
     */
    public static int calculateSize(Serializable obj) {
        ByteArrayOutputStream bos;
        ObjectOutputStream oos;
        // Create the streams.
        try {
            bos = new ByteArrayOutputStream();
            // Object output stream will write to the byte array output stream.
            oos = new ObjectOutputStream(bos);
            // Write the object to the object output stream.
            oos.flush();
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException e) {
            System.err.println("[Packet] Could not serialize.");
            e.printStackTrace();
            return -1;
        }
        // Acquire the bytes from the byte array output stream.
        byte[] bytes = bos.toByteArray();
        // Close the streams.
        try {
            oos.close();
            bos.close();
        } catch (IOException e) {
            System.err.println("[Packet] Could not close the streams.");
            e.printStackTrace();
        }
        return bytes.length;
    }

}

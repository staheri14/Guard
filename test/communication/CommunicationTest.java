package communication;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import network.PingHandler;
import network.Response;
import network.packets.requests.PingRequest;
import network.packets.responses.AckResponse;

/**
 * This test generates two local communication layers and tests the connectivity between them.
 */
class CommunicationTest {

    static final int LOCAL_PORT = 9090;
    static final int REMOTE_PORT = 9091;

    static Communication localCommunication;
    static Communication remoteCommunication;

    /**
     * Creates two local communication layers.
     */
    @BeforeAll
    static void setUp() {
        localCommunication = new Communication(LOCAL_PORT);
        remoteCommunication = new Communication(REMOTE_PORT);
        Assertions.assertTrue(localCommunication.initializeHost(new PingHandler(localCommunication)));
        Assertions.assertTrue(remoteCommunication.initializeHost(new PingHandler(remoteCommunication)));
    }

    /**
     * Sends messages from `localCommunication` comm. layer to the `remoteCommunication` comm. layer and
     * asserts the correctness of the operation.
     */
    @Test
    void send() {
        String remoteAddress = remoteCommunication.getAddress();
        Response r1 = localCommunication.send(remoteAddress, new PingRequest(false));
        Assertions.assertNotNull(r1);
        Assertions.assertTrue(r1 instanceof AckResponse);
        Assertions.assertFalse(r1.isError());
        Response r2 = localCommunication.send(remoteAddress, new PingRequest(true));
        Assertions.assertNotNull(r2);
        Assertions.assertTrue(r2 instanceof AckResponse);
        Assertions.assertTrue(r2.isError());
    }
}
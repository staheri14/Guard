package protocol;

import middleware.Middleware;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import protocol.packets.requests.PingRequest;
import protocol.packets.responses.AckResponse;

class MiddlewareTest {

    static final int LOCAL_PORT = 9090;
    static final int REMOTE_PORT = 9091;

    static Middleware localMiddleware;
    static Middleware remoteMiddleware;

    @BeforeAll
    static void setUp() {
        localMiddleware = new Middleware(LOCAL_PORT);
        remoteMiddleware = new Middleware(REMOTE_PORT);
        Assertions.assertTrue(localMiddleware.initializeHost(new PingHandler(localMiddleware)));
        Assertions.assertTrue(remoteMiddleware.initializeHost(new PingHandler(remoteMiddleware)));
    }

    @Test
    void send() {
        String remoteAddress = remoteMiddleware.getAddress();
        Response r1 = localMiddleware.send(remoteAddress, new PingRequest(false));
        Assertions.assertNotNull(r1);
        Assertions.assertTrue(r1 instanceof AckResponse);
        Assertions.assertFalse(r1.isError());
        Response r2 = localMiddleware.send(remoteAddress, new PingRequest(true));
        Assertions.assertNotNull(r2);
        Assertions.assertTrue(r2 instanceof AckResponse);
        Assertions.assertTrue(r2.isError());
    }
}
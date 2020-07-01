package ttp;

import ttp.packets.requests.AuthChallengeRequest;
import ttp.packets.requests.RegisterRequest;
import ttp.packets.responses.AuthChallengeResponse;
import ttp.packets.responses.RegistrationResponse;
import org.junit.jupiter.api.*;
import misc.Builders;

/**
 * This test assert the correctness of the simple TTP methods. For now, we only check the registration protocol since
 * other methods require a full skip graph to be constructed. Those are tested in the `AuthNodeTest` tests.
 */
class TTPTest {

    static final int START_PORT = 8080;
    static final SystemParameters systemParameters = new SystemParameters(4, false, false);

    static TTP ttp;

    @BeforeAll
    static void setUp() {
        ttp = Builders.buildTTP(systemParameters, START_PORT);
    }

    @Test
    void register() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.sourceAddress = "0.0.0.0";
        RegistrationResponse regResponse = ttp.register(registerRequest);
        Assertions.assertNotNull(regResponse);
        Assertions.assertFalse(regResponse.isError());
        Assertions.assertTrue(regResponse.successful);

        AuthChallengeRequest authChallengeRequest = new AuthChallengeRequest();
        authChallengeRequest.sourceAddress = "0.0.0.1";
        AuthChallengeResponse authChallengeResponse = ttp.authChallenge(authChallengeRequest);
        Assertions.assertTrue(authChallengeResponse.isError());

        authChallengeRequest.sourceAddress = "0.0.0.0";
        authChallengeResponse = ttp.authChallenge(authChallengeRequest);
        Assertions.assertFalse(authChallengeResponse.isError());
    }
}
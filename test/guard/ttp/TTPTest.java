package guard.ttp;

import guard.ttp.packets.requests.AuthChallengeRequest;
import guard.ttp.packets.requests.RegisterRequest;
import guard.ttp.packets.responses.AuthChallengeResponse;
import guard.ttp.packets.responses.RegistrationResponse;
import org.junit.jupiter.api.*;
import userinterface.Constructors;

class TTPTest {

    static final int START_PORT = 8080;
    static final SystemParameters systemParameters = new SystemParameters(4, false, false, false);

    static TTP ttp;

    @BeforeAll
    static void setUp() {
        ttp = Constructors.createTTP(systemParameters, START_PORT);
    }

    @Test
    void register() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.senderAddress = "0.0.0.0";
        RegistrationResponse regResponse = ttp.register(registerRequest);
        Assertions.assertNotNull(regResponse);
        Assertions.assertFalse(regResponse.isError());
        Assertions.assertTrue(regResponse.successful);

        AuthChallengeRequest authChallengeRequest = new AuthChallengeRequest();
        authChallengeRequest.senderAddress = "0.0.0.1";
        AuthChallengeResponse authChallengeResponse = ttp.authChallenge(authChallengeRequest);
        Assertions.assertTrue(authChallengeResponse.isError());

        authChallengeRequest.senderAddress = "0.0.0.0";
        authChallengeResponse = ttp.authChallenge(authChallengeRequest);
        Assertions.assertFalse(authChallengeResponse.isError());
    }
}
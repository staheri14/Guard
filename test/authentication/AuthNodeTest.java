package authentication;

import authentication.packets.requests.AuthSearchByNumIDRequest;
import authentication.packets.responses.AuthSearchResultResponse;
import ttp.SystemParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import misc.LocalSystem;

/**
 * This test constructs a local authenticated skip graph and tests their correct construction &
 * the lookup operation correctness on them.
 */
class AuthNodeTest {

    static final int STARTING_PORT = 8080;

    @Test
    void fourNodeTest() {
        SystemParameters systemParameters = new SystemParameters(4, true, true);
        testSystem(new LocalSystem(systemParameters, STARTING_PORT));
    }

    @Test
    void eightNodeTest() {
        SystemParameters systemParameters = new SystemParameters(8, true, true);
        testSystem(new LocalSystem(systemParameters, STARTING_PORT));
    }

    @Test
    void sixteenNodeTest() {
        SystemParameters systemParameters = new SystemParameters(16, true, true);
        testSystem(new LocalSystem(systemParameters, STARTING_PORT));
    }

    /**
     * Asserts the correct construction and search operations in the given local authenticated skip graph.
     * @param localSystem local authenticated skip graph.
     */
    void testSystem(LocalSystem localSystem) {
        // Registration:
        for(Authentication auth : localSystem.getAuthLayers()) {
            Assertions.assertFalse(auth.nodeRegister().isError());
        }

        // Insertion:
        for(int i = 0; i < localSystem.getNodes().length; i++) {
            Authentication auth = localSystem.getAuthLayers()[i];
            Assertions.assertFalse(auth.authInsert().isError());
        }

        // Construction:
        for(Authentication auth : localSystem.getAuthLayers()) {
            Assertions.assertFalse(auth.nodeConstruct().isError());
        }

        // Guard assignment:
        for(Authentication auth : localSystem.getAuthLayers()) {
            Assertions.assertFalse(auth.nodeAssign().isError());
        }

        // Searches:
        for(Authentication auth : localSystem.getAuthLayers()) {
            for(int i = -1; i < localSystem.getNodes().length + 1; i++) {
                AuthSearchResultResponse r = auth.authSearchByNumID(new AuthSearchByNumIDRequest(i));
                int expectedResult = Math.min(localSystem.getNodes().length-1, Math.max(i, 0));
                Assertions.assertFalse(r.isError());
                Assertions.assertEquals(expectedResult, r.result.getNumID());
            }
        }

        // Terminate the system.
        localSystem.terminate();
    }
}
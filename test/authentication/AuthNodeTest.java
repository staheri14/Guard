package authentication;

import authentication.packets.responses.AuthSearchResultResponse;
import ttp.SystemParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import skipnode.packets.requests.InsertRequest;
import skipnode.packets.requests.SearchByNumIDRequest;
import userinterface.LocalSystem;

class AuthNodeTest {

    static final int STARTING_PORT = 8080;

    @Test
    void fourNodeTest() {
        SystemParameters systemParameters = new SystemParameters(4, true, true, false);
        testSystem(new LocalSystem(systemParameters, STARTING_PORT));
    }

    @Test
    void eightNodeTest() {
        SystemParameters systemParameters = new SystemParameters(8, true, true, false);
        testSystem(new LocalSystem(systemParameters, STARTING_PORT));
    }

    @Test
    void sixteenNodeTest() {
        SystemParameters systemParameters = new SystemParameters(16, true, true, false);
        testSystem(new LocalSystem(systemParameters, STARTING_PORT));
    }

    void testSystem(LocalSystem localSystem) {
        // Registration:
        for(Authentication node : localSystem.getAuthLayers()) {
            Assertions.assertFalse(node.nodeRegister().isError());
        }

        // Insertion:
        for(int i = 0; i < localSystem.getNodes().length; i++) {
            Authentication node = localSystem.getAuthLayers()[i];
            String introducerAddress = (i == 0) ? null : localSystem.getNodes()[i-1].getAddress();
            Assertions.assertFalse(node.authInsert(new InsertRequest(introducerAddress)).isError());
        }

        // Construction:
        for(Authentication node : localSystem.getAuthLayers()) {
            Assertions.assertFalse(node.nodeConstruct().isError());
        }

        // Guard assignment:
        for(Authentication node : localSystem.getAuthLayers()) {
            Assertions.assertFalse(node.nodeAssign().isError());
        }

        // Searches:
        for(Authentication node : localSystem.getAuthLayers()) {
            for(int i = -1; i < localSystem.getNodes().length + 1; i++) {
                AuthSearchResultResponse r = node.authSearchByNumID(new SearchByNumIDRequest(i));
                int expectedResult = Math.min(localSystem.getNodes().length-1, Math.max(i, 0));
                Assertions.assertFalse(r.isError());
                Assertions.assertEquals(expectedResult, r.result.getNumID());
            }
        }

        // Terminate the system.
        localSystem.terminate();
    }
}
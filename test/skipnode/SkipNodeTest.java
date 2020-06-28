package skipnode;

import ttp.SystemParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import skipnode.packets.requests.SearchByNumIDRequest;
import communication.Communication;
import network.packets.responses.AckResponse;
import skipnode.packets.responses.SearchResultResponse;

/**
 * This test creates for unauthenticated nodes and tests two fundamental skip graph operations, i.e.
 * joining (insertion into skip graph) and lookup (numerical ID search over the skip graph.)
 */
class SkipNodeTest {

    static final SystemParameters systemParameters = new SystemParameters(4, false, false);
    static final int STARTING_PORT = 9090;

    static Communication communication0;
    static Communication communication1;
    static Communication communication2;
    static Communication communication3;

    static SkipNode node0;
    static SkipNode node1;
    static SkipNode node2;
    static SkipNode node3;

    @BeforeAll
    static void setUp() {
        communication0 = new Communication(STARTING_PORT);
        communication1 = new Communication(STARTING_PORT + 1);
        communication2 = new Communication(STARTING_PORT + 2);
        communication3 = new Communication(STARTING_PORT + 3);

        node0 = new SkipNode( 0, "00", null, systemParameters, communication0.getAddress());
        node1 = new SkipNode( 1, "01", communication0.getAddress(), systemParameters, communication1.getAddress());
        node2 = new SkipNode( 2, "10", communication1.getAddress(), systemParameters, communication2.getAddress());
        node3 = new SkipNode(3, "11", communication2.getAddress(), systemParameters, communication3.getAddress());

        node0.setUnderlay(communication0);
        node1.setUnderlay(communication1);
        node2.setUnderlay(communication2);
        node3.setUnderlay(communication3);

        Assertions.assertTrue(communication0.initializeHost(node0));
        Assertions.assertTrue(communication1.initializeHost(node1));
        Assertions.assertTrue(communication2.initializeHost(node2));
        Assertions.assertTrue(communication3.initializeHost(node3));
    }

    @BeforeEach
    void insert() {
        AckResponse insertResponse = node0.insert();
        Assertions.assertNotNull(insertResponse);
        Assertions.assertFalse(insertResponse.isError());

        insertResponse = node1.insert();
        Assertions.assertNotNull(insertResponse);
        Assertions.assertFalse(insertResponse.isError());

        insertResponse = node2.insert();
        Assertions.assertNotNull(insertResponse);
        Assertions.assertFalse(insertResponse.isError());

        insertResponse = node3.insert();
        Assertions.assertNotNull(insertResponse);
        Assertions.assertFalse(insertResponse.isError());

        tableConsistencyCheck(node0.getInfo().getNumID(), node0.getInfo().getNameID(), node0.getLookupTable());
        tableConsistencyCheck(node1.getInfo().getNumID(), node1.getInfo().getNameID(), node1.getLookupTable());
        tableConsistencyCheck(node2.getInfo().getNumID(), node2.getInfo().getNameID(), node2.getLookupTable());
        tableConsistencyCheck(node3.getInfo().getNumID(), node3.getInfo().getNameID(), node3.getLookupTable());
    }

    @Test
    void searchByNumID() {
        SearchResultResponse searchResponse = node0.searchByNumID(new SearchByNumIDRequest(3));
        Assertions.assertEquals(3, searchResponse.result.getNumID());
        searchResponse = node0.searchByNumID(new SearchByNumIDRequest(5));
        Assertions.assertEquals(3, searchResponse.result.getNumID());
        searchResponse = node0.searchByNumID(new SearchByNumIDRequest(0));
        Assertions.assertEquals(0, searchResponse.result.getNumID());
        searchResponse = node0.searchByNumID(new SearchByNumIDRequest(-1));
        Assertions.assertEquals(0, searchResponse.result.getNumID());

        searchResponse = node1.searchByNumID(new SearchByNumIDRequest(-1));
        Assertions.assertEquals(0, searchResponse.result.getNumID());
        searchResponse = node1.searchByNumID(new SearchByNumIDRequest(0));
        Assertions.assertEquals(0, searchResponse.result.getNumID());
        searchResponse = node1.searchByNumID(new SearchByNumIDRequest(1));
        Assertions.assertEquals(1, searchResponse.result.getNumID());
        searchResponse = node1.searchByNumID(new SearchByNumIDRequest(2));
        Assertions.assertEquals(2, searchResponse.result.getNumID());
        searchResponse = node1.searchByNumID(new SearchByNumIDRequest(3));
        Assertions.assertEquals(3, searchResponse.result.getNumID());
        searchResponse = node1.searchByNumID(new SearchByNumIDRequest(4));
        Assertions.assertEquals(3, searchResponse.result.getNumID());

        searchResponse = node2.searchByNumID(new SearchByNumIDRequest(-1));
        Assertions.assertEquals(0, searchResponse.result.getNumID());
        searchResponse = node2.searchByNumID(new SearchByNumIDRequest(0));
        Assertions.assertEquals(0, searchResponse.result.getNumID());
        searchResponse = node2.searchByNumID(new SearchByNumIDRequest(1));
        Assertions.assertEquals(1, searchResponse.result.getNumID());
        searchResponse = node2.searchByNumID(new SearchByNumIDRequest(2));
        Assertions.assertEquals(2, searchResponse.result.getNumID());
        searchResponse = node2.searchByNumID(new SearchByNumIDRequest(3));
        Assertions.assertEquals(3, searchResponse.result.getNumID());
        searchResponse = node2.searchByNumID(new SearchByNumIDRequest(4));
        Assertions.assertEquals(3, searchResponse.result.getNumID());

        searchResponse = node3.searchByNumID(new SearchByNumIDRequest(-1));
        Assertions.assertEquals(0, searchResponse.result.getNumID());
        searchResponse = node3.searchByNumID(new SearchByNumIDRequest(0));
        Assertions.assertEquals(0, searchResponse.result.getNumID());
        searchResponse = node3.searchByNumID(new SearchByNumIDRequest(1));
        Assertions.assertEquals(1, searchResponse.result.getNumID());
        searchResponse = node3.searchByNumID(new SearchByNumIDRequest(2));
        Assertions.assertEquals(2, searchResponse.result.getNumID());
        searchResponse = node3.searchByNumID(new SearchByNumIDRequest(3));
        Assertions.assertEquals(3, searchResponse.result.getNumID());
        searchResponse = node3.searchByNumID(new SearchByNumIDRequest(4));
        Assertions.assertEquals(3, searchResponse.result.getNumID());
    }

    void tableConsistencyCheck(int numID, String nameID, LookupTable table) {
        for(int i = 0; i < table.getSize(); i++) {
            for(int j = 0; j < 2; j++) {
                NodeInfo neighbor = table.getNeighbor(i, j);
                if(neighbor.invalid) continue;
                Assertions.assertTrue(NodeInfo.commonBits(neighbor.getNameID(), nameID) >= i);
            }
            NodeInfo leftNeighbor = table.getNeighbor(i ,0);
            NodeInfo rightNeighbor = table.getNeighbor(i, 1);
            if(!leftNeighbor.invalid) Assertions.assertTrue(leftNeighbor.getNumID() < numID);
            if(!rightNeighbor.invalid) Assertions.assertTrue(rightNeighbor.getNumID() > numID);
        }
    }

}
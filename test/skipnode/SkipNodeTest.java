package skipnode;

import ttp.SystemParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import skipnode.packets.requests.InsertRequest;
import skipnode.packets.requests.SearchByNumIDRequest;
import middleware.Middleware;
import protocol.packets.responses.AckResponse;
import skipnode.packets.responses.SearchResultResponse;

class SkipNodeTest {

    static final SystemParameters systemParameters = new SystemParameters(4, false, false, false);
    static final int STARTING_PORT = 9090;

    static Middleware middleware0;
    static Middleware middleware1;
    static Middleware middleware2;
    static Middleware middleware3;

    static SkipNode node0;
    static SkipNode node1;
    static SkipNode node2;
    static SkipNode node3;

    @BeforeAll
    static void setUp() {
        middleware0 = new Middleware(STARTING_PORT);
        middleware1 = new Middleware(STARTING_PORT + 1);
        middleware2 = new Middleware(STARTING_PORT + 2);
        middleware3 = new Middleware(STARTING_PORT + 3);

        node0 = new SkipNode( 0, "00", systemParameters, middleware0);
        node1 = new SkipNode( 1, "01", systemParameters, middleware1);
        node2 = new SkipNode( 2, "10", systemParameters, middleware2);
        node3 = new SkipNode(3, "11", systemParameters, middleware3);

        Assertions.assertTrue(middleware0.initializeHost(node0));
        Assertions.assertTrue(middleware1.initializeHost(node1));
        Assertions.assertTrue(middleware2.initializeHost(node2));
        Assertions.assertTrue(middleware3.initializeHost(node3));
    }

    @BeforeEach
    void insert() {
        AckResponse insertResponse = node0.insert(new InsertRequest(null));
        Assertions.assertNotNull(insertResponse);
        Assertions.assertFalse(insertResponse.isError());

        insertResponse = node1.insert(new InsertRequest(middleware0.getAddress()));
        Assertions.assertNotNull(insertResponse);
        Assertions.assertFalse(insertResponse.isError());

        insertResponse = node2.insert(new InsertRequest(middleware1.getAddress()));
        Assertions.assertNotNull(insertResponse);
        Assertions.assertFalse(insertResponse.isError());

        insertResponse = node3.insert(new InsertRequest(middleware2.getAddress()));
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
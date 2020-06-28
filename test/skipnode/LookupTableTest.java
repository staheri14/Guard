package skipnode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test asserts the correctness of the various methods of a lookup table.
 */
class LookupTableTest {

    static final int MAX_LEVELS = 8;
    LookupTable lookupTable;

    @BeforeEach
    void setUp() {
        lookupTable = new LookupTable(MAX_LEVELS);
    }

    @Test
    void getNeighbor() {
        Assertions.assertNull(lookupTable.getNeighbor(0, 2));
        Assertions.assertNull(lookupTable.getNeighbor(MAX_LEVELS, 1));
        for(int i = 0; i < MAX_LEVELS; i++) {
            for(int j = 0; j < 2; j++) {
                Assertions.assertTrue(lookupTable.getNeighbor(i, j).invalid);
            }
        }
        NodeInfo node = new NodeInfo(3, "11");
        lookupTable.setNeighbor(MAX_LEVELS/2, 0, node);
        Assertions.assertFalse(lookupTable.getNeighbor(MAX_LEVELS/2, 0).invalid);
        Assertions.assertTrue(lookupTable.getNeighbor(MAX_LEVELS/2, 0).equals(node));
    }

    @Test
    void hasNeighbor() {
        for(int i = 0; i < MAX_LEVELS; i++) {
            for(int j = 0; j < 2; j++) {
                Assertions.assertFalse(lookupTable.hasNeighbor(i, j));
            }
        }
        NodeInfo node = new NodeInfo(3, "11");
        lookupTable.setNeighbor(MAX_LEVELS/2, 0, node);
        Assertions.assertTrue(lookupTable.hasNeighbor(MAX_LEVELS/2, 0));
    }

    @Test
    void findNextHop() {
        int currentNumID = 2;
        NodeInfo[] zeroLevelNeighbors = new NodeInfo[] {
                new NodeInfo(1, "001"),
                new NodeInfo(3, "011")
        };
        NodeInfo[] firstLevelNeighbors = new NodeInfo[] {
                new NodeInfo(0, "000"),
                new NodeInfo(6, "110")
        };
        lookupTable.setNeighbor(0, 0, zeroLevelNeighbors[0]);
        lookupTable.setNeighbor(0, 1, zeroLevelNeighbors[1]);
        lookupTable.setNeighbor(1, 0, firstLevelNeighbors[0]);
        lookupTable.setNeighbor(1, 1, firstLevelNeighbors[1]);

        LookupTable.NextHop nextHop1 = LookupTable.findNextHop(currentNumID, 7, 1, lookupTable);
        Assertions.assertNotNull(nextHop1);
        Assertions.assertEquals(firstLevelNeighbors[1].getNumID(), nextHop1.node.getNumID());

        LookupTable.NextHop nextHop2 = LookupTable.findNextHop(currentNumID, 7, 0, lookupTable);
        Assertions.assertNotNull(nextHop2);
        Assertions.assertEquals(zeroLevelNeighbors[1].getNumID(), nextHop2.node.getNumID());

        LookupTable.NextHop nextHop3 = LookupTable.findNextHop(currentNumID, -1, 1, lookupTable);
        Assertions.assertNotNull(nextHop3);
        Assertions.assertEquals(firstLevelNeighbors[0].getNumID(), nextHop3.node.getNumID());

        LookupTable.NextHop nextHop4 = LookupTable.findNextHop(currentNumID, -1, 0, lookupTable);
        Assertions.assertNotNull(nextHop4);
        Assertions.assertTrue(nextHop4.node.equals(zeroLevelNeighbors[0]));
        Assertions.assertEquals(zeroLevelNeighbors[0].getNumID(), nextHop4.node.getNumID());

        LookupTable.NextHop nextHop5 = LookupTable.findNextHop(currentNumID, currentNumID, 2, lookupTable);
        Assertions.assertNull(nextHop5);
    }
}
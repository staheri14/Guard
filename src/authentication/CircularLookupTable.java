package authentication;

import skipnode.LookupTable;
import skipnode.NodeInfo;

/**
 * In Guard, the 0th level is modeled as a circular linked list, i.e. the last node and the first
 * node are connected with each other. This lookup table overrides the default lookup table with
 * such mechanism.
 */
public class CircularLookupTable extends LookupTable {

    private final NodeInfo circularLeftNeighbor;
    private final NodeInfo circularRightNeighbor;

    public CircularLookupTable(LookupTable from, NodeInfo circularLeftNeighbor, NodeInfo circularRightNeighbor) {
        super(from);
        this.circularLeftNeighbor = circularLeftNeighbor;
        this.circularRightNeighbor = circularRightNeighbor;
    }

    @Override
    public NodeInfo getNeighbor(int level, int position) {
        if(level == 0 && position == 0) {
            return circularLeftNeighbor;
        }
        if(level == 0 && position == 1) {
            return circularRightNeighbor;
        }
        return super.getNeighbor(level, position);
    }

    /**
     * Converts the circular lookup table to a regular lookup table in which the 0-th level is not a circular list.
     * @param ownerNumID the numerical ID of the owner of this lookup table.
     * @return the lookup table conversion.
     */
    public LookupTable convertToLookupTable(int ownerNumID) {
        lock.readLock().lock();
        // Construct this node's actual lookup table (with non-circular 0th level).
        NodeInfo[][] convertedLookupTable = new NodeInfo[table.length][2];
        // Handle the 0th level.
        convertedLookupTable[0][0] = (!table[0][0].invalid && table[0][0].getNumID() > ownerNumID)
                ? new NodeInfo() : table[0][0];
        convertedLookupTable[0][1] = (!table[0][1].invalid && table[0][1].getNumID() < ownerNumID)
                ? new NodeInfo() : table[0][1];
        // Simply copy the remaining levels.
        for(int i = 1; i < table.length; i++) {
            System.arraycopy(table[i], 0, convertedLookupTable[i], 0, 2);
        }
        lock.readLock().unlock();
        return new LookupTable(convertedLookupTable);
    }
}

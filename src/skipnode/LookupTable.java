package skipnode;

import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LookupTable implements Serializable {

    protected final NodeInfo[][] table;
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();

    public static class NextHop {

        public final NodeInfo node;
        public final int level;
        public final int position;

        public NextHop(NodeInfo node, int level, int position) {
            this.node = node;
            this.level = level;
            this.position = position;
        }
    }

    public LookupTable(int levels) {
        table = new NodeInfo[levels][2];
        for(int i = 0; i < levels; i++) {
            for(int j = 0; j < 2; j++) {
                table[i][j] = new NodeInfo();
            }
        }
    }

    public LookupTable(LookupTable other) {
        this(other.table);
    }

    public LookupTable(NodeInfo[][] table) {
        this.table = new NodeInfo[table.length][2];
        for(int i = 0; i < table.length; i++) {
            System.arraycopy(table[i], 0, this.table[i], 0, 2);
        }
    }

    public int getSize() {
        return table.length;
    }

    private boolean isInvalidPosition(int level, int position) {
        return level < 0 || level >= table.length || position > 1 || position < 0;
    }

    /**
     * Returns the neighbor at the given position.
     * @param level level of the neighbor.
     * @param position position of the neighbor at the level. 0 (left) or 1 (right).
     * @return the neighbor. null if the given position is invalid.
     */
    public NodeInfo getNeighbor(int level, int position) {
        if(isInvalidPosition(level, position)) {
            return null;
        }
        lock.readLock().lock();
        NodeInfo entry = new NodeInfo(table[level][position]);
        lock.readLock().unlock();
        return entry;
    }

    public boolean setNeighbor(int level, int position, NodeInfo neighbor) {
        if(isInvalidPosition(level, position)) {
            return false;
        }
        lock.writeLock().lock();
        table[level][position] = new NodeInfo(neighbor);
        lock.writeLock().unlock();
        return true;
    }

    public boolean hasNeighbor(int level, int position) {
        if(isInvalidPosition(level, position)) {
            return false;
        }
        lock.readLock().lock();
        boolean isNeighborInvalid = table[level][position].invalid;
        lock.readLock().unlock();
        return !isNeighborInvalid;
    }

    /**
     * Returns the next hop that a request should be routed.
     * @param currNumID the numerical ID of the node owning the lookup table.
     * @param target the target numerical ID.
     * @param fromLevel the level that the search should be started from.
     * @param lookupTable the lookup table that the search should be performed on.
     * @return the next hop information, or null if the owner of the lookup table is the target node.
     */
    public static NextHop findNextHop(int currNumID, int target, int fromLevel, LookupTable lookupTable) {
        if(currNumID == target)
            return null;
        // If the target is greater than the current node then we should search right
        if(currNumID < target) {
            // Keep going down levels as long as there is either no right neighbor
            // or the right neighbor has a numID greater than the target
            while(fromLevel >= 0 && (!lookupTable.hasNeighbor(fromLevel, 1)
                    || lookupTable.getNeighbor(fromLevel, 1).getNumID() > target))
                fromLevel--;
            // If there are no more levels to go down to return the current node
            if(fromLevel < 0) {
                return null;
            }
            return new NextHop(lookupTable.getNeighbor(fromLevel, 1), fromLevel, 1);
        } else {
            // If the target is less than the current node then we should search left
            // Keep going down levels as long as there is either no right neighbor
            // or the left neighbor has a numID greater than the target
            while(fromLevel >= 0 && (!lookupTable.hasNeighbor(fromLevel, 0)
                    || lookupTable.getNeighbor(fromLevel, 0).getNumID() < target))
                fromLevel--;
            // If there are no more levels to go down to return the current node
            if(fromLevel < 0)
                return null;
            return new NextHop(lookupTable.getNeighbor(fromLevel, 0), fromLevel, 0);
        }
    }
}

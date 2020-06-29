package skipnode;
import java.io.Serializable;

/**
 * Contains the fundamental information about a skip-graph node.
 */
public class NodeInfo implements Serializable {

	private String address;
	private final int numID;
	private final String nameID;
	// Whether the node is invalid or not. An invalid node is simply a placeholder in the
	// lookup table of an actual node. It means that the actual node does not have a such
	// neighbor.
	public final boolean invalid;

	// Whether this node is inserted or not.
	private boolean inserted;

	public NodeInfo() {
		address = "";
		numID = -1;
		nameID = "";
		invalid = true;
		inserted = false;
	}

	// Copy constructor.
	public NodeInfo(NodeInfo other) {
		this.address = other.address;
		this.numID = other.numID;
		this.nameID = other.nameID;
		this.inserted = other.inserted;
		invalid = other.invalid;
	}
	
	public NodeInfo(int numID, String nameID) {
		this.numID = numID;
		this.nameID = nameID;
		invalid = false;
		inserted = false;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getNumID() {
		return numID;
	}

	public String getNameID() {
		return nameID;
	}

	public void markAsInserted() {
		inserted = true;
	}

	public boolean isInserted() {
		return inserted;
	}

	public boolean equals(NodeInfo node) {
		return numID == node.getNumID() &&
				nameID.equals(node.getNameID()); 
	}

	/*
	 * This method returns the length of the common prefix between two name IDs.
	 */
	public static int commonBits(String name1, String name2) {
		if(name1 == null || name2 == null) {
			return -1;
		}
		if(name1.length() != name2.length())
			return -1;
		int i = 0;
		for(; i < name1.length() && name1.charAt(i) == name2.charAt(i) ; ++i);
		return i;
	}

	@Override
	public String toString() {
		return "{Address = " + address + ", " +
				"Num. ID = " + numID + ", " +
				"Name ID = " + nameID + "}";
	}
}

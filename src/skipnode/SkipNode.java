package skipnode;

import ttp.SystemParameters;
import skipnode.packets.requests.*;
import protocol.packets.responses.AckResponse;
import skipnode.packets.responses.NodeInfoResponse;
import protocol.Request;
import protocol.Response;
import protocol.Layer;
import skipnode.packets.responses.SearchResultResponse;

public class SkipNode extends Layer {

    private LookupTable lookupTable;
    private NodeInfo info;
    private SystemParameters systemParameters;
    private String introducerAddress;

    public SkipNode() {}

    public SkipNode(int numID, String nameID, String introducerAddress, SystemParameters systemParameters, String address) {
        initialize(numID, nameID, introducerAddress, systemParameters);
        // Set the address separately as the underlay might not be assigned yet.
        info.setAddress(address);
    }

    public void initialize(int numID, String nameID, String introducerAddress, SystemParameters systemParameters) {
        lookupTable = new LookupTable(systemParameters.getNameIDLength());
        info = new NodeInfo(numID, nameID);
        info.setAddress(getAddress());
        this.systemParameters = systemParameters;
        this.introducerAddress = introducerAddress;
    }

    public LookupTable getLookupTable() {
        return new LookupTable(lookupTable);
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        return switch(request.type) {
            case GET_INFO -> getInfo(request);
            case GET_LEFT_NODE -> getLeftNode((GetLeftNodeRequest) request);
            case GET_RIGHT_NODE -> getRightNode((GetRightNodeRequest) request);
            case SET_LEFT_NODE -> setLeftNode((SetLeftNodeRequest) request);
            case SET_RIGHT_NODE -> setRightNode((SetRightNodeRequest) request);
            case INSERT -> insert();
            case FIND_LADDER -> findLadder((FindLadderRequest) request);
            case ROUTE_SEARCH_NUM_ID -> routeSearchNumID((RouteSearchNumIDRequest) request);
            case SEARCH_BY_NUM_ID -> searchByNumID((SearchByNumIDRequest) request);
            default -> null;
        };
    }

    public NodeInfo getInfo() {
        if(info == null) return null;
        return new NodeInfo(info);
    }

    public NodeInfoResponse getInfo(Request request) {
        if(info == null) {
            return new NodeInfoResponse(null, "Not registered yet.");
        }
        return new NodeInfoResponse(getInfo(), null);
    }

    public NodeInfoResponse getLeftNode(GetLeftNodeRequest request) {
        NodeInfo entry = lookupTable.getNeighbor(request.level, 0);
        if(entry == null) {
            return new NodeInfoResponse(null, "getLeftNode: invalid level (" + request.level + ")");
        }
        return new NodeInfoResponse(entry, null);
    }

    public NodeInfoResponse getRightNode(GetRightNodeRequest request) {
        NodeInfo entry = lookupTable.getNeighbor(request.level, 1);
        if(entry == null) {
            return new NodeInfoResponse(null, "getRightNode: invalid level (" + request.level + ")");
        }
        return new NodeInfoResponse(lookupTable.getNeighbor(request.level, 1), null);
    }

    public AckResponse setLeftNode(SetLeftNodeRequest request) {
        if(!lookupTable.setNeighbor(request.level, 0, request.nodeInfo)) {
            return new AckResponse("setLeftNode: invalid level (" + request.level + ")");
        }
        return new AckResponse(null);
    }

    public AckResponse setRightNode(SetRightNodeRequest request) {
        if(!lookupTable.setNeighbor(request.level, 1, request.nodeInfo)) {
            return new AckResponse("setRightNode: invalid level (" + request.level + ")");
        }
        return new AckResponse(null);
    }

    public SearchResultResponse routeSearchNumID(RouteSearchNumIDRequest request) {
        LookupTable.NextHop nextHop = LookupTable.findNextHop(info.getNumID(), request.target, request.level, lookupTable);
        if(nextHop == null) {
            return new SearchResultResponse(new NodeInfo(info), null);
        }
        // We modify the acquired request as opposed to creating a new one in order to mitigate type erasure.
        request.level = nextHop.level;
        Response r = send(nextHop.node.getAddress(), request);
        if(r.isError()) {
            return new SearchResultResponse(null, r.errorMessage);
        }
        return (SearchResultResponse) r;
    }

    public SearchResultResponse searchByNumID(SearchByNumIDRequest request) {
        return routeSearchNumID(new RouteSearchNumIDRequest(request.target, systemParameters.getMaxLevels()));
    }

    public NodeInfoResponse findLadder(FindLadderRequest request) {
        // If the current node and the inserted node have common bits more than the current level,
        // then this node is the neighbor so we return it
        if(NodeInfo.commonBits(request.target, info.getNameID()) > request.level) {
            return new NodeInfoResponse(new NodeInfo(info), null);
        }
        // Response from the neighbor.
        Response neighborResponse;
        // If the search is to the right...
        if(request.direction == 1) {
            // And if the right neighbor does not exist then at this level the right neighbor of the inserted node is null.
            if(lookupTable.getNeighbor(request.level, 1).invalid) {
                return new NodeInfoResponse(new NodeInfo(), null);
            }
            // Otherwise, delegate the search to right neighbor.
            neighborResponse = send(lookupTable.getNeighbor(request.level, 1).getAddress(), request);
        } else {
            // If the search is to the left and if the left neighbor is null, then the left neighbor of the inserted
            // node at this level is null.
            if(lookupTable.getNeighbor(request.level, 0).invalid) {
                return new NodeInfoResponse(null, null);
            }
            // Otherwise, delegate the search to the left neighbor.
            neighborResponse = send(lookupTable.getNeighbor(request.level, 0).getAddress(), request);
        }
        if(neighborResponse.isError()) {
            return new NodeInfoResponse(null, neighborResponse.errorMessage);
        }
        return (NodeInfoResponse) neighborResponse;
    }

    public AckResponse insert() {
        // Do not insert an already inserted node.
        if(info.isInserted()) return new AckResponse(null);
        // We search through the introducer node to find the node with
        // the closest num ID.
        NodeInfo position;
        if(introducerAddress == null) {
            // This node is the first node of the skip graph. Simply insert it.
            info.markAsInserted();
            return new AckResponse(null);
        } else {
            // Search through the introducer of this node.
            Response r = send(introducerAddress, new RouteSearchNumIDRequest(info.getNumID(), systemParameters.getMaxLevels()));
            if(r.isError()) {
                return new AckResponse(r.errorMessage);
            }
            position = ((SearchResultResponse) r).result;
        }
        // If we cannot find a node, then there is a dire problem.
        if(position == null) {
            return new AckResponse("insert: the address resulting from the search is null");
        }
        // First, we insert the node at level 0.
        int posNum = position.getNumID(); // numID of the closest node
        String posAddress = position.getAddress();
        NodeInfo newLeftNode;
        NodeInfo newRightNode;
        // If the closest node is to the right, then the left of my right will be my left.
        if(posNum > info.getNumID()) {
            Response r = send(posAddress, new GetLeftNodeRequest(0));
            if(r.isError()) {
                return new AckResponse(r.errorMessage);
            }
            newLeftNode = ((NodeInfoResponse) r).nodeInfo;
            newRightNode = new NodeInfo(position);
        } else {
            // if the closest node is to the left...
            Response r = send(posAddress, new GetRightNodeRequest(0));
            if(r.isError()) {
                return new AckResponse(r.errorMessage);
            }
            newRightNode = ((NodeInfoResponse) r).nodeInfo;
            newLeftNode = new NodeInfo(position);
        }
        // Update my lookup table.
        lookupTable.setNeighbor(0, 0, newLeftNode);
        lookupTable.setNeighbor(0, 1, newRightNode);
        // Update my 0-level neighbor's lookup table.
        if(!newLeftNode.invalid) {
            Response r = send(newLeftNode.getAddress(), new SetRightNodeRequest(0, info));
            if(r.isError()) {
                return new AckResponse(r.errorMessage);
            }
        }
        if(!newRightNode.invalid) {
            Response r = send(newRightNode.getAddress(), new SetLeftNodeRequest(0, info));
            if(r.isError()) {
                return new AckResponse(r.errorMessage);
            }
        }
        // Now, we insert the node in the rest of the levels by climbing up.
        // In level i , we make a recursive search for the nodes that will be
        // the neighbors of the inserted nodes at level i+1
        String leftAddress = (!newLeftNode.invalid) ? newLeftNode.getAddress() : null;
        String rightAddress = (!newRightNode.invalid) ? newRightNode.getAddress() : null;
        int level = 0;

        while(level < systemParameters.getMaxLevels()) {
            if(leftAddress != null) {
                // Get my new left node at the level.
                Response r = send(leftAddress, new FindLadderRequest(level, 0, info.getNameID()));
                if(r.isError()) {
                    return new AckResponse(r.errorMessage);
                }
                NodeInfo newLeftAtLevel = ((NodeInfoResponse) r).nodeInfo;
                if(newLeftAtLevel != null) {
                    lookupTable.setNeighbor(level + 1, 0, newLeftAtLevel);
                }
                // set left and leftNum to default values (null,-1)
                // so that if the left neighbor is null then we no longer need
                // to search in higher levels to the left
                leftAddress = null;
                if(newLeftAtLevel != null) {
                    leftAddress = newLeftAtLevel.getAddress();
                    Response r2 = send(leftAddress, new SetRightNodeRequest(level + 1, info));
                    if(r2.isError()) {
                        return new AckResponse(r.errorMessage);
                    }
                }
            }
            if(rightAddress != null) {
                // Get my new right at the level.
                Response r = send(leftAddress, new FindLadderRequest(level, 1, info.getNameID()));
                if(r.isError()) {
                    return new AckResponse(r.errorMessage);
                }
                NodeInfo newRightAtLevel = ((NodeInfoResponse) r).nodeInfo;
                if(newRightAtLevel != null) {
                    lookupTable.setNeighbor(level + 1, 1, newRightAtLevel);
                }
                // set right and rightNum to default values (null,-1)
                // so that if the right neighbor is null then we no longer need
                // to search in higher levels to the right
                rightAddress = null;
                if(newRightAtLevel != null) {
                    rightAddress = newRightAtLevel.getAddress();
                    Response r2 = send(rightAddress, new SetLeftNodeRequest(level + 1, info));
                    if(r2.isError()) {
                        return new AckResponse(r.errorMessage);
                    }
                }
            }
            level++;
        }
        // Mark this node as inserted.
        info.markAsInserted();
        // Insertion is complete.
        return new AckResponse(null);
    }
}

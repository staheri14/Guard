package skipnode.packets.requests;

import misc.Logger;
import network.Request;
import network.RequestType;

public class FindLadderRequest extends Request {

    public final int level;
    public final int direction;
    public final String target;

    public FindLadderRequest(int level, int direction, String target) {
        super(RequestType.FIND_LADDER);
        this.level = level;
        this.direction = direction;
        this.target = target;
        this.phase = Logger.Phase.CONSTRUCTION;
    }
}

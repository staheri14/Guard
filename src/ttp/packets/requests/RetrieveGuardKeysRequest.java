package ttp.packets.requests;

import crypto.memento.SignatureMemento;
import misc.Logger;
import network.Request;
import network.RequestType;

public class RetrieveGuardKeysRequest extends Request {

    public final SignatureMemento challengeSolution;
    public final String guardedNodeAddress;
    public final int guardIndex;

    public RetrieveGuardKeysRequest(SignatureMemento challengeSolution, String guardedNodeAddress, int guardIndex) {
        super(RequestType.TTP_RETRIEVE_GUARD_INFO);
        this.challengeSolution = challengeSolution;
        this.guardedNodeAddress = guardedNodeAddress;
        this.guardIndex = guardIndex;
        this.auth = true;
        this.phase = Logger.Phase.GUARD_ASSIGNMENT;
    }
}

package ttp.packets.requests;

import crypto.memento.SignatureMemento;
import protocol.Request;
import protocol.RequestType;

public class RetrieveGuardKeysRequest extends Request {

    public final SignatureMemento challengeSolution;
    public final String guardedNodeAddress;
    public final int guardIndex;

    public RetrieveGuardKeysRequest(SignatureMemento challengeSolution, String guardedNodeAddress, int guardIndex) {
        super(RequestType.TTP_RETRIEVE_GUARD_KEYS);
        this.challengeSolution = challengeSolution;
        this.guardedNodeAddress = guardedNodeAddress;
        this.guardIndex = guardIndex;
    }
}

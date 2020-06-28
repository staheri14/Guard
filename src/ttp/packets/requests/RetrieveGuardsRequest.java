package ttp.packets.requests;

import crypto.memento.SignatureMemento;
import authentication.CircularLookupTable;
import authentication.TableProof;
import network.Request;
import network.RequestType;

public class RetrieveGuardsRequest extends Request {

    public final SignatureMemento challengeSolution;
    public final TableProof tableProof;
    public final CircularLookupTable circularLookupTable;

    public RetrieveGuardsRequest(SignatureMemento challengeSolution, TableProof tableProof,
                                 CircularLookupTable circularLookupTable) {
        super(RequestType.TTP_RETRIEVE_GUARDS);
        this.challengeSolution = challengeSolution;
        this.tableProof = tableProof;
        this.circularLookupTable = circularLookupTable;
    }
}

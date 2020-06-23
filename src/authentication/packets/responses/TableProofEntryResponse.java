package authentication.packets.responses;

import crypto.memento.SignatureMemento;
import protocol.Response;

public class TableProofEntryResponse extends Response {

    public final SignatureMemento signature;

    public TableProofEntryResponse(SignatureMemento signature, String errorMessage) {
        super(errorMessage);
        this.signature = signature;
    }
}

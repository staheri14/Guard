package ttp.packets.responses;

import crypto.PublicParameters;
import it.unisa.dia.gas.jpbc.Element;
import protocol.Response;

public class RetrieveGuardsResponse extends Response {

    public final String mainGuardAddress;
    public final String leftGuardAddress;
    public final String rightGuardAddress;
    // Public distributed key parameter.
    public byte[][] yBytes;

    public RetrieveGuardsResponse(String mainGuardAddress, String leftGuardAddress, String rightGuardAddress,
                                  Element[] Y) {
        super(null);
        this.mainGuardAddress = mainGuardAddress;
        this.leftGuardAddress = leftGuardAddress;
        this.rightGuardAddress = rightGuardAddress;
        yBytes = new byte[Y.length][];
        for(int i = 0; i < Y.length; i++) {
            yBytes[i] = Y[i].toBytes();
        }
    }

    public RetrieveGuardsResponse(String errorMessage) {
        super(errorMessage);
        this.mainGuardAddress = null;
        this.leftGuardAddress = null;
        this.rightGuardAddress = null;
    }

    public Element[] reconstructY(PublicParameters publicParameters) {
        Element[] Y = new Element[yBytes.length];
        for(int i = 0; i < Y.length; i++) {
            Y[i] = publicParameters.G.newElementFromBytes(yBytes[i]).getImmutable();
        }
        return Y;
    }

}

package crypto;

import it.unisa.dia.gas.jpbc.Element;

import java.io.Serializable;
import java.math.BigInteger;

public class PrivateKey implements Serializable {
    private Element first;
    private Element second;
    private BigInteger r_u;

    public PrivateKey(Element first, Element second, BigInteger r_u) {
        this.first = first.getImmutable();
        this.second = second.getImmutable();
        this.r_u = r_u;
    }

    public Element getFirst() {
        return first.getImmutable();
    }

    public Element getSecond() {
        return second.getImmutable();
    }

    public BigInteger getR_u() {
        return r_u;
    }


}

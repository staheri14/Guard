package crypto;

import it.unisa.dia.gas.jpbc.Element;

public class Signature {
    private Element first;
    private Element second;
    private Element third;

    public Signature(Element first, Element second, Element third) {
        this.first = first.getImmutable();
        this.second = second.getImmutable();
        this.third = third.getImmutable();
    }

    public Element getFirst() {
        return first.getImmutable();
    }

    public Element getSecond() {
        return second.getImmutable();
    }

    public Element getThird() {
        return third.getImmutable();
    }

}

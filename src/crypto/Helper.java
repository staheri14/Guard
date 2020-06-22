package crypto;

import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;

public class Helper {
    // Allows us to take the negative exponents of elements.
    public static Element power(Element base, BigInteger exponent) {
        Element result = base.getImmutable().pow(exponent.abs()).getImmutable();
        if(exponent.signum() < 0) {
            result = result.invert();
        }
        return result.getImmutable();
    }
}

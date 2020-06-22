package crypto.threshold;

import java.math.BigInteger;

public class Polynomial {

    private BigInteger[] coefficients;
    private BigInteger p;

    public Polynomial(BigInteger[] coefficients, BigInteger p) {
        if(coefficients.length < 1) {
            System.err.println("At least one coefficient for the polynomial is required!");
        }
        this.p = p;
        this.coefficients = coefficients;
    }

    public BigInteger compute(BigInteger x) {
        BigInteger result = coefficients[0];
        BigInteger x_i = x;
        for(int i = 1; i < coefficients.length; i++) {
            // result += parameter * x^i
            result = result.add(coefficients[i].multiply(x_i));
            x_i = x_i.multiply(x);
        }
        return result.mod(p);
    }

    public BigInteger getCoefficient(int index) {
        return this.coefficients[index];
    }
}

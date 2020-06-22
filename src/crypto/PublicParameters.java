package crypto;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;


public class PublicParameters {
    // ** Please note that all the elements are `immutable`.

    // Input field.
    public Field G;
    // Output field.
    public Field GT;
    // A random element from G.
    public Element g;
    // g to the power alpha.
    public Element g1;
    // A random element from G.
    public Element g2;
    // A random element from G.
    public Element uPrime;
    // A random element from G.
    public Element mPrime;
    // A vector of random elements from G.
    public Element[] U;
    // A vector of random elements from G.
    public Element[] M;
}

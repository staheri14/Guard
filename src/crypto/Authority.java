package crypto;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

public class Authority {
    public Element generateMPrime(Field G) {
        return G.newRandomElement().getImmutable();
    }

    public Element generateUPrime(Field G) {
        return G.newRandomElement().getImmutable();
    }

    public Element[] generateMVector(Field G, int n_m) {
        return generateRandomVector(G, n_m);
    }

    public Element[] generateUVector(Field G, int n_u) {
        return generateRandomVector(G, n_u);
    }

    private Element[] generateRandomVector(Field field, int size) {
        Element[] vector = new Element[size];
        for(int i = 0; i < size; i++) {
            vector[i] = field.newRandomElement().getImmutable();
        }
        return vector;
    }
}

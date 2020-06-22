package guard.node;

import guard.GuardHelpers;
import guard.ttp.SystemParameters;

import java.io.Serializable;

public class RoutingTranscript implements Serializable {

    // NumID of the routing node.
    public final int R;
    // Num ID of the preceding node.
    public final int F;
    // Num ID of the subsequent node.
    public final int T;
    // Num ID of the initiator node.
    public final int I;
    // Queried numerical id.
    public final int Q;
    // Nonce.
    public final String N;

    // Construction by the initiator.
    public RoutingTranscript(int i, int t, int q, String n) {
        R = i;
        F = -1;
        T = t;
        I = i;
        Q = q;
        N = n;
    }

    // Construction by a routing node.
    public RoutingTranscript(RoutingTranscript old, int r, int t) {
        R = r;
        F = old.R;
        T = t;
        I = old.I;
        Q = old.Q;
        N = old.N;
    }

    // Returns a string representation of the routing transcript.
    public String toBitString(SystemParameters systemParameters) {
        return GuardHelpers.toBinaryStringWithSize(String.valueOf(R) + F + T + I + Q + N, systemParameters.MESSAGE_LENGTH);
    }
}

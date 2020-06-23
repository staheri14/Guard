package ttp;

import crypto.Scheme;
import crypto.Signature;
import authentication.CircularLookupTable;
import authentication.GuardHelpers;
import authentication.TableProof;
import skipnode.NodeInfo;

public class TTPHelpers {

    public static boolean verifyLookupTable(CircularLookupTable lookupTable, TableProof proof, Scheme scheme,
                                            SystemParameters systemParameters) {
        for(int level = 0; level < lookupTable.getSize(); level++) {
            for(int position = 0; position < 2; position++) {
                NodeInfo neighbor = lookupTable.getNeighbor(level, position);
                // Get the signature.
                Signature neighborSignature = proof.getSignature(level, position, scheme.getPublicParameters());
                // Handle the no neighbor case.
                if(neighbor.invalid) {
                    if(neighborSignature == null) {
                        continue;
                    } else {
                        return false;
                    }
                }
                int neighborNumID = neighbor.getNumID();
                String message = GuardHelpers.constructNeighborMessage(proof.ownerNumID, level, position, systemParameters);
                // Early exit..
                boolean validEntry = scheme.Verify(GuardHelpers.sha256(neighborNumID), message, neighborSignature);
                if(!validEntry) {
                    return false;
                }
            }
        }
        return true;
    }
}

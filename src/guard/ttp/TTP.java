package guard.ttp;

import crypto.Authority;
import crypto.PrivateKey;
import crypto.memento.PrivateKeyMemento;
import crypto.memento.PublicParametersMemento;
import crypto.memento.SignatureMemento;
import crypto.threshold.ThresholdScheme;
import guard.GuardHelpers;
import guard.ttp.packets.requests.RetrieveGuardKeysRequest;
import guard.ttp.packets.requests.RetrieveGuardsRequest;
import guard.ttp.packets.responses.RegistrationResponse;
import guard.ttp.packets.responses.AuthChallengeResponse;
import guard.ttp.packets.responses.RetrieveGuardInfoResponse;
import guard.ttp.packets.responses.RetrieveGuardsResponse;
import protocol.Request;
import protocol.Layer;
import protocol.RequestType;
import protocol.Response;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class TTP extends Layer {

    private final ThresholdScheme scheme;
    private final SystemParameters systemParameters;
    private final Map<String, RegisteredNodeInformation> registrations;
    // We keep a map of name IDs to the addresses that they belong to in order to mitigate
    // performing a name ID search over the skip graph during guard assignment phase.
    private final Map<String, String> nameIDAddresses;

    private final Map<String, String> authChallenges;
    private final GuardPermutation guardPermutation;

    private int lastNumID = 0;

    public TTP(SystemParameters systemParameters, Layer underlay) {
        this.systemParameters = systemParameters;
        registrations = new HashMap<>();
        authChallenges = new HashMap<>();
        scheme = new ThresholdScheme(systemParameters.R_BITS, systemParameters.Q_BITS, systemParameters.IDENTITY_LENGTH,
                systemParameters.MESSAGE_LENGTH);
        scheme.Setup(new Authority());
        guardPermutation = new GuardPermutation(systemParameters.SYSTEM_CAPACITY);
        nameIDAddresses = new HashMap<>();
        setUnderlay(underlay);
    }

    private boolean checkChallengeSolution(String senderAddress, SignatureMemento challengeSolution) {
        String challenge = authChallenges.remove(senderAddress);
        if(challenge == null || challengeSolution == null || !registrations.containsKey(senderAddress)) {
            return false;
        }
        int numID = registrations.get(senderAddress).numID;
        String numIDHashed = GuardHelpers.sha256(numID);
        return scheme.Verify(numIDHashed, challenge, challengeSolution.reconstruct(scheme.getPublicParameters()));
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        if(request.type == RequestType.TTP_REGISTER) {

        }
        return switch(request.type) {
            case TTP_AUTH_CHALLENGE -> authChallenge(request);
            case TTP_REGISTER -> register(request);
            case TTP_RETRIEVE_GUARDS ->  retrieveGuards((RetrieveGuardsRequest) request);
            case TTP_RETRIEVE_GUARD_KEYS -> retrieveGuardKeys((RetrieveGuardKeysRequest) request);
            default -> new Response("unknown request");
        };
    }

    public RegistrationResponse register(Request request) {
        if(lastNumID >= systemParameters.SYSTEM_CAPACITY) {
            return new RegistrationResponse("system capacity reached");
        }
        // Find the numerical id of the node.
        int numID = lastNumID++;
        // Find the name id of the node.
        String nameID = GuardHelpers.getNameIDFromNumID(numID, systemParameters);
        // Extract the private key of the node and prepare it to be sent over the wore.
        PrivateKey signatureKey = scheme.Extract(GuardHelpers.sha256(numID));
        PrivateKeyMemento signatureKeyMemento = new PrivateKeyMemento(signatureKey);
        // Construct the public parameters state to be sent over the wire.
        PublicParametersMemento publicParametersMemento = new PublicParametersMemento(scheme.getPairingParameters(),
                scheme.getPublicParameters());
        // Save the node information.
        registrations.put(request.senderAddress, new RegisteredNodeInformation(request.senderAddress, numID, nameID, signatureKey));
        nameIDAddresses.put(nameID, request.senderAddress);
        // Return the response.
        return new RegistrationResponse(true, numID, nameID, signatureKeyMemento, publicParametersMemento, systemParameters);
    }

    public AuthChallengeResponse authChallenge(Request request) {
        RegisteredNodeInformation info = registrations.get(request.senderAddress);
        if(info == null) {
            return new AuthChallengeResponse(null, "node is not registered");
        }
        if(authChallenges.containsKey(request.senderAddress)) {
            return new AuthChallengeResponse(null, "node is already being challenged");
        }
        String challenge = GuardHelpers.randomBitString(systemParameters.MESSAGE_LENGTH);
        // Save it.
        authChallenges.put(request.senderAddress, challenge);
        return new AuthChallengeResponse(challenge, null);
    }

    public RetrieveGuardsResponse retrieveGuards(RetrieveGuardsRequest request) {
        // Check for authentication.
        if(!checkChallengeSolution(request.senderAddress, request.challengeSolution)) {
            return new RetrieveGuardsResponse("could not authenticate");
        }
        RegisteredNodeInformation nodeInformation = registrations.get(request.senderAddress);
        // Validate the table proof.
        boolean tableProofValid = TTPHelpers.verifyLookupTable(request.circularLookupTable, request.tableProof, scheme, systemParameters);
        if(!tableProofValid) {
            return new RetrieveGuardsResponse("table proof is invalid");
        }
        // Save this node's verified lookup table & table proof.
        nodeInformation.circularLookupTable = request.circularLookupTable;
        nodeInformation.lookupTable = request.circularLookupTable.convertToLookupTable(nodeInformation.numID);
        nodeInformation.tableProof = request.tableProof;
        // Acquire the guard's name ids...
        String nameID = GuardHelpers.getNameIDFromNumID(nodeInformation.numID, systemParameters);
        String[] guardNameIDs = new String[3];
        // Get the main guard.
        guardNameIDs[0] = guardPermutation.findMainGuard(nodeInformation.nameID, systemParameters);
        // Get the left guard.
        guardNameIDs[1] = guardPermutation.findMainGuard(nodeInformation.circularLookupTable.getNeighbor(0, 0).getNameID(), systemParameters);
        // Get the right guard.
        guardNameIDs[2] = guardPermutation.findMainGuard(nodeInformation.circularLookupTable.getNeighbor(0, 1).getNameID(), systemParameters);
        // Save the information.
        nodeInformation.guardNameIDs = guardNameIDs;
        // Split the node's signature key into guards & save it.
        nodeInformation.guardKeys = scheme.KeyDis(nodeInformation.signatureKey, 3, 3,
                GuardHelpers.prependToLength(nameID, systemParameters.IDENTITY_LENGTH));
        // Return the addresses of the guards.
        return new RetrieveGuardsResponse(nameIDAddresses.get(guardNameIDs[0]),
                nameIDAddresses.get(guardNameIDs[1]), nameIDAddresses.get(guardNameIDs[2]), nodeInformation.guardKeys.getY());
    }

    public RetrieveGuardInfoResponse retrieveGuardKeys(RetrieveGuardKeysRequest request) {
        // First, authenticate.
        if(!checkChallengeSolution(request.senderAddress, request.challengeSolution)) {
            return new RetrieveGuardInfoResponse("could not authenticate");
        }
        RegisteredNodeInformation guardInformation = registrations.get(request.senderAddress);
        if(guardInformation == null) {
            return new RetrieveGuardInfoResponse("not registered");
        }
        RegisteredNodeInformation guardedInformation = registrations.get(request.guardedNodeAddress);
        if(guardedInformation == null) {
            return new RetrieveGuardInfoResponse("not a guard");
        }
        String guardNameID = guardInformation.nameID;
        // Make sure that the guard is a guard of the claimed node.
        if(!guardedInformation.guardNameIDs[request.guardIndex].equals(guardNameID)) {
            return new RetrieveGuardInfoResponse("not a guard");
        }
        // Get the guard's partial key.
        BigInteger partialKey = guardedInformation.guardKeys.getPrivateKey(request.guardIndex+1);
        return new RetrieveGuardInfoResponse(partialKey, guardedInformation.lookupTable, guardedInformation.numID);
    }

}

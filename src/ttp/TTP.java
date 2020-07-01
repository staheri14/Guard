package ttp;

import crypto.Authority;
import crypto.PrivateKey;
import crypto.memento.PrivateKeyMemento;
import crypto.memento.PublicParametersMemento;
import crypto.memento.SignatureMemento;
import crypto.threshold.ThresholdScheme;
import authentication.GuardHelpers;
import ttp.packets.requests.AuthChallengeRequest;
import ttp.packets.requests.RetrieveGuardKeysRequest;
import ttp.packets.requests.RetrieveGuardsRequest;
import ttp.packets.responses.RegistrationResponse;
import ttp.packets.responses.AuthChallengeResponse;
import ttp.packets.responses.RetrieveGuardInfoResponse;
import ttp.packets.responses.RetrieveGuardsResponse;
import network.Request;
import network.Layer;
import network.Response;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the TTP layer.
 */
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

    public TTP(SystemParameters systemParameters) {
        this.systemParameters = systemParameters;
        registrations = new HashMap<>();
        authChallenges = new HashMap<>();
        scheme = new ThresholdScheme(systemParameters.R_BITS, systemParameters.Q_BITS, systemParameters.IDENTITY_LENGTH,
                systemParameters.MESSAGE_LENGTH);
        scheme.Setup(new Authority());
        guardPermutation = new GuardPermutation(systemParameters.SYSTEM_CAPACITY);
        nameIDAddresses = new HashMap<>();
    }

    public SystemParameters getSystemParameters() {
        return systemParameters;
    }

    public List<RegisteredNodeInformation> getRegisteredNodes() {
        return new ArrayList<>(registrations.values());
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
        return switch(request.type) {
            case TTP_AUTH_CHALLENGE -> authChallenge((AuthChallengeRequest) request);
            case TTP_REGISTER -> register(request);
            case TTP_RETRIEVE_GUARDS ->  retrieveGuards((RetrieveGuardsRequest) request);
            case TTP_RETRIEVE_GUARD_INFO -> retrieveGuardInfo((RetrieveGuardKeysRequest) request);
            default -> new Response("unknown request");
        };
    }

    public RegistrationResponse register(Request request) {
        if(lastNumID >= systemParameters.SYSTEM_CAPACITY) {
            return new RegistrationResponse("TTP.register: system capacity reached");
        }
        if(registrations.containsKey(request.sourceAddress)) {
            return new RegistrationResponse("TTP.register: already registered");
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
        // Find an initiator address for the registered node so that it can insert itself into the skip graph.
        String initiatorAddress = (registrations.isEmpty()) ? null : ((RegisteredNodeInformation) registrations.values().toArray()[0]).address;
        // Save the node information.
        registrations.put(request.sourceAddress, new RegisteredNodeInformation(request.sourceAddress, numID, nameID, signatureKey));
        nameIDAddresses.put(nameID, request.sourceAddress);
        // Return the response.
        return new RegistrationResponse(true, numID, nameID, signatureKeyMemento, publicParametersMemento, systemParameters, initiatorAddress);
    }

    public AuthChallengeResponse authChallenge(AuthChallengeRequest request) {
        // Choose the correct challenge map depending on the request.
        RegisteredNodeInformation info = registrations.get(request.sourceAddress);
        if(info == null) {
            return new AuthChallengeResponse(null, "TTP.authChallenge: node is not registered");
        }
        if(authChallenges.containsKey(request.sourceAddress)) {
            return new AuthChallengeResponse(null, "TTP.authChallenge: node is already being challenged");
        }
        String challenge = GuardHelpers.randomBitString(systemParameters.MESSAGE_LENGTH);
        // Save it.
        authChallenges.put(request.sourceAddress, challenge);
        return new AuthChallengeResponse(challenge, null);
    }

    public RetrieveGuardsResponse retrieveGuards(RetrieveGuardsRequest request) {
        // Check for authentication.
        if(!checkChallengeSolution(request.sourceAddress, request.challengeSolution)) {
            return new RetrieveGuardsResponse("TTP.retrieveGuards: could not authenticate");
        }
        RegisteredNodeInformation nodeInformation = registrations.get(request.sourceAddress);
        // Validate the table proof.
        boolean tableProofValid = TTPHelpers.verifyLookupTable(request.circularLookupTable, request.tableProof, scheme, systemParameters);
        if(!tableProofValid) {
            return new RetrieveGuardsResponse("TTP.retrieveGuards: table proof is invalid");
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

    public RetrieveGuardInfoResponse retrieveGuardInfo(RetrieveGuardKeysRequest request) {
        // First, authenticate.
        if(!checkChallengeSolution(request.sourceAddress, request.challengeSolution)) {
            return new RetrieveGuardInfoResponse("TTP.retrieveGuardKeys: could not authenticate");
        }
        RegisteredNodeInformation guardInformation = registrations.get(request.sourceAddress);
        if(guardInformation == null) {
            return new RetrieveGuardInfoResponse("TTP.retrieveGuardKeys: not registered");
        }
        RegisteredNodeInformation guardedInformation = registrations.get(request.guardedNodeAddress);
        if(guardedInformation == null) {
            return new RetrieveGuardInfoResponse("TTP.retrieveGuardKeys: not a guard");
        }
        String guardNameID = guardInformation.nameID;
        // Make sure that the guard is a guard of the claimed node.
        if(!guardedInformation.guardNameIDs[request.guardIndex].equals(guardNameID)) {
            return new RetrieveGuardInfoResponse("TTP.retrieveGuardKeys: not a guard");
        }
        // Get the guard's partial key.
        BigInteger partialKey = guardedInformation.guardKeys.getPrivateKey(request.guardIndex+1);
        return new RetrieveGuardInfoResponse(partialKey, guardedInformation.lookupTable, guardedInformation.numID);
    }

}

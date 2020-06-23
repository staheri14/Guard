package authentication;

import crypto.PrivateKey;
import crypto.PublicParameters;
import crypto.Scheme;
import crypto.Signature;
import crypto.memento.SignatureMemento;
import crypto.memento.SignatureShareMemento;
import crypto.threshold.SignatureShare;
import crypto.threshold.ThresholdScheme;
import authentication.packets.requests.*;
import authentication.packets.responses.*;
import authentication.workers.GuardSignWorker;
import authentication.workers.SignWorker;
import authentication.workers.VerifySignatureWorker;
import ttp.SystemParameters;
import ttp.packets.requests.*;
import ttp.packets.responses.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import protocol.RequestType;
import skipnode.LookupTable;
import skipnode.NodeInfo;
import skipnode.SkipNode;
import protocol.Request;
import protocol.Layer;
import protocol.Response;
import protocol.packets.responses.AckResponse;
import skipnode.packets.requests.GetInfoRequest;
import skipnode.packets.requests.InsertRequest;
import skipnode.packets.requests.SearchByNumIDRequest;
import skipnode.packets.responses.NodeInfoResponse;
import skipnode.packets.responses.SearchResultResponse;

import java.util.*;


public class Authentication extends Layer {

    private final String ttpAddress;
    private SkipNode skipNode;

    // Maintained during insertion:
    private NodeInfo leftGuardNeighbor;
    private NodeInfo rightGuardNeighbor;

    // Initialized at the registration phase:
    private PublicParameters publicParameters;
    private SystemParameters systemParameters;
    private Pairing pairing;
    private PrivateKey signatureKey;

    // Initialized at the construction phase:
    private CircularLookupTable circularLookupTable;
    private TableProof tableProof;

    // Initialized at the guard assignment phase:
    private String[] guardAddresses;
    private Element[] publicDistKey;
    private final Map<String, RetrieveGuardInfoResponse> guardInformation;

    public Authentication(String ttpAddress) {
        this.ttpAddress = ttpAddress;
        leftGuardNeighbor = new NodeInfo();
        rightGuardNeighbor = new NodeInfo();
        guardInformation = new HashMap<>();
    }

    @Override
    public void setOverlay(Layer layer) {
        if(!(layer instanceof SkipNode)) {
            return;
        }
        this.overlay = layer;
        this.skipNode = (SkipNode) layer;
    }

    @Override
    public Response handleReceivedRequest(Request request) {
        // Only handle the authenticated search requests.
        if(request.type == RequestType.ROUTE_SEARCH_NUM_ID && request instanceof AuthRouteSearchNumIDRequest) {
            return authRouteSearchNumID((AuthRouteSearchNumIDRequest) request);
        }
        return switch(request.type) {
            case NODE_REGISTER -> nodeRegister();
            case NODE_CONSTRUCT -> nodeConstruct();
            case NODE_ASSIGN -> nodeAssign();
            case GET_TABLE_PROOF_ENTRY -> getTableProofEntry((GetTableProofEntryRequest) request);
            case SEARCH_BY_NUM_ID -> authSearchByNumID((SearchByNumIDRequest) request);
            case INSERT -> authInsert((InsertRequest) request);
            case GET_GUARD_NEIGHBOR -> getGuardNeighbor((GetGuardNeighborRequest) request);
            case SET_GUARD_NEIGHBOR -> setGuardNeighbor((SetGuardNeighborRequest) request);
            case INFORM_GUARD -> informGuard((InformGuardRequest) request);
            case GET_GUARD_SIGNATURE ->  getGuardSignature((GetGuardSignatureRequest) request);
            default -> null;
        };
    }

    private Response sendTTP(Request request) {
        return send(ttpAddress, request);
    }

    public AckResponse setGuardNeighbor(SetGuardNeighborRequest request) {
        if(request.position == 0) leftGuardNeighbor = request.neighbor;
        else if(request.position == 1) rightGuardNeighbor = request.neighbor;
        else return new AckResponse("invalid position");
        return new AckResponse(null);
    }

    public NodeInfoResponse getGuardNeighbor(GetGuardNeighborRequest request) {
        if(request.position == 0) return new NodeInfoResponse(leftGuardNeighbor, null);
        if(request.position == 1) return new NodeInfoResponse(rightGuardNeighbor, null);
        return new NodeInfoResponse(null, "invalid position");
    }

    public PartialSignatureResponse getGuardSignature(GetGuardSignatureRequest request) {
        String guardedNodeAddress = request.destinationAddress;
        if(!guardInformation.containsKey(guardedNodeAddress)) {
            return new PartialSignatureResponse(null, "not a guard of this node");
        }
        RoutingTranscript rt = request.routingTranscript;
        RetrieveGuardInfoResponse guardInfo = guardInformation.get(guardedNodeAddress);
        // Find the previous node
        // Verify the route.
        // 1. Find the preceding neighbor in the guarded lookup table if the node is not an initiator.
        NodeInfo precedingNeighbor = null;
        int precedingNeighborLevel = systemParameters.getMaxLevels();
        if(rt.F != -1) {
            for (int level = 0; level < guardInfo.guardedLookupTable.getSize(); level++) {
                for (int pos = 0; pos < 2; pos++) {
                    NodeInfo neighbor = guardInfo.guardedLookupTable.getNeighbor(level, pos);
                    if (neighbor.invalid) continue;
                    if (neighbor.getNumID() == rt.F) {
                        precedingNeighborLevel = level;
                        precedingNeighbor = neighbor;
                    }
                }
            }
        }
        // If no such neighbor exists for an intermediate node, there is a problem.
        if (rt.F != -1 && precedingNeighbor == null) {
            return new PartialSignatureResponse(null, "no such preceding node");
        }
        // 2. Verify the subsequent neighbor in the guarded lookup table (no circular 0th level version)
        LookupTable.NextHop nextCorrectHop = LookupTable.findNextHop(rt.R, rt.Q, precedingNeighborLevel,
                guardInfo.guardedLookupTable);
        if ((nextCorrectHop == null && rt.T != -1) || (nextCorrectHop != null && nextCorrectHop.node.getNumID() != rt.T)) {
            return new PartialSignatureResponse(null, "incorrect next hop");
        }
        // Partially sign the routing transcript.
        String guardedNameID = GuardHelpers.getNameIDFromNumID(guardInfo.guardedNumID, systemParameters);
        SignatureShare sgn = ThresholdScheme.ThrSigIndividual(rt.toBitString(systemParameters),
                GuardHelpers.prependToLength(guardedNameID, systemParameters.IDENTITY_LENGTH),
                guardInfo.partialSignatureKey, publicParameters);
        return new PartialSignatureResponse(new SignatureShareMemento(sgn), null);
    }

    public AuthSearchResultResponse authSearchByNumID(SearchByNumIDRequest request) {
        // Make sure that the previous phases are complete before performing a search.
        if(signatureKey == null) {
            return new AuthSearchResultResponse(null, null, "not registered yet");
        } else if(tableProof == null) {
            return new AuthSearchResultResponse(null, null, "not constructed yet");
        } else if(guardAddresses == null) {
            return new AuthSearchResultResponse(null, null, "not assigned yet");
        }
        String nonce = GuardHelpers.randomBitString(systemParameters.NONCE_LENGTH);
        AuthSearchResultResponse r = authRouteSearchNumID(new AuthRouteSearchNumIDRequest(new LinkedList<>(), request.target,
                systemParameters.getMaxLevels(), nonce));
        if(r.isError()) {
            return new AuthSearchResultResponse(r.routingProofs, null, r.errorMessage);
        } else if(systemParameters.VERIFY_AT_INITIATOR && !batchVerify(r.routingProofs)) {
            // If the proofs should be verified at the initiator, do so.
            return new AuthSearchResultResponse(r.routingProofs, null, "could not verify the search path at the initator");
        }
        return r;
    }

    public boolean batchVerify(List<RoutingProof> routingProofs) {
        VerifySignatureWorker[] workers = new VerifySignatureWorker[2 * routingProofs.size()];
        Thread[] verifierThreads = new Thread[workers.length];
        Iterator<RoutingProof> it = routingProofs.iterator();
        for(int i = 0; i < workers.length && it.hasNext(); i += 2) {
            RoutingProof proof = it.next();
            workers[i] = new VerifySignatureWorker(proof.selfSignature, publicParameters, pairing,
                    GuardHelpers.prependToLength(GuardHelpers.sha256(proof.transcript.R), systemParameters.IDENTITY_LENGTH),
                    proof.transcript.toBitString(systemParameters));
            workers[i + 1] =  new VerifySignatureWorker(proof.guardSignature, publicParameters, pairing,
                    GuardHelpers.prependToLength(GuardHelpers.getNameIDFromNumID(proof.transcript.R, systemParameters), systemParameters.IDENTITY_LENGTH),
                    proof.transcript.toBitString(systemParameters));
            verifierThreads[i] = new Thread(workers[i]);
            verifierThreads[i + 1] = new Thread(workers[i + 1]);
        }
        // Start the threads.
        for (Thread verifierThread : verifierThreads) {
            verifierThread.start();
        }
        // Complete the threads.
        for (Thread verifierThread : verifierThreads) {
            try {
                verifierThread.join();
            } catch (InterruptedException e) {
                System.err.println("error while waiting for the verifier threads to complete during batch verification");
                e.printStackTrace();
            }
        }
        // Check the results.
        for(VerifySignatureWorker worker : workers) {
            if(!worker.valid) return false;
        }
        return true;
    }

    // Check the piggybacked routing proofs & append the new proof.
    public AuthSearchResultResponse authRouteSearchNumID(AuthRouteSearchNumIDRequest request) {
        boolean initiator = request.routingProofs.size() == 0;
        LookupTable.NextHop nextHop = LookupTable.findNextHop(skipNode.getInfo().getNumID(), request.target, request.level, skipNode.getLookupTable());
        // Construct the transcript accordingly depending on whether we are the initiator/search result or not.
        RoutingTranscript rt;
        if(initiator) {
            rt = new RoutingTranscript(skipNode.getInfo().getNumID(), (nextHop == null) ? -1 : nextHop.node.getNumID(),
                    request.target, request.nonce);
        } else {
            RoutingTranscript lastTranscript = request.routingProofs.getLast().transcript;
            rt = new RoutingTranscript(lastTranscript, skipNode.getInfo().getNumID(), (nextHop == null) ? -1 : nextHop.node.getNumID());
        }
        boolean shouldVerify = !initiator && systemParameters.VERIFY_AT_ROUTER;
        // Create the workers.
        VerifySignatureWorker proofSelfSignatureVerifier = null;
        VerifySignatureWorker proofGuardSignatureVerifier = null;
        // Only create the verifier threads
        if(shouldVerify) {
            RoutingProof lastProof = request.routingProofs.getLast();
            proofSelfSignatureVerifier = new VerifySignatureWorker(lastProof.selfSignature, publicParameters, pairing,
                    GuardHelpers.prependToLength(GuardHelpers.sha256(lastProof.transcript.R), systemParameters.IDENTITY_LENGTH),
                    lastProof.transcript.toBitString(systemParameters));
            proofGuardSignatureVerifier =  new VerifySignatureWorker(lastProof.guardSignature, publicParameters, pairing,
                    GuardHelpers.prependToLength(GuardHelpers.getNameIDFromNumID(lastProof.transcript.R, systemParameters), systemParameters.IDENTITY_LENGTH),
                    lastProof.transcript.toBitString(systemParameters));
        }
        SignWorker selfSignWorker = new SignWorker(rt.toBitString(systemParameters), signatureKey, publicParameters);
        GuardSignWorker[] guardSignWorkers = new GuardSignWorker[] {
                new GuardSignWorker(guardAddresses[0], rt, this),
                new GuardSignWorker(guardAddresses[1], rt, this),
                new GuardSignWorker(guardAddresses[2], rt, this)
        };
        // Create the threads.
        Thread[] threads = new Thread[6];
        threads[2] = new Thread(selfSignWorker);
        threads[3] = new Thread(guardSignWorkers[0]);
        threads[4] = new Thread(guardSignWorkers[1]);
        threads[5] = new Thread(guardSignWorkers[2]);
        // Run the threads.
        threads[2].start();
        threads[3].start();
        threads[4].start();
        threads[5].start();
        // Start the verification threads if the parameter is set.
        if(shouldVerify) {
            threads[0] = new Thread(proofSelfSignatureVerifier);
            threads[1] = new Thread(proofGuardSignatureVerifier);
            threads[0].start();
            threads[1].start();
        }
        // Wait for the threads to complete.
        try {
            for (Thread t : threads) {
                if(t != null) t.join();
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        if(shouldVerify && (!proofSelfSignatureVerifier.valid || !proofGuardSignatureVerifier.valid)) {
            return new AuthSearchResultResponse(request.routingProofs, null, "could not verify the previous proof");
        }
        // Check if guard signatures were successfully received.
        for(int i = 0; i < 3; i++) {
            if(guardSignWorkers[i].errorMsg != null) {
                return new AuthSearchResultResponse(request.routingProofs, null, guardSignWorkers[i].errorMsg);
            }
        }
        // Collect the signature shares from the guards.
        SignatureShare[] signatureShares = new SignatureShare[] {
                guardSignWorkers[0].signatureShare.reconstruct(publicParameters),
                guardSignWorkers[1].signatureShare.reconstruct(publicParameters),
                guardSignWorkers[2].signatureShare.reconstruct(publicParameters)
        };
        // Reconstruct the guard signature from the partial signatures.
        Signature guardSignature = ThresholdScheme.Reconstruct(new int[] { 1, 2, 3 }, signatureShares, publicDistKey, publicParameters);
        if(guardSignature == null) {
            return new AuthSearchResultResponse(request.routingProofs, null, "could not reconstruct the guard signature");
        }
        // Construct the routing proof.
        RoutingProof rp = new RoutingProof(rt, new SignatureMemento(selfSignWorker.signature), new SignatureMemento(guardSignature));
        // Add the proof to the partial result.
        request.routingProofs.addLast(rp);
        // Let the skip node layer handle the request.
        SearchResultResponse r = skipNode.routeSearchNumID(request);
        if(r.isError()) {
            return new AuthSearchResultResponse(null, null, r.errorMessage);
        } else if(r instanceof AuthSearchResultResponse) {
            return (AuthSearchResultResponse) r;
        }
        return new AuthSearchResultResponse(request.routingProofs, r.result, null);
    }

    public AckResponse nodeRegister() {
        Response r = sendTTP(new RegisterRequest());
        if(r.isError()) {
            return new AckResponse(r.errorMessage);
        }
        RegistrationResponse regResponse = (RegistrationResponse) r;
        if(regResponse.publicParametersMemento == null || regResponse.assignedPrivateKeyMemento == null || regResponse.systemParameters == null) {
            return new AckResponse("malformed registration response");
        }
        skipNode.initialize(regResponse.assignedNumID, regResponse.assignedNameID,  regResponse.systemParameters, this);
        systemParameters = regResponse.systemParameters;
        publicParameters = regResponse.publicParametersMemento.reconstruct();
        pairing = regResponse.publicParametersMemento.reconstructPairing();
        signatureKey = regResponse.assignedPrivateKeyMemento.reconstruct(publicParameters);
        return new AckResponse(null);
    }

    public SignatureMemento authenticateWithTTP() {
        Response r = sendTTP(new AuthChallengeRequest());
        if(r.isError()) {
            System.err.println("authenticateWithTTP: " + r.errorMessage);
            return null;
        }
        Signature sgn = Scheme.SignGlobal(((AuthChallengeResponse) r).challenge, signatureKey, publicParameters);
        return new SignatureMemento(sgn);
    }

    public AckResponse authInsert(InsertRequest request) {
        // Perform its own insertion. The nodes' lookup table is built.
        AckResponse s = skipNode.insert(request);
        if(s.isError()) {
            return s;
        }
        LookupTable lookupTable = skipNode.getLookupTable();
        // We need to handle the 0th level since it needs to be circular for Guard to work.
        String newLeftAddr = null;
        String newRightAddr = null;
        // Get the zero level neighbors.
        NodeInfo zeroLeft = lookupTable.getNeighbor(0, 0);
        NodeInfo zeroRight = lookupTable.getNeighbor(0, 1);
        // This node was inserted in between two nodes at level 0.
        if(!zeroLeft.invalid && !zeroRight.invalid) {
            newLeftAddr = zeroLeft.getAddress();
            newRightAddr = zeroRight.getAddress();
        } else if(zeroLeft.invalid && !zeroRight.invalid) {
            // This node was inserted at the left edge at level 0.
            newRightAddr = zeroRight.getAddress();
            Response r = send(newRightAddr, new GetGuardNeighborRequest(0));
            if(r.isError()) {
                return new AckResponse(r.errorMessage);
            }
            newLeftAddr = ((NodeInfoResponse) r).nodeInfo.getAddress();
        } else if(!zeroLeft.invalid) {
            // This node was inserted at the right edge at level 0.
            newLeftAddr = zeroLeft.getAddress();
            Response r = send(newLeftAddr, new GetGuardNeighborRequest(1));
            if(r.isError()) {
                return new AckResponse(r.errorMessage);
            }
            newRightAddr = ((NodeInfoResponse) r).nodeInfo.getAddress();
        } else {
            // This node is the first node in the skip graph.
            leftGuardNeighbor = skipNode.getInfo();
            rightGuardNeighbor = skipNode.getInfo();
            return new AckResponse(null);
        }
        // Complete the 0th level connections.
        // Handle the new right neighbor.
        Response r = send(newRightAddr, new GetInfoRequest());
        if(r.isError()) {
            return new AckResponse(r.errorMessage);
        }
        rightGuardNeighbor = ((NodeInfoResponse) r).nodeInfo;
        send(newRightAddr, new SetGuardNeighborRequest(0, skipNode.getInfo()));
        // Handle the new left neighbor.
        r = send(newLeftAddr, new GetInfoRequest());
        if(r.isError()) {
            return new AckResponse(r.errorMessage);
        }
        leftGuardNeighbor = ((NodeInfoResponse) r).nodeInfo;
        send(newLeftAddr, new SetGuardNeighborRequest(1, skipNode.getInfo()));
        // Success.
        return new AckResponse(null);
    }

    public TableProofEntryResponse getTableProofEntry(GetTableProofEntryRequest request) {
        if(circularLookupTable == null) {
            circularLookupTable = new CircularLookupTable(skipNode.getLookupTable(), leftGuardNeighbor, rightGuardNeighbor);
        }
        NodeInfo claimedNeighbor = circularLookupTable.getNeighbor(request.neighborLevel, 1-request.relativePosition);
        if(claimedNeighbor == null || claimedNeighbor.invalid || claimedNeighbor.getNumID() != request.requesterNumID) {
            return new TableProofEntryResponse(null, "invalid neighbor");
        }
        String message = GuardHelpers.toBinaryStringWithSize(request.requesterNumID + "" + request.relativePosition + "" + request.neighborLevel,
                systemParameters.MESSAGE_LENGTH);
        // Create the signature.
        Signature signature = Scheme.SignGlobal(message, signatureKey, publicParameters);
        return new TableProofEntryResponse(new SignatureMemento(signature), null);
    }

    public AckResponse nodeConstruct() {
        if(circularLookupTable == null) {
            circularLookupTable = new CircularLookupTable(skipNode.getLookupTable(), leftGuardNeighbor, rightGuardNeighbor);
        }
        tableProof = new TableProof(systemParameters.getMaxLevels(), skipNode.getInfo().getNumID());
        // Iterate through the neighbors and request table proof signatures from them.
        for(int i = 0; i < circularLookupTable.getSize(); i++) {
            for(int j = 0; j < 2; j++) {
                NodeInfo neighbor = circularLookupTable.getNeighbor(i, j);
                if(neighbor.invalid) continue;
                Response r = send(neighbor.getAddress(), new GetTableProofEntryRequest(skipNode.getInfo().getNumID(), i, j));
                if(r.isError()) {
                    return new AckResponse(r.errorMessage);
                }
                tableProof.insertProof(i, j, ((TableProofEntryResponse) r).signature);
            }
        }
        // Success.
        return new AckResponse(null);
    }

    public AckResponse nodeAssign() {
        SignatureMemento challengeSolution = authenticateWithTTP();
        if(challengeSolution == null) {
            return new AckResponse("could not authenticate with ttp");
        }
        // Retrieve the guards of this node from the TTP.
        CircularLookupTable circularLookupTable = new CircularLookupTable(skipNode.getLookupTable(), leftGuardNeighbor, rightGuardNeighbor);
        Response r = sendTTP(new RetrieveGuardsRequest(challengeSolution, tableProof, circularLookupTable));
        if(r.isError()) {
            return new AckResponse(r.errorMessage);
        }
        RetrieveGuardsResponse guardResponse = (RetrieveGuardsResponse) r;
        guardAddresses = new String[3];
        guardAddresses[0] = guardResponse.mainGuardAddress;
        guardAddresses[1] = guardResponse.leftGuardAddress;
        guardAddresses[2] = guardResponse.rightGuardAddress;
        // Reconstruct the public parameters for distributed keys.
        publicDistKey = guardResponse.reconstructY(publicParameters);
        // Inform the guards as they are this node's new guards.
        for(int i = 0; i < 3; i++) {
            r = send(guardAddresses[i], new InformGuardRequest(i));
            if(r.isError()) {
                return new AckResponse(r.errorMessage);
            }
        }
        return new AckResponse(null);
    }

    public AckResponse informGuard(InformGuardRequest request) {
        // Authenticate with the TTP.
        SignatureMemento challengeSolution = authenticateWithTTP();
        if(challengeSolution == null) {
            return new AckResponse("could not authenticate with TTP");
        }
        Response r = sendTTP(new RetrieveGuardKeysRequest(challengeSolution, request.senderAddress, request.guardIndex));
        if(r.isError()) {
            return new AckResponse(r.errorMessage);
        }
        RetrieveGuardInfoResponse guardKeys = (RetrieveGuardInfoResponse) r;
        guardInformation.put(request.senderAddress, guardKeys);
        return new AckResponse(null);
    }
}

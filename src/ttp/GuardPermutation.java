package ttp;

import authentication.GuardHelpers;

import java.security.SecureRandom;
import java.util.*;

/**
 * Represents a private pseudorandom permutation over the name ID space.
 * Used by TTP to find guard name IDs.
 */
public class GuardPermutation {

    private final Map<Integer, Integer> permutationMap;

    public GuardPermutation(int inputSpaceSize) {
        permutationMap = new HashMap<>();
        // SecureRandom seeds itself.
        SecureRandom rand = new SecureRandom();
        List<Integer> inputSpace = new ArrayList<>(inputSpaceSize);
        List<Integer> outputSpace = new ArrayList<>(inputSpaceSize);
        for(int i = 0; i < inputSpaceSize; i++) {
            inputSpace.add(i);
            outputSpace.add(i);
        }
        // Shuffle the outputs corresponding to each input.
        Collections.shuffle(outputSpace, rand);
        for(int i = 0; i < inputSpaceSize; i++) {
            permutationMap.put(inputSpace.get(i), outputSpace.get(i));
        }
    }

    // Finds the name ID of the main guard of the node with the given name ID.
    public String findMainGuard(String ownerNameID, SystemParameters parameters) {
        // Convert the name ID to its integer representation.
        int input = Integer.parseInt(ownerNameID, 2);
        // Get the integer representation of the guard's name ID from the permutation.
        int output = permutationMap.get(input);
        // Return the name ID of the guard.
        return GuardHelpers.prependToLength(Integer.toBinaryString(output), parameters.getNameIDLength());
    }
}

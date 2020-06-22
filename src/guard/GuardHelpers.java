package guard;

import guard.ttp.SystemParameters;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class GuardHelpers {

    static MessageDigest digest = null;
    // TODO: feed a seed
    static SecureRandom rand = new SecureRandom();


    public static String constructNeighborMessage(int ownerNumID, int relativeLevel, int relativePosition, SystemParameters parameters) {
        return toBinaryStringWithSize(ownerNumID + "" + relativePosition + "" + relativeLevel,
                parameters.MESSAGE_LENGTH);
    }

    public static String toBinaryStringWithSize(String s, int size) {
        return prependToLength(toBinaryString(s), size);
    }

    public static String toBinaryString(String s) {
        return new BigInteger(s.getBytes()).toString(2);
    }

    /**
     * Prepends `0` to the original string until targetLength is reached.
     */
    public static String prependToLength(String original, int targetLength) {
        StringBuilder originalBuilder = new StringBuilder(original);
        while(originalBuilder.length() < targetLength) {
            originalBuilder.insert(0, '0');
        }
        original = originalBuilder.toString();
        return original;
    }

    public static String getNameIDFromNumID(int numID, SystemParameters systemParameters) {
        // Get the binary representation of the numerical id.
        String binaryRepresentation = Integer.toBinaryString(numID);
        return GuardHelpers.prependToLength(binaryRepresentation, systemParameters.getNameIDLength());
    }

    /**
     * Hashes the given integer and returns it as a bit string of 256 length.
     */
    public static String sha256(int input) {
        if(digest == null) {
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        byte[] outputBytes = digest.digest(String.valueOf(input).getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : outputBytes) {
            String bitPiece = String.format("%8s", Integer.toBinaryString(b & 0xFF))
                    .replace(' ', '0');
            sb.append(bitPiece);
        }
        return sb.toString();
    }

    public static String randomBitString(int size) {
        StringBuilder s = new StringBuilder();
        while(s.length() < size) {
            s.append(rand.nextBoolean() ? '0' : '1');
        }
        return s.toString();
    }

}

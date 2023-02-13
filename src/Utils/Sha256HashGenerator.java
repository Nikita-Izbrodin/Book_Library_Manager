package Utils;

import javax.swing.JOptionPane;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// The SHA-256 algorithm generates an almost unique, fixed-size 256-bit (32-byte) hash.
// This is a one-way function, so the result cannot be decrypted back to the original value.
public class Sha256HashGenerator implements HashGenerator {

    public String getHashValueOf(String originalString) {

        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        assert messageDigest != null;

        byte[] hashedString = messageDigest.digest(originalString.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashedString);
    }

    private String bytesToHex(byte[] hashedString) {

        StringBuilder hexadecimalString = new StringBuilder(2 * hashedString.length);

        for (byte b : hashedString) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexadecimalString.append('0');
            }
            hexadecimalString.append(hex);
        }

        return hexadecimalString.toString();
    }
}
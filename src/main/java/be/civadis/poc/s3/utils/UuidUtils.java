package be.civadis.poc.s3.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class UuidUtils {
    private static Logger logger = LoggerFactory.getLogger(UuidUtils.class);

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private UuidUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateTicketUUID() throws NoSuchAlgorithmException {
        try {
            return generateUniqueKeysWithUUIDAndMessageDigest();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
            throw new NoSuchAlgorithmException("Erreur lors de la génération du ticket");
        }
    }

    private static String generateUniqueKeysWithUUIDAndMessageDigest() throws NoSuchAlgorithmException {
        MessageDigest salt = MessageDigest.getInstance("SHA-256");
        salt.update(UUID.randomUUID()
            .toString()
            .getBytes(StandardCharsets.UTF_8));
        return bytesToHex(salt.digest());
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}

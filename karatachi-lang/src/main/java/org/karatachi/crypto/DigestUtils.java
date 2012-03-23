package org.karatachi.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.karatachi.exception.IncompatiblePlatformException;

public class DigestUtils {
    public static final String DEFAULT_DIGEST = "SHA1";

    public static MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance(DEFAULT_DIGEST);
        } catch (NoSuchAlgorithmException e) {
            throw new IncompatiblePlatformException(e);
        }
    }
}

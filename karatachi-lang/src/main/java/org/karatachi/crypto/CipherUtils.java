package org.karatachi.crypto;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.karatachi.exception.IncompatiblePlatformException;
import org.karatachi.translator.ByteArrayTranslator;

public class CipherUtils {
    public static final String DEFAULT_SYMMETRIC_CIPHER_ALGORITHM = "AES";
    public static final String DEFAULT_SYMMETRIC_CIPHER =
            "AES/CBC/PKCS5Padding";

    public static final String DEFAULT_ASYMMETRIC_CIPHER_ALGORITHM = "RSA";
    public static final String DEFAULT_ASYMMETRIC_CIPHER =
            "RSA/ECB/PKCS1Padding";

    public static class Symmetric {
        public static void main(String[] args) {
            byte[] key = generateKey();
            byte[] params = generateParams(key);
            System.out.println("key=" + ByteArrayTranslator.toBase64(key));
            System.out.println("params=" + ByteArrayTranslator.toBase64(params));
        }

        public static byte[] generateKey() {
            try {
                KeyGenerator keygen =
                        KeyGenerator.getInstance(DEFAULT_SYMMETRIC_CIPHER_ALGORITHM);
                SecretKey key = keygen.generateKey();
                return key.getEncoded();
            } catch (NoSuchAlgorithmException e) {
                throw new IncompatiblePlatformException(e);
            }
        }

        public static byte[] generateParams(byte[] key) {
            try {
                Cipher cipher = createEncrypter(key, null);
                return cipher.getParameters().getEncoded();
            } catch (IOException e) {
                throw new IncompatiblePlatformException(e);
            } catch (InvalidKeyException e) {
                throw new IncompatiblePlatformException(e);
            } catch (InvalidAlgorithmParameterException e) {
                throw new IncompatiblePlatformException(e);
            }
        }

        public static Cipher createEncrypter(byte[] key, byte[] params)
                throws InvalidKeyException, InvalidAlgorithmParameterException {
            return createCipher(Cipher.ENCRYPT_MODE, key, params);
        }

        public static Cipher createDecrypter(byte[] key, byte[] params)
                throws InvalidKeyException, InvalidAlgorithmParameterException {
            return createCipher(Cipher.DECRYPT_MODE, key, params);
        }

        private static Cipher createCipher(int mode, byte[] key, byte[] params)
                throws InvalidKeyException, InvalidAlgorithmParameterException {
            try {
                Key keyspec =
                        new SecretKeySpec(key,
                                DEFAULT_SYMMETRIC_CIPHER_ALGORITHM);
                Cipher cipher = Cipher.getInstance(DEFAULT_SYMMETRIC_CIPHER);
                if (params != null) {
                    AlgorithmParameters algoparams =
                            AlgorithmParameters.getInstance(keyspec.getAlgorithm());
                    algoparams.init(params);
                    cipher.init(mode, keyspec, algoparams);
                } else {
                    cipher.init(mode, keyspec);
                }
                return cipher;
            } catch (IOException e) {
                throw new IncompatiblePlatformException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new IncompatiblePlatformException(e);
            } catch (NoSuchPaddingException e) {
                throw new IncompatiblePlatformException(e);
            }
        }
    }

    public static class ASymmetricKey {
        private final boolean _private;
        private final byte[] key;

        public ASymmetricKey(boolean privateKey, byte[] key) {
            this._private = privateKey;
            this.key = key;
        }

        public boolean isPrivate() {
            return _private;
        }

        public byte[] getKey() {
            return key;
        }
    }

    public static class ASymmetric {
        public static void main(String[] args) {
            ASymmetricKey[] key = generateKey();
            System.out.println("private="
                    + ByteArrayTranslator.toBase64(key[0].getKey()));
            System.out.println("public="
                    + ByteArrayTranslator.toBase64(key[1].getKey()));
        }

        public static ASymmetricKey[] generateKey() {
            try {
                KeyPairGenerator keygen =
                        KeyPairGenerator.getInstance(DEFAULT_ASYMMETRIC_CIPHER_ALGORITHM);
                KeyPair pair = keygen.generateKeyPair();
                return new ASymmetricKey[] {
                        new ASymmetricKey(true, pair.getPrivate().getEncoded()),
                        new ASymmetricKey(false, pair.getPublic().getEncoded()) };
            } catch (NoSuchAlgorithmException e) {
                throw new IncompatiblePlatformException(e);
            }
        }

        public static Cipher createEncrypter(ASymmetricKey key)
                throws InvalidKeyException, InvalidAlgorithmParameterException {
            return createCipher(Cipher.ENCRYPT_MODE, key.isPrivate(),
                    key.getKey());
        }

        public static Cipher createDecrypter(ASymmetricKey key)
                throws InvalidKeyException, InvalidAlgorithmParameterException {
            return createCipher(Cipher.DECRYPT_MODE, key.isPrivate(),
                    key.getKey());
        }

        private static Cipher createCipher(int mode, boolean isPrivate,
                byte[] key) throws InvalidKeyException,
                InvalidAlgorithmParameterException {
            try {
                KeyFactory factory =
                        KeyFactory.getInstance(DEFAULT_ASYMMETRIC_CIPHER_ALGORITHM);

                Key keyspec;
                if (isPrivate) {
                    keyspec =
                            factory.generatePrivate(new PKCS8EncodedKeySpec(key));
                } else {
                    keyspec =
                            factory.generatePublic(new X509EncodedKeySpec(key));
                }

                Cipher cipher = Cipher.getInstance(DEFAULT_ASYMMETRIC_CIPHER);
                cipher.init(mode, keyspec);

                return cipher;
            } catch (InvalidKeySpecException e) {
                throw new IncompatiblePlatformException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new IncompatiblePlatformException(e);
            } catch (NoSuchPaddingException e) {
                throw new IncompatiblePlatformException(e);
            }
        }
    }
}

package org.karatachi.crypto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;

import org.karatachi.crypto.CipherUtils.ASymmetric;
import org.karatachi.crypto.CipherUtils.ASymmetricKey;
import org.karatachi.crypto.CipherUtils.Symmetric;

public class CipherStreamUtils {
    public static CipherOutputStream encryptedOutputStream(OutputStream out,
            ASymmetricKey asym_key) throws IOException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        byte[] sym_key = Symmetric.generateKey();

        Cipher asym_cipher = ASymmetric.createEncrypter(asym_key);
        Cipher sym_cipher = Symmetric.createEncrypter(sym_key, null);

        byte[] sym_param = sym_cipher.getParameters().getEncoded();

        byte[] encrypted_sym_key = asym_cipher.doFinal(sym_key);
        byte[] encrypted_sym_param = asym_cipher.doFinal(sym_param);

        DataOutputStream dataout = new DataOutputStream(out);

        dataout.writeInt(encrypted_sym_key.length);
        dataout.write(encrypted_sym_key);

        dataout.writeInt(encrypted_sym_param.length);
        dataout.write(encrypted_sym_param);

        return new CipherOutputStream(out, sym_cipher);
    }

    public static CipherInputStream encryptedInputStream(InputStream in,
            ASymmetricKey asym_key) throws IOException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        DataInputStream datain = new DataInputStream(in);

        byte[] encrypted_sym_key = new byte[datain.readInt()];
        readAll(datain, encrypted_sym_key);

        byte[] encrypted_sym_param = new byte[datain.readInt()];
        readAll(datain, encrypted_sym_param);

        Cipher asym_cipher = ASymmetric.createDecrypter(asym_key);

        byte[] sym_key = asym_cipher.doFinal(encrypted_sym_key);
        byte[] sym_param = asym_cipher.doFinal(encrypted_sym_param);

        Cipher sym_cipher = Symmetric.createDecrypter(sym_key, sym_param);

        return new CipherInputStream(in, sym_cipher);
    }

    public static CipherOutputStream encryptedOutputStream(OutputStream out,
            byte[] sym_key, byte[] params) throws IOException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher sym_cipher = Symmetric.createEncrypter(sym_key, params);
        return new CipherOutputStream(out, sym_cipher);
    }

    public static CipherInputStream encryptedInputStream(InputStream in,
            byte[] sym_key, byte[] params) throws IOException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher sym_cipher = Symmetric.createDecrypter(sym_key, params);
        return new CipherInputStream(in, sym_cipher);
    }

    private static void readAll(InputStream in, byte[] buffer)
            throws IOException {
        int off = 0;
        while (off < buffer.length) {
            int ret = in.read(buffer, off, buffer.length - off);
            if (ret > 0) {
                off += ret;
            } else {
                throw new EOFException();
            }
        }
    }
}

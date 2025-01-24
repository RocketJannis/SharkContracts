package net.sharksystem.contracts;

import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.crypto.ASAPKeyStore;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class ASAPCryptoUtilsExtension {

    /**
     * Uses asymmetric encryption to encrypt the unencryptedBytes using the public key of the recipient.
     * @param unencryptedBytes bytes to encrypt
     * @param recipient identifier of the recipient
     * @param ASAPKeyStore key store that holds the public key of the recipient
     * @return encrypted bytes
     * @throws ASAPSecurityException if the public key is missing or other problems occurred during encryption
     */
    public static byte[] encryptAsymmetric(byte[] unencryptedBytes, CharSequence recipient, ASAPKeyStore ASAPKeyStore) throws ASAPSecurityException {
        PublicKey publicKey = ASAPKeyStore.getPublicKey(recipient);
        if (publicKey == null) {
            throw new ASAPSecurityException("recipients' public key cannot be found");
        } else {
            try {
                Cipher cipher = Cipher.getInstance(ASAPKeyStore.getAsymmetricEncryptionAlgorithm());
                cipher.init(1, publicKey);
                return cipher.doFinal(unencryptedBytes);
            } catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                     NoSuchAlgorithmException e) {
                throw new ASAPSecurityException("problems when encrypting", e);
            }
        }
    }

}

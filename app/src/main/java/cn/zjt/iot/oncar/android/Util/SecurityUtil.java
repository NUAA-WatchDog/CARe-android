package cn.zjt.iot.oncar.android.Util;

import org.apaches.commons.codec.DecoderException;
import org.apaches.commons.codec.binary.Hex;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Mr Dk.
 * @version 2018.5.26
 * @see cn.zjt.iot.oncar.android.Thread.NetThread
 * @since 2018.5.26
 */

public class SecurityUtil {

    /**
     * @Variables
     */
    private static Cipher cipher;
    private static SecretKey generateKey;
    private static byte[] KEY = {-49, -122, -91, 45, -17, 69, -88, -126, -47, -24, 37, 46, -42, 97, 27, -44};
    private static String Algorithm = "AES";

    /**
     * @Methods
     */

    /*
     * @Method InitKey
     * @Return void
     * @Function Initialize the key
     */
    private static void InitKey() {
        generateKey = new SecretKeySpec(KEY, "AES");
    }

    /*
     * @Method Encode
     * @Param String src
     * @Return String
     * @Function Encoding
     */
    public static String Encode(String src) {

        try {
            if (generateKey == null) {
                InitKey();
            }
            if (cipher == null) {
                cipher = Cipher.getInstance(Algorithm);
            }

            cipher.init(Cipher.ENCRYPT_MODE, generateKey);
            byte[] resultBytes = cipher.doFinal(src.getBytes());

            return Hex.encodeHexString(resultBytes);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
     * @Method Decode
     * @Param String secret
     * @Return String
     * @Function Decoding
     */
    public static String Decode(String secret) {

        try {
            if (generateKey == null) {
                InitKey();
            }
            if (cipher == null) {
                cipher = Cipher.getInstance(Algorithm);
            }

            cipher.init(Cipher.DECRYPT_MODE, generateKey);
            byte[] result = Hex.decodeHex(secret.toCharArray());

            return new String(cipher.doFinal(result));

        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DecoderException e) {
            e.printStackTrace();
        }

        return null;
    }
}

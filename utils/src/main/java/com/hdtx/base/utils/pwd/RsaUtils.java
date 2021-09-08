package com.hdtx.base.utils.pwd;


import com.hdtx.base.exception.EncryptException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaUtils {
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 1024;
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    public static String sign(String content, String privateKey) throws EncryptException {
        byte[] decode = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(decode);

        try {
            KeyFactory keyf = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));

            byte[] signed = signature.sign();
            return Base64.getEncoder().encodeToString(signed);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException e) {
            throw new EncryptException(e);
        }
    }

    /**
     * 校验签名
     *
     * @param content   内容
     * @param sign      签名
     * @param publicKey 公钥
     */
    public static boolean check(String content, String sign, String publicKey) throws EncryptException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            byte[] encodedKey = Base64.getDecoder().decode(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | SignatureException | InvalidKeyException e) {
            throw new EncryptException(e);
        }
    }


    public static String decrypt(byte[] data, String privateKey) throws EncryptException {
        try {
            // 对密钥解密
            byte[] keyBytes = Base64.getDecoder().decode(privateKey);
            // 取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key key = keyFactory.generatePrivate(pkcs8KeySpec);
            // 对数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] bytes = cipherDoFinal(cipher, data, KEY_SIZE / 8);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (NoSuchPaddingException | IOException | BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException e) {
            throw new EncryptException(e);
        }
    }


    /**
     * 解密<br>
     * 用私钥解密
     */
    public static String decrypt(String data, String privateKey)
            throws EncryptException {
        byte[] decode = Base64.getDecoder().decode(data);
        return decrypt(decode, privateKey);
    }


    /**
     * 加密<br>
     */
    public static String encrypt(String data, String publicKey)
            throws EncryptException {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        return encrypt(bytes, publicKey);
    }

    /**
     * @param data 待加密文本
     */
    public static String encrypt(byte[] data, String publicKey)
            throws EncryptException {
        // 对密钥解密
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        // 取得私钥
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key key = keyFactory.generatePublic(keySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] bytes = cipherDoFinal(cipher, data, KEY_SIZE / 8 - 11);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException | IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | InvalidKeySpecException e) {
            throw new EncryptException(e);
        }
    }

    private static byte[] cipherDoFinal(Cipher cipher, byte[] srcBytes,
                                        int segmentSize) throws IOException, BadPaddingException, IllegalBlockSizeException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int inputLen = srcBytes.length;
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > segmentSize) {
                cache = cipher.doFinal(srcBytes, offSet, segmentSize);
            } else {
                cache = cipher.doFinal(srcBytes, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * segmentSize;
        }
        byte[] data = out.toByteArray();
        out.close();
        return data;
    }


    /**
     * 初始化秘钥对
     */
    public static RsaKeyPair initKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        RsaKeyPair rsaKeyPair = new RsaKeyPair();
        String encodePublic = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String encodePrivate = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        rsaKeyPair.setPublicKey(encodePublic);
        rsaKeyPair.setPrivateKey(encodePrivate);
        return rsaKeyPair;
    }

    public static class RsaKeyPair {
        private String publicKey;
        private String privateKey;

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }
    }

}

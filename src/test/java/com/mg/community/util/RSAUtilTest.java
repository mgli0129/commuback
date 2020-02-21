package com.mg.community.util;


import java.security.KeyPair;

import static com.mg.community.util.RSAUtil.decrypt;
import static com.mg.community.util.RSAUtil.getPrivateKey;

/**
 * @ClassName RSAUtilTest
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/20 21:44
 * @Version 1.0
 */
public class RSAUtilTest {

    public static void main(String[] args) {
        RSAUtil rsaUtil = new RSAUtil();

        try {
            // 生成密钥对
            KeyPair keyPair = rsaUtil.getKeyPair();
            String privateKey = new String(Base64Util.encoder(keyPair.getPrivate().getEncoded()));
            String publicKey = new String(Base64Util.encoder(keyPair.getPublic().getEncoded()));
            System.out.println("私钥:" + privateKey);
            System.out.println("公钥:" + publicKey);
            // RSA加密
            String data = "123";
            System.out.println("加密前内容:" + data);
            String encryptData = rsaUtil.encrypt(data, rsaUtil.getPublicKey(publicKey));
            System.out.println("加密后内容:" + encryptData);
            System.out.println("加密后内容长度:" + encryptData.length());
            // RSA解密
//            String pbkey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChShUAemSbEEnC94WiU1bRrW5uGQrKcvH1xbsikOG/7UnRhU0sbeEu6B3j1HfvOGxvRGE79ME+2+Zqsx1AdyzMddSfm3ATWj9/LnO2rmPmG7irYsU4h6Wtj5M73yyBNaYpZnWs4MYJcI0JKfjyvuAIlSRnEgarNFCms9s8hoycdQIDAQAB";
//            String pvkey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJVVtygEgLQbIb2vhB9ohggNREOP7/FAsYI46tBEu2icJPxrx0XORWtIcL6PStp7hceydwC4ckPW+FyCU8xq421C+g6MGPt4b/EizLKvGDCC71BXcPugqOTN2oSGNdxPYV6q7LZR+LwU0ORLX3WkxgwyFVfb5w5x5P7SnSa+u1F/AgMBAAECgYAitB6yQAmg0UVQDX/IxMORD3QztzlteTlHJ+75o19h+hdmSa2vfTYIrnb08dvVVitKsyCQnEyBk214IhlTQevD14vlTU+nvEUxKbVMbVh1rIDgJe4MJzsiKPD9z5TLN9srz1XwblHCgZ6rsP+75MzS/bIqWcj7fTJJurylB5Z4YQJBAMg2e+7zBtvDZRnit5m2i7QrIIh8g+95b7B3kk27wuqS5NIs50QQ/ZCmxfZrV1xQpDlbf3lFfHSxw92D4AtqMHcCQQC+8gSxTYot2pzt8dhYbyQaMhKUubxaF+9qUYT8j0xyyPffG3R7w+TIqG6vIFgp/R/8rGW3MPZvrDSWHzr/XnE5AkAyjz/A2fJzcOaJIO3IjYa+Gt3+WaNfyETiRW/W3YGdhzbttJU6ZSgDbXo0fmrzTxIJwgdw8pE5TuLtf64Gc+yFAkBbmt43dXyyLOwWl2z0WDWYv6b8ZPMAwfThK8TCKblCZDnOdmx2p+9NeMjJCZpiYoaDAO43aa0AvDvqPYdfVh65AkA5qc9rfe6T7QgC0mFU0HFNq7hJNICTosMvHN8WuRx6d5dcKyCxWbbw4Pi39fg/hQu+byTYh2Yv/csZCJ2U9Slv";
//            String encryptData = "O0z8tj9GR0frs6Iw2DHPv0MKzN7H44cSbhMs13NmPZk2HYFb0XC5L4PN8SzIsKUwTeuYE743h2k9hnTFOqLMZNyEfKdlOnq0RpKTPFeOM4cbstF9vD+c9zkJ71lnOuW9+ebgZ7F1y6zwxZQC83mnC/kdGw/kSY8YGN1xEwOcDCA=";
//            String decryptData = decrypt(encryptData, getPrivateKey(privateKey));
            String decryptData = rsaUtil.decrypt(encryptData, getPrivateKey(privateKey));
            System.out.println("解密后内容:" + decryptData);
            // RSA签名
            String sign = rsaUtil.sign(data, getPrivateKey(privateKey));
            // RSA验签
            boolean result = rsaUtil.verify(data, rsaUtil.getPublicKey(publicKey), sign);
            System.out.print("验签结果:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("加解密异常");
        }

    }
}

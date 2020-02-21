package com.mg.community.util;

import java.util.Base64;

/**
 * @ClassName Base64Util
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/20 21:05
 * @Version 1.0
 */
public class Base64Util {
    public static final Base64.Encoder encoder = Base64.getEncoder();
    public static final Base64.Decoder decoder = Base64.getDecoder();

    /**
     * base64加密
     *
     * @param encodeText 明文
     * @return
     */
    public static byte[] encoder(byte[] encodeText) {
        return encoder.encode(encodeText);
    }

    /**
     * base64加密
     *
     * @param decodeText 密文
     */
    public static byte[] decoder(byte[] decodeText) {
        return decoder.decode(decodeText);
    }

}

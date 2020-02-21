package com.mg.community.util;

/**
 * @ClassName RSAConst
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2020/2/20 21:11
 * @Version 1.0
 */
public interface RSAConst {

    /**
     * 加密算法RSA
     */
    public final static String ALGORITHM_NAME = "RSA";
    /**
     * 签名算法
     */
    public final static String MD5_RSA = "MD5withRSA";

    /**
     * RSA最大加密明文大小
     */
    public final static int MAX_ENCRYPT_BLOCK = 117;
    /**
     * RSA最大解密密文大小
     */
    public final static int MAX_DECRYPT_BLOCK = 128;
    /**
     * RSA 位数 如果采用2048 上面最大加密和最大解密则须填写:  245 256
     */
    public final static int INITIALIZE_LENGTH = 1024;

}

package com.mg.community.exception;

/**
 * @ClassName CommonErrorCode
 * @Description 公共的错误信息码清单：
 *              本模块包括的错误信息码类别：
 *              1）系统登录；
 *              2）通用的系统错误；
 * @Author MGLi
 * @Date 2020/1/21 21:23
 * @Version 1.0
 */
public enum CommonErrorCode implements ICustomizeErrorCode{

    SUCCESS("000000", "处理成功"),
    GENERAL_ERROR("100000", "服务冒烟了，要不您稍后再试试"),

    LOGIN_INVALID_USER_PASSWORD("100001", "用户名或密码验证不通过"),
    LOGIN_GITHUB_ACCOUNT_VERIFIED_FAILURE("100002", "GITHUB账户验证失败，请重新验证"),
    LOGIN_GITHUB_ACCOUNT_NOT_EXIST("100003", "无此GITHUB账户"),

    TOKEN_NULL("100004","用户未登录，请重新登录"),
    TOKEN_INVALID("100005","用户验证不通过，请重新登录"),
    TOKEN_USER_NOT_FOUND("100006","用户不存在，请重新登录"),
    TOKEN_HAS_EXPIRED("100007","用户验证信息已过期，请重新登录"),
    TOKEN_SESSION_HAS_EXPIRED("100008","用户验证信息已过期，请重新登录"),
    SYSTEM_GET_ENV_ERROR("100009", "获取环境信息异常"),
    SYSTEM_RSA_ERROR("100010", "用户验证密钥不存在"),
    USER_NAME_EXISTED("1000011", "该用户名已被注册"),
    USER_PHONE_EXISTED("1000012", "该手机号已被注册"),
    USER_CREATE_FAIL("1000013", "注册用户失败"),
    RSA_ENCRYPT_DEENCRYPT_ERROR("1000014", "验密系统错误"),
    REDIS_OPERATION_ERROR("1000015", "Redis操作失败"),
    TOKEN_GENERATED_FAIL("1000016", "Token生成失败，请重试"),

    ;

    private String code;
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return code;
    }

    CommonErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

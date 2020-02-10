package com.mg.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{

    SUCCESS("0000", "处理成功"),
    GENERAL_ERROR("2000", "服务冒烟了，要不您稍后再试试"),
    QUESTION_NOT_FOUND("2001","您找的问题不在了，要不要换个试试"),
    COMMENT_NOT_FOUND("2002","当前评论不存在，要不要换个试"),
    NO_LOGIN("2003","当前操作需要登录，请登录后重试"),
    TARGET_PARAM_NOT_FOUND("2004","未选中任何问题或评论进行回复"),
    TYPE_PARAM_NOT_FOUND("2005","回复的类型不存在"),
    CONTENT_IS_EMPTY("2006","评论的内容不能为空"),
    FILE_UPLOAD_FAILURE("2007", "文件上传失败"),
    INVALID_USER_PASSWORD("2008", "用户名或密码验证不通过"),
    GITHUB_ACCOUNT_VERIFIED_FAILURE("2101", "GITHUB账户验证失败，请重新验证"),
    GITHUB_ACCOUNT_NOT_EXIST("2102", "无此GITHUB账户"),
    PUBLISH_TITLE_EMPTY("2501", "发布的标题不能为空"),
    PUBLISH_CONTENT_EMPTY("2502", "发布的问题补充不能为空"),
    PUBLISH_TAG_INVALID("2503", "发布的标签不合法"),
    GET_ENVIRONMENT_ERROR("3001", "获取环境信息异常"),


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

    CustomizeErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

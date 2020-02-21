package com.mg.community.exception;

/**
 * @ClassName CommunityErrorCode
 * @Description 业务的错误信息码清单：
 *              本模块包括的错误信息码均与业务相关；
 *              类型有两种：
 *              1）不带自定义信息的错误信息；
 *              2）带自定义信息的错误信息；
 *                 .标记位置：&1 &2 ...
 *                 .throw 异常时,带入自定义信息；
 *                 .错误返回时，使用自定义信息替换&1 &2 ...
 * @Author MGLi
 * @Date 2020/1/21 21:23
 * @Version 1.0
 */
public enum CommunityErrorCode implements ICustomizeErrorCode{

    QUESTION_NOT_FOUND("200001","您找的问题不在了，要不要换个试试"),
    COMMENT_NOT_FOUND("200002","当前评论不存在，要不要换个试"),
    TARGET_PARAM_NOT_FOUND("200003","未选中任何问题或评论进行回复"),
    TYPE_PARAM_NOT_FOUND("200004","回复的类型不存在"),
    CONTENT_IS_EMPTY("200005","评论的内容不能为空"),
    FILE_UPLOAD_FAILURE("200006", "文件上传失败"),
    PUBLISH_TITLE_EMPTY("200007", "发布的标题不能为空"),
    PUBLISH_CONTENT_EMPTY("200008", "发布的问题补充不能为空"),
    PUBLISH_TAG_INVALID("200009", "发布的标签不合法---&1"),

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

    CommunityErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

package com.mg.community.dto;

import lombok.Data;

/**
 * @ClassName FileUploadDTO
 * @Description 对我描述吧
 * @Author MGLi
 * @Date 2019/12/18 11:25
 * @Version 1.0
 */
@Data
public class FileUploadDTO {
    private int success;
    private String message;
    private String url;
}

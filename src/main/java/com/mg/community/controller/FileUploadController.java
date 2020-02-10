package com.mg.community.controller;

import com.mg.community.dto.FileUploadDTO;
import com.mg.community.dto.ResultDTO;
import com.mg.community.exception.CustomizeErrorCode;
import com.mg.community.util.EnvInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @ClassName FileUploadController
 * @Description File upload through markdown editor.
 * 1. Markdown editor 前端的原始代码会有一些异常，可通过调试后修改，具体的代码见：
 * [image-dialog.js]:
 * // var json = (body.innerText) ? body.innerText : ( (body.textContent) ? body.textContent : null);
 * var jsonContainer = body.getElementsByTagName("pre")[0];
 * var json = (jsonContainer.innerText) ? jsonContainer.innerText : ( (jsonContainer.textContent) ? jsonContainer.textContent : null);
 * // --- end
 * // Edited by MG 20191218  --- begin
 * dialog.find("[data-link]").val(json.url);
 * // --- end
 * 2. Markdown editor会通过js调用后台的服务；
 * 3. 后台服务里，可以通过MultipartHttpServletRequest强转HttpServletRequest对象，再由MultipartHttpServletRequest对象获取上传的文件，返回MultipartFile类型；
 * 4. 后续的处理为正常的文件流方式；
 * 或者：
 * 3. 直接使用MultipartFile 接收上传的文件；
 * 4. 后续使用transferTo上传文件；
 * @Author MGLi
 * @Date 2019/12/18 10:45
 * @Version 1.0
 */

@RestController
@Slf4j
public class FileUploadController {

    @Value("${file.uploadPath}")
    private String uploadPath;

    @Value("${file.uploadRoot}")
    private String uploadRoot;

    @Autowired
    private EnvInfo envInfo;

    @RequestMapping("/upload")
    public FileUploadDTO upload(@RequestParam(value = "editormd-image-file", required = false) MultipartFile file, HttpServletRequest request) {

        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        String originalFilename = file.getOriginalFilename();
        log.info("file name is {}" + originalFilename);

        //修改文件名
        String fileName = UUID.randomUUID().toString() + "." + originalFilename.split("\\.")[1];

        File uploadFile = new File(uploadRoot +uploadPath,fileName);

        try {
            file.transferTo(uploadFile);
        } catch (IOException e) {
            //上传失败
            log.debug("File upload failure: {}" + ResultDTO.errorOf(CustomizeErrorCode.FILE_UPLOAD_FAILURE));
            fileUploadDTO.setSuccess(0);
            fileUploadDTO.setMessage(ResultDTO.errorOf(CustomizeErrorCode.FILE_UPLOAD_FAILURE).getMessage());
            fileUploadDTO.setUrl("");
            return fileUploadDTO;
        }

        //上传成功
        //组装markdown editor所需要的返回信息
        String rtnPath = envInfo.getUrl()+ uploadPath + "/" + fileName;
        fileUploadDTO.setSuccess(1);
        fileUploadDTO.setMessage("File has already uploaded successfully!");
        fileUploadDTO.setUrl(rtnPath);
        log.info("uploading file: {}" + fileUploadDTO);

        return fileUploadDTO;
    }
}

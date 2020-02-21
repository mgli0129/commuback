package com.mg.community.controller;

import com.mg.community.annotation.UserLoginToken;
import com.mg.community.dto.ResultDTO;
import com.mg.community.exception.CommunityErrorCode;
import com.mg.community.util.EnvInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

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

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@Slf4j
public class FileUploadController {

    @Value("${file.uploadPath}")
    private String uploadPath;

    @Value("${file.uploadRoot}")
    private String uploadRoot;

    @Autowired
    private EnvInfo envInfo;

    @UserLoginToken
    @RequestMapping(value = "/api/upload", method = RequestMethod.POST)
    public Object upload(@RequestParam(value = "files", required = false) MultipartFile[] files, HttpServletRequest request) {

        //输出格式测试
        Map<String, Object> outUni = new HashMap<String, Object>();
        List<String> urlList = new ArrayList();

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            log.info("file name is {}" + originalFilename);

            //修改文件名
            String fileName = UUID.randomUUID().toString() + "." + originalFilename.split("\\.")[1];
            File uploadFile = new File(uploadRoot + uploadPath, fileName);

            try {
                file.transferTo(uploadFile);
            } catch (IOException e) {
                //上传失败
                log.debug("File upload failure: {}" + ResultDTO.errorOf(CommunityErrorCode.FILE_UPLOAD_FAILURE));
            }
            //上传成功
            //组装markdown editor所需要的返回信息
            String rtnPath = envInfo.getUrl() + uploadPath + "/" + fileName;
            urlList.add(rtnPath);
        }

        if (urlList.size() < 1) {
            return ResultDTO.errorOf(CommunityErrorCode.FILE_UPLOAD_FAILURE);
        }

        outUni.put("urlList", urlList);
        log.info("files uploaded successfully" + urlList);
        return ResultDTO.okOf(outUni);
    }
}

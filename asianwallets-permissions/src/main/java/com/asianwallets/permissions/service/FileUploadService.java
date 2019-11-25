package com.asianwallets.permissions.service;
import org.springframework.web.multipart.MultipartFile;

/**
 *文件上传服务
 */
public interface FileUploadService {
    /**
     * 图片上传
     * @param input
     * @return
     */
    String uploadImage(MultipartFile input);
}

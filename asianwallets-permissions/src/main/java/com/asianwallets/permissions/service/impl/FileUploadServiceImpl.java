package com.asianwallets.permissions.service.impl;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateUtil;
import com.asianwallets.common.utils.FileUtil;
import com.asianwallets.permissions.dto.FileInfo;
import com.asianwallets.permissions.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;


/**
 * 文件上传服务
 */
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    public static final String IMAGES_DIR = "/imagesaas/";

    @Value("${file.http.server}")//已经不用了
    private String fileHttpServer;//图片上传返回的url比如：http://img.asianwallets.com/images/

    @Value("${file.upload.path}")
    private String fileUploadPath;//图片上传到服务器的目录：/data/file/upload/

    /**
     * 图片上传
     * @param input
     * @return
     */
    @Override
    public String uploadImage(MultipartFile input) {
        String imagePath = fileUploadPath.concat(IMAGES_DIR).concat(DateUtil.getCurrentDate());
        return this.uploadProcess(input, imagePath);
    }

    /**
     * 上传处理(默认校验登陆状态)
     * @param input
     * @param path
     * @return
     */
    private String uploadProcess(MultipartFile input, String path) {
        try {
            InputStream in = input.getInputStream();
            return this.upload(input.getOriginalFilename(), path,in).getPath();
        }catch (Exception e){
            log.error("uploadProcess 转换成文件流发送异常：",e.getMessage());
            throw new BusinessException(EResultEnum.UPLOAD_FILE_ERROR.getCode());
        }
    }

    public FileInfo upload(String fileName, String fileDir, InputStream in) {
        FileOutputStream out = null;
        FileInfo fileInfo = null;
        try {
            String newFileName = UUID.randomUUID().toString().replaceAll("-", "").concat(fileName.substring(fileName.lastIndexOf(".")));
            String savePath = fileDir.concat("/").concat(newFileName);
            createDir(fileDir);
            out = new FileOutputStream(savePath);
            int n = -1;
            byte[] b = new byte[10240];
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            out.close();
            out.flush();
            in.close();

            fileInfo = new FileInfo();
            fileInfo.setOldName(fileName);
            fileInfo.setNewName(newFileName);
            fileInfo.setPath(savePath.substring(savePath.indexOf(fileUploadPath) + fileUploadPath.length()));

            log.info("upload.fileInfo:{}", fileInfo);
            return fileInfo;
        } catch (Exception e) {
            FileUtil.close(out);
            FileUtil.close(in);
            log.error("upload.error:{}",e);
        }
        return null;
    }

    private void createDir(String path) {
        File fileDir = new File(path);
        if (!fileDir.exists() && !fileDir.isDirectory()) {
            fileDir.mkdirs();
        }
    }
}

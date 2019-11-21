package com.asianwallets.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

/**
 * @author Wu, Hua-Zheng
 * @version v1.0.0
 * @classDesc: 功能描述: 图片工具类
 * @createTime 2018年7月5日 上午10:44:05
 * @copyright: 上海众哈网络技术有限公司
 */
@Slf4j
public class TCFileUtils {

    /**
     * @param multipartFile
     * @methodDesc: 功能描述: 保存文件，直接以multipartFile形式
     * @author Wu, Hua-Zheng
     * @createTime 2018年7月5日 上午10:49:17
     * @version v1.0.0
     */
    public static String saveFile(MultipartFile multipartFile, String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
        String originalFilename = multipartFile.getOriginalFilename();
        String fileName = UUID.randomUUID().toString().replaceAll("-", "")
                + TCFileUtils.getFileSuffixes(originalFilename);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path + File.separator + fileName));
        byte[] bs = new byte[1024];
        int len;
        while ((len = fileInputStream.read(bs)) != -1) {
            bos.write(bs, 0, len);
        }
        bos.flush();
        bos.close();
        return fileName;
    }

    /**
     * @param fileName         需要下载文件路径
     * @param downloadfilePath 文件保存路径
     * @methodDesc: 功能描述: 文件下载
     * @author Wu, Hua-Zheng
     * @createTime 2018/7/19 下午4:42
     * @version v1.0.0
     */
    public static String downloadFile(String fileName, String downloadfilePath) {

        // 从网络上下载一张图片
        InputStream inputStream = null;
        OutputStream outputStream = null;
        // 建立一个网络链接
        URLConnection con = null;
        URL url = null;
        String filePath = null;
        try {
            // 构造URL
            url = new URL(fileName);
            // 打开连接
            con = url.openConnection();
            // 输入流
            inputStream = con.getInputStream();
            if (null == inputStream) {
                return null;
            }
            // 输出的文件流
            File downloadFile = new File(downloadfilePath);

            if (!downloadFile.exists()) {
                downloadFile.mkdirs();
            }
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            filePath = downloadFile.getPath() + File.separator + fileName;
            outputStream = new FileOutputStream(new File(filePath));

            int n = -1;
            byte b[] = new byte[1024];
            while ((n = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, n);
            }
            outputStream.flush();
        } catch (Exception e) {
            log.error("[TCFileUtils-downloadFile]-Exception异常{}", e);
        } finally {
            try {
                //inputStream不能为空的时候判断
                inputStream.close();
            } catch (IOException e) {
                log.error("[TCFileUtils-downloadFile-IOException异常]-{}", e);
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("[TCFileUtils-downloadFile-IOException异常]-{}", e);
            }
        }

        return filePath;
    }

    /**
     * @classDesc: 功能描述: 创建文件
     * @author Wu, Hua-Zheng
     * @createTime 2018/7/20 下午6:37
     * @version v1.0.0
     * @copyright: 上海投嶒网络技术有限公司
     */
    public static boolean createFile(String filePath, String fileName) {
        File file = new File(filePath + fileName);
        File parentFile = new File(filePath);

        try {
            // 如果文件不存在，则创建新的文件
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();
            return true;
        } catch (Exception e) {
            log.error("[TCFileUtils-createFile异常]-{}", e);
        }

        return false;
    }

    /**
     * 创建单个文件夹
     *
     * @param dir
     * @param ignoreIfExitst true 表示如果文件夹存在就不再创建了-false是重新创建
     * @throws IOException
     */
    public static void createDir(String dir, boolean ignoreIfExitst) throws IOException {

        File file = new File(dir);
        if (ignoreIfExitst && file.exists()) {
            return;
        }
        if (!file.mkdir()) {
            throw new IOException("Cannot create the directory = " + dir);
        }
    }

    /**
     * 创建多个文件夹
     *
     * @param dir
     * @param ignoreIfExitst
     * @throws IOException
     */
    public static void createDirs(String dir, boolean ignoreIfExitst) throws IOException {

        File file = new File(dir);
        if (ignoreIfExitst && file.exists()) {
            return;
        }
        if (!file.mkdirs()) {
            throw new IOException("Cannot create directories = " + dir);
        }
    }

    /**
     * @classDesc: 功能描述: 向文件中写入内容
     * @author Wu, Hua-Zheng
     * @createTime 2018/7/20 下午6:34
     * @version v1.0.0
     * @copyright: 上海众哈网络技术有限公司
     */
    public static boolean writeFileContent(String filepath, String filecontent) {

        String filein = filecontent + "\r\n";// 新写入的行，换行
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos = null;
        PrintWriter pw = null;
        String temp = "";
        try {
            File file = new File(filepath);// 文件路径(包括文件名称)
            // 将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();

            // 文件原有内容
            for (; (temp = br.readLine()) != null; ) {
                buffer.append(temp);
                // 行与行之间的分隔符 相当于“\n”
                buffer = buffer.append(System.getProperty("line.separator"));
            }

            buffer.append(filein);
            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
            return true;
        } catch (Exception e) {
            log.error("[TCFileUtils-writeFileContent异常]-{}", e);
        } finally {

            try {
                // 不要忘记关闭
                if (pw != null) {
                    pw.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                log.error("[TCFileUtils-writeFileContent异常]-{}", e);
            }

        }
        return false;
    }

    /**
     * @classDesc: 功能描述: 删除文件
     * @author Wu, Hua-Zheng
     * @createTime 2018/7/20 下午6:29
     * @version v1.0.0
     * @copyright: 上海众哈网络技术有限公司
     */
    public static boolean delFile(String filePath, String fileName) {
        File file = new File(filePath + fileName);
        try {
            if (file.exists()) {
                file.delete();
                return true;
            }
        } catch (Exception e) {
            log.error("[TCFileUtils-delFile异常]-{}", e);
        }
        return false;
    }

    /**
     * 获取文件后缀
     *
     * @param fileName
     * @return
     */
    public static String getFileSuffixes(String fileName) {
        int index = fileName.lastIndexOf(".");
        String suffixes = fileName.substring(index, fileName.length());
        return suffixes;
    }

    /**
     * 获取文件类型
     *
     * @param filePath
     * @return
     */
    public static String getFileType(String filePath) {
        String suffixes = getFileSuffixes(filePath);
        if (".bmp".equalsIgnoreCase(suffixes) || ".jpg".equalsIgnoreCase(suffixes) || ".gif".equalsIgnoreCase(suffixes)
                || ".png".equalsIgnoreCase(suffixes)) {
            return "image";
        } else if (".pdf".equalsIgnoreCase(suffixes)) {
            return "pdf";
        } else if (".doc".equalsIgnoreCase(suffixes) || ".docx".equalsIgnoreCase(suffixes)) {
            return "doc";
        } else if (".txt".equalsIgnoreCase(suffixes)) {
            return "txt";
        } else if (".xls".equalsIgnoreCase(suffixes) || ".xlsx".equalsIgnoreCase(suffixes)) {
            return "xls";
        }
        return "";
    }

//    public static void main(String[] args) {
//        String fileName = "http://patiencecats.com/ueditor/php/upload/image/20180312/1520829763554937.jpg";
//        String downloadfilePath = "/opt/code/file/wuhuazheng";
//        System.out.printf(TCFileUtils.downloadFile(fileName, downloadfilePath));
//    }
}
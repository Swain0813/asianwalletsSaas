package com.asianwallets.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class FileUtil {

    /**
     * 检查指定的文件名是否合法
     *
     * @param name 指定的文件名
     * @return boolean 如果指定的文件名合法则返回true，否则返回false
     */
    public static boolean checkFileName(String name) {
        boolean valid = true;
        if (name == null) {
            valid = false;
        } else if (name.length() == 0) {
            valid = false;
        } else if (name.length() == 1 && name.equals(".")) {
            valid = false;
        } else {
            char[] chars = {'*', '?', '<', '>', '|', '\"'};
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                for (int j = 0; j < chars.length; j++) {
                    if (c == chars[j]) {
                        valid = false;
                        break;
                    }
                }
                if (valid == false) {
                    break;
                }
            }
        }
        return valid;
    }

    // 关闭一个输出流
    public synchronized static void close(AutoCloseable... autoCloseable) {
        for (AutoCloseable closeable : autoCloseable) {
            if (closeable != null) {
                try {
                    closeable.close();
                    closeable = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // 关闭一个输出流
    public static void close(FileChannel channel) {
        if (channel != null) {
            try {
                channel.close();
                channel = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 关闭一个输入流
    public static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 关闭一个输出流
    public static void close(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
                outputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 关闭一个输入流
    public static void close(Reader... reader) {
        for (Reader r : reader) {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // 压缩
    public static String compress(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = null;
        GZIPOutputStream gzip = null;
        try {
            out = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
            gzip.close();
            return out.toString("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            log.error("压缩" + str + "出错：", e);
        } catch (IOException e) {
            log.error("压缩" + str + "出错：", e);
        } finally {
            close(gzip);
            close(out);
        }
        return str;
    }

    /**
     * 创建文件夹
     *
     * @param path String
     */
    public static void fileMkdir(String path) {
        File dirPath = new File(path);
        if (!dirPath.exists()) {
            dirPath.mkdir();
        }
    }

    public static Object fromBuffer(ByteBuffer buffer) throws IOException, ClassNotFoundException {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes, 0, bytes.length);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object obj = ois.readObject();
        ois.close();
        bais.close();
        return obj;
    }

    public static FileChannel getChannel(String pathName, boolean readOnly) {
        try {
            return new RandomAccessFile(FileUtil.getFile(pathName), readOnly ? "r" : "rw").getChannel();
        } catch (FileNotFoundException e) {
            log.error("文件" + pathName + "不存在", e);
            throw new IOError(e);
        }
    }

    // 获取指定的文件 如果不存在 自动创建
    public static File getFile(String pathName) {
        if (checkFileName(pathName)) {
            File path = new File(pathName.substring(0, Math.max(pathName.lastIndexOf("\\"), pathName.lastIndexOf("/"))));
            if (!path.isDirectory()) {// 如果目录不存在则创建目录
                path.mkdirs();
            }
            File file = new File(pathName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {

                    log.error("创建文件出错：" + path, e);
                }
            }
            return file;
        } else {
            log.error(pathName + "文件名有误");
        }
        return null;
    }

    public static MappedByteBuffer getFileBuffer(String pathName) {

        FileChannel channel = null;
        try {
            channel = getChannel(pathName, Boolean.TRUE);
            MappedByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
            return buffer;
        } catch (IOException e) {
            log.error("读取" + pathName + "文件出错", e);
            throw new IOError(e);
        } finally {
            FileUtil.close(channel);
        }

    }

    public static File[] getFiles(String path) {

        File file = new File(path);
        if (file.isDirectory()) {// 如果目录不存在则创建目录
            return file.listFiles();
        } else {
            log.error("目录不存在:" + path);
        }
        return null;
    }

    public static ArrayList<File> getDirectory(String path, ArrayList<File> objects) {
        File[] files = FileUtil.getFiles(path);
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                objects.add(file);
                getDirectory(file.getPath(), objects);
            }
        }
        return objects;
    }

    public static ImageIcon getIcon(String fileName) {

        return new ImageIcon(iconUrl(fileName));
    }

    public static Image getImg(String fileName) {
        return new ImageIcon(iconUrl(fileName)).getImage();
    }

    public static ByteBuffer getInputBuffer(int defSize, File File) {
        FileInputStream inputStream = null;
        FileChannel channel = null;
        ByteBuffer bf = null;
        try {
            bf = null;
            inputStream = new FileInputStream(File);
            channel = inputStream.getChannel();
            if (defSize < 1) {
                defSize = (int) channel.size();
            }
            bf = ByteBuffer.allocate(defSize);
            channel.read(bf);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                channel.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bf;
    }

    public static URL iconUrl(String fileName) {
        URL resource = FileUtil.class.getResource("/img/" + fileName);
        return resource;
    }

    public static Object read(File file, String pathName, Consumer<String> action) {

        try {
            MappedByteBuffer buffer = getFileBuffer(pathName);
            if (buffer.remaining() > 0) {
                Object fromBuffer = FileUtil.fromBuffer(buffer);
                return fromBuffer;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            log.error("文件" + pathName + "保存数据出错", e);
            throw new IOError(e);
        }
        return null;

    }

    public static void readLine(File file, String encoding, Consumer<? super String> action) {
        Objects.requireNonNull(action);
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, encoding);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                action.accept(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void read(String fileName, String encoding, Consumer<? super String> action) {
        Objects.requireNonNull(action);
        try {
            FileInputStream fis = new FileInputStream(getFile(fileName));
            InputStreamReader isr = new InputStreamReader(fis, encoding);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                action.accept(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 指定编码类型读文件
     *
     * @param fileName fileName
     * @param encoding encoding
     * @return String  String
     * @author Aspenn
     */
    public static String read(String fileName, String encoding) {
        StringBuffer fileContent = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(getFile(fileName));
            InputStreamReader isr = new InputStreamReader(fis, encoding);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                fileContent.append(line);
                fileContent.append(System.getProperty("line.separator"));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileContent.toString();
    }

    public static FileInputStream readInputStream(String filePath) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(getFile(filePath));
        } catch (FileNotFoundException e) {
            log.error("读取" + filePath + "出错", e);
        }
        return fileInputStream;

    }

    /**
     * 读取指定文件内的对象
     *
     * @param pathName 文件全路径
     * @return Object  Object
     */
    public static Object readObject(String pathName) {
        FileInputStream fis = null;
        ObjectInputStream inputStream = null;
        try {
            fis = new FileInputStream(FileUtil.getFile(pathName));
            if (fis.available() > 0) {

                inputStream = new ObjectInputStream(fis);

                return inputStream.readObject();
            }
        } catch (FileNotFoundException e) {
            log.error("读取文文件" + pathName + "出错1", e);
        } catch (ClassNotFoundException e) {
            FileUtil.getFile(pathName).delete();
            log.error("读取文文件" + pathName + "出错2", e);
        } catch (IOException e) {
            FileUtil.getFile(pathName).delete();
            log.error("读取文文件" + pathName + "出错3", e);
        } finally {
            close(inputStream);
            close(fis);
        }
        return null;
    }

    public static void save(Serializable obj, String pathName) {

        FileChannel channel = null;
        try {
            channel = getChannel(pathName, Boolean.FALSE);
            channel.write(FileUtil.toBuffer(obj));
        } catch (IOException e) {
            log.error("文件" + pathName + "保存数据出错", e);
            throw new IOError(e);
        } finally {
            FileUtil.close(channel);
        }
    }

    public static void saveObject(Object obj, String pathName) {
        FileOutputStream output = null;
        ObjectOutputStream outputStream = null;
        try {
            output = new FileOutputStream(getFile(pathName));
            outputStream = new ObjectOutputStream(output);
            outputStream.writeObject(obj);
            outputStream.flush();
            output.flush();
        } catch (FileNotFoundException e) {
            log.error("文件" + pathName + "不存在", e);
        } catch (IOException e) {
            log.error("文件" + pathName + "保存数据出错", e);
        } finally {
            close(outputStream);
            close(output);
        }
    }

    /**
     * 将指定的字符串保存到指定的文件中
     *
     * @param string 指定的字符串
     * @param file   指定的文件
     */
    public static void saveStringFile(String string, File file) {
        try {
            Writer writer = new FileWriter(file);
            writer.write(string);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ByteBuffer toBuffer(Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.flush();
        ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
        oos.close();
        baos.close();
        return buffer;
    }

    // 解压缩
    public static String uncompress(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = null;
        GZIPInputStream gunzip = null;
        try {
            out = new ByteArrayOutputStream();
            gunzip = new GZIPInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            // toString()使用平台默认编码，也可以显式的指定如toString("GBK")
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            log.error("压缩" + str + "出错：", e);
        } catch (IOException e) {
            log.error("压缩" + str + "出错：", e);
        } finally {
            close(gunzip);
            close(out);
        }
        return str;
    }

    // 指定编码类型写文件
    public static void write(String fileContent, String fileName, String encoding) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
            osw.write(fileContent);
            osw.flush();
            osw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FileOutputStream writeOutputStream(String filePath) {
        FileOutputStream fileInputStream = null;
        try {
            fileInputStream = new FileOutputStream(getFile(filePath));
        } catch (FileNotFoundException e) {
            log.error("写数据" + filePath + "出错", e);
        }
        return fileInputStream;

    }

}

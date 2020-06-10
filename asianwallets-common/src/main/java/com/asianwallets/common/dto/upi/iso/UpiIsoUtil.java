package com.asianwallets.common.dto.upi.iso;

import com.asianwallets.common.dto.th.ISO8583.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-10 14:45
 **/
@Slf4j
public class UpiIsoUtil {
    private UpiIsoUtil() {
    }

    ///**
    // * 128域组包
    // *
    // * @param iso8583DTO128 报文交互DTO，128域
    // *                      4位报文长度 + 4位消息类型 + 32位BITMAP + 报文信息
    // * @return
    // */
    //public static String packISO8583DTO(ISO8583DTO iso8583DTO128) throws IncorrectLengthException {
    //    StringBuilder sendMsg = new StringBuilder();
    //    // 先拼接消息类型
    //    sendMsg.append(iso8583DTO128.getMessageType());
    //    // 拼接BITMAP + 报文信息
    //    Object[] o = getBitMapAndMsg(iso8583DTO128, 64);
    //    sendMsg.append(o[0]);
    //    // 计算报文长度，长度占4个字节，不足4字节左补0
    //    int sendMsgLen = (int) o[1];
    //    String sendMsgLenStr = Integer.toString(sendMsgLen);
    //    sendMsgLenStr = NumberStringUtil.addLeftChar(sendMsgLenStr, 8, '0');
    //    sendMsgLenStr = NumberStringUtil.str2HexStr(sendMsgLenStr);
    //    System.out.println("报文长度 = " + sendMsgLenStr);
    //    // 将4位报文长度插到最前边
    //    sendMsg.insert(0, sendMsgLenStr);
    //
    //    return sendMsg.toString();
    //}

    /**
     * 128域组包
     *
     * @param iso8583DTO128 报文交互DTO，128域
     *                      4位报文长度 + 4位消息类型 + 32位BITMAP + 报文信息
     * @return
     */
    public static String packISO8583DTO(ISO8583DTO iso8583DTO128, String key) throws Exception {
        StringBuilder sendMsg = new StringBuilder();
        // 先拼接消息类型
        sendMsg.append(iso8583DTO128.getMessageType());
        // 拼接BITMAP + 报文信息
        Object[] o = getBitMapAndMsg(iso8583DTO128, 64, key);
        sendMsg.append(o[0]);
        log.info("==========【ISO8583Util】========== Mac Block :{}", sendMsg.toString());
        //计算MAC值
        if (!StringUtils.isEmpty(key)) {
            sendMsg.append(MACUtil.getCupEcbMac(key, sendMsg.toString()));
        }
        // 计算报文长度，长度占4个字节，不足4字节左补0
        int sendMsgLen = (int) o[1];
        //String sendMsgLenStr = Integer.toString(sendMsgLen);
        //sendMsgLenStr = NumberStringUtil.addLeftChar(sendMsgLenStr, 8, '0');
        //sendMsgLenStr = NumberStringUtil.str2HexStr(sendMsgLenStr);
        //log.info("==========【ISO8583Util】========== 报文长度 :{}", sendMsgLenStr);
        // 将4位报文长度插到最前边
        //sendMsg.insert(0, sendMsgLenStr);
        return sendMsg.toString();
    }

    /**
     * 字符串转化成为16进制字符串
     *
     * @param s
     * @return
     */
    public static String strTo16(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 128域解包
     *
     * @param receivedMsg 收到的报文消息
     *                    4位报文长度 + 4位消息类型 + 32位BITMAP + 报文信息
     * @return
     * @throws IncorrectMessageException
     */
    public static ISO8583DTO unpackISO8583DTO(String receivedMsg) throws IncorrectMessageException {

        if (null == receivedMsg) {
            throw new IncorrectMessageException("报文为空");
        }
        int totalLen = receivedMsg.length();
        if (totalLen < 40) {
            throw new IncorrectMessageException("报文格式不正确，报文长度最少为40");
        }

        int msgLen = Integer.valueOf(NumberStringUtil.hexStr2Str(receivedMsg.substring(148, 164)));
        //if(msgLen != totalLen - 4){
        //    throw new IncorrectMessageException("报文长度不匹配");
        //}
        String messageType = receivedMsg.substring(164, 168);
        String hexBitMap = receivedMsg.substring(168, 184);
        String binaryBitMap = NumberStringUtil.hexToBinaryString(hexBitMap);
        //String binaryBitMap = hexBitMap;
        String[] binaryBitMapArgs = binaryBitMap.split("");
        String msg = receivedMsg.substring(184);

        ISO8583DTO iso8583DTO128 = (ISO8583DTO) msgToObject(ISO8583DTO.class, binaryBitMapArgs, msg);
        iso8583DTO128.setMessageType(messageType);

        return iso8583DTO128;
    }

    /**
     * 获取ISO8583DTO类的属性，key为fldIndex，value为属性名
     *
     * @param clazz
     * @return
     */
    private static Map<Integer, String> getISO8583DTOFldMap(Class clazz) {
        Map<Integer, String> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            boolean fldHasAnnotation = field.isAnnotationPresent(ISO8583Annotation.class);
            if (fldHasAnnotation) {
                ISO8583Annotation fldAnnotation = field.getAnnotation(ISO8583Annotation.class);
                map.put(fldAnnotation.fldIndex(), field.getName());
            }
        }
        return map;
    }

    private static Object[] getBitMapAndMsg(Object iso8583DTO, int bitLen, String key) throws IncorrectLengthException {
        // 获取ISO8583DTO类的属性，key为fldIndex域序号，value为属性名
        Map<Integer, String> iso8583DTOFldMap = getISO8583DTOFldMap(iso8583DTO.getClass());
        // 初始化域位图
        StringBuffer bitMap = initBitMap(bitLen);

        // 获取有值的域，并生成位图
        PropertyDescriptor propertyDescriptor;
        String fldName;
        String fldValue;
        // 按照格式处理后的值
        String fldSendValue;
        int len = 0;
        // 将每个域对应的值，保存到对应下标中
        String[] fldSendValues = new String[bitLen];
        try {
            // 循环判断哪个字段有值
            for (Map.Entry<Integer, String> entry : iso8583DTOFldMap.entrySet()) {
                fldName = entry.getValue();
                propertyDescriptor = new PropertyDescriptor(fldName, iso8583DTO.getClass());
                fldValue = (String) propertyDescriptor.getReadMethod().invoke(iso8583DTO);

                if (StringUtils.isNotEmpty(fldValue)) {
                    // 如果此域有值，将对应的位图位置修改为1
                    bitMap = bitMap.replace(entry.getKey() - 1, entry.getKey(), "1");
                    // 根据注解对值进行处理
                    Field field = iso8583DTO.getClass().getDeclaredField(fldName);
                    Object[] o = verifyAndTransValue(field, fldValue);
                    fldSendValue = (String) o[0];
                    len += (int) o[1];
                    fldSendValues[entry.getKey() - 1] = fldSendValue;
                }
            }
        } catch (IncorrectLengthException e) {
            throw e;
        } catch (Exception e) {
            log.info("对象序列化报错");
            return null;
        }

        // 将128位2进制位图转换为32位16进制数据
        if (StringUtils.isNotEmpty(key)) {
            bitMap = bitMap.replace(63, 64, "1");
        }
        String bitMapHexStr = NumberStringUtil.binaryToHexString(bitMap.toString());
        //String bitMapHexStr = bitMap.toString();
        log.info("bitmap = " + bitMapHexStr);
        StringBuffer bitMapAndMsg = new StringBuffer();
        // 位图在前，先拼接位图
        bitMapAndMsg.append(bitMapHexStr);
        // 拼接报文数据
        for (String value : fldSendValues) {
            if (StringUtils.isNotEmpty(value)) {
                bitMapAndMsg.append(value);
            }
        }

        return new Object[]{bitMapAndMsg.toString().toUpperCase(), len};
    }

    private static Object msgToObject(Class clazz, String[] binaryBitMapArgs, String msg) {

        try {
            // 返回对象
            Object retObject = clazz.newInstance();

            // 获取ISO8583DTO类的属性，key为fldIndex域序号，value为属性名
            Map<Integer, String> iso8583DTOFldMap = getISO8583DTOFldMap(clazz);
            int indexFlag = 0;
            String fldName;
            Field field;
            ISO8583Annotation fldAnnotation;
            FldFlag fldFlag;
            int dataLength;
            String fldValue;
            String type;
            // 循环位图，第一位表示64域或者128域，所以从第二位开始
            for (int i = 1, len = binaryBitMapArgs.length; i < len; i++) {
                if (Objects.equals(binaryBitMapArgs[i], "0")) {
                    // 0表示没有值，跳过
                    continue;
                }

                // 位图下标从1开始，所以需要+1
                fldName = iso8583DTOFldMap.get(i + 1);
                field = clazz.getDeclaredField(fldName);
                fldAnnotation = field.getAnnotation(ISO8583Annotation.class);
                fldFlag = fldAnnotation.fldFlag();
                type = fldAnnotation.type();
                if (Objects.equals(FldFlag.FIXED, fldFlag)) {
                    dataLength = fldAnnotation.dataFldLength();
                } else if (Objects.equals(FldFlag.UNFIXED_2, fldFlag)) {
                    dataLength = Integer.valueOf(msg.substring(indexFlag, indexFlag = indexFlag + 2));
                } else if (Objects.equals(FldFlag.UNFIXED_3, fldFlag)) {
                    dataLength = Integer.valueOf(msg.substring(indexFlag, indexFlag = indexFlag + 4));
                } else {
                    //未知类型，不做处理
                    continue;
                }
                if (type.equals("ASC") || type.equals("BINARY")) {
                    dataLength = dataLength * 2;
                }
                fldValue = msg.substring(indexFlag, indexFlag + dataLength);
                if (dataLength % 2 != 0) {
                    dataLength = dataLength + 1;
                }
                indexFlag += dataLength;
                field.setAccessible(true);
                if (type.equals("ASC")
                        && fldAnnotation.fldIndex() != 35
                        && fldAnnotation.fldIndex() != 46
                        && fldAnnotation.fldIndex() != 47
                        && fldAnnotation.fldIndex() != 62) {
                    fldValue = NumberStringUtil.hexStr2Str(fldValue);
                }
                field.set(retObject, fldValue);
            }

            return retObject;
        } catch (Exception e) {
            log.info("对象反序列化报错");
            return null;
        }


    }

    /**
     * 组包时根据字段原值按照其配置规则转为十六进制 PACK
     *
     * @param field
     * @param fldValue 字段值
     * @throws Exception .
     */
    private static Object[] verifyAndTransValue(Field field, String fldValue)
            throws IncorrectLengthException {
        Object[] o = new Object[]{};
        boolean fldHasAnnotation = field.isAnnotationPresent(ISO8583Annotation.class);
        if (!fldHasAnnotation) {
            return o;
        }

        ISO8583Annotation iso8583Annotation = field.getAnnotation(ISO8583Annotation.class);
        FldFlag fldFlag = iso8583Annotation.fldFlag();
        int expectLen = iso8583Annotation.dataFldLength();
        String type = iso8583Annotation.type();
        int actualLen = fldValue.length();

        // 固定长度，则校验一下长度是否一致
        if (Objects.equals(FldFlag.FIXED, fldFlag)) {
            if (actualLen != expectLen) {
                String msg = String.format("%s长度不正确，期望长度为[%d]，实际长度为[%d]。"
                        , field.getName(), expectLen, actualLen);
                throw new IncorrectLengthException(msg);
            }
            if (type.equals("ASC")) {
                fldValue = NumberStringUtil.str2HexStr(fldValue);
            }
            if (actualLen % 2 != 0 && type.equals("BCD")) {
                fldValue = fldValue + "0";
                actualLen = actualLen + 1;
            }
            return new Object[]{fldValue, actualLen};
        }

        // 可变长度，校验一下长度是否超过上限。如果长度符合，则在前边拼接长度值
        if (Objects.equals(FldFlag.UNFIXED_2, fldFlag) || Objects.equals(FldFlag.UNFIXED_3, fldFlag)) {
            if ( iso8583Annotation.fldIndex() != 2
                    && iso8583Annotation.fldIndex() != 35
                    && actualLen > expectLen) {
                String msg = String.format("%s长度不正确，最大长度为[%d]，实际长度为[%d]。"
                        , field.getName(), expectLen, actualLen);
                throw new IncorrectLengthException(msg);
            }

            int len = 2;
            if (Objects.equals(FldFlag.UNFIXED_3, fldFlag)) {
                len = 3;
            }
            // 在报文前边拼接长度

            if (type.equals("ASC")
                    && iso8583Annotation.fldIndex() != 35
                    && iso8583Annotation.fldIndex() != 46
                    && iso8583Annotation.fldIndex() != 47
                    && iso8583Annotation.fldIndex() != 62) {
                fldValue = NumberStringUtil.str2HexStr(fldValue);
            }
            if (iso8583Annotation.fldIndex() == 35
                    || iso8583Annotation.fldIndex() == 2
                    || iso8583Annotation.fldIndex() == 46
                    || iso8583Annotation.fldIndex() == 47
                    || iso8583Annotation.fldIndex() == 62) {
                actualLen = actualLen / 2;
            }
            fldValue = NumberStringUtil.addLeftChar(String.valueOf(actualLen), (len - 1) * 2, '0') + fldValue;
            if (actualLen % 2 != 0 && type.equals("BCD")) {
                fldValue = fldValue + "0";
                actualLen = actualLen + 1;
            }
            return new Object[]{fldValue, actualLen};
        }
        return o;
    }

    /**
     * 初始64和128域位图。
     * 64域的全是0；128域的除第一位为1，其它位全为0
     *
     * @param d
     * @return
     */
    private static StringBuffer initBitMap(int d) {

        StringBuffer bf = new StringBuffer();

        if (d != 64 && d != 128) {
            return bf;
        }

        if (d == 64) {
            bf.append("0");
        } else {
            // 128域的第一位为"1"
            bf.append("1");
        }

        for (int i = 1; i < d; i++) {
            bf.append("0");
        }

        return bf;
    }

    /**
     * 向服务器 发送8583报文
     *
     * @param ip      主机地址IP
     * @param port    端口号
     * @param reqData 发送给服务器的报文
     * @return 返回的数据
     */
    public static Map<String, String> sendTCPRequest(String ip, String port, byte[] reqData) {
        Map<String, String> respMap = new HashMap<String, String>();
        OutputStream out = null;      //写
        InputStream in = null;        //读
        String localPort = null;      //本地绑定的端口(java socket, client, /127.0.0.1:50804 => /127.0.0.1:9901)
        String respData = null;       //响应报文
        String respDataHex = null;    //远程主机响应的原始字节的十六进制表示
        Socket socket = new Socket(); //客户机
        try {
            socket.setTcpNoDelay(true);
            socket.setReuseAddress(true);
            socket.setSoTimeout(30000);
            socket.setSoLinger(true, 5);
            socket.setSendBufferSize(1024);
            socket.setReceiveBufferSize(1024);
            socket.setKeepAlive(true);
            socket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), 30000);
            localPort = String.valueOf(socket.getLocalPort());
            /**
             * 发送TCP请求
             */
            out = socket.getOutputStream();
            out.write(reqData);
            /**
             * 接收TCP响应
             */
            in = socket.getInputStream();
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int len = -1;
            while ((len = in.read(buffer)) != -1) {
                bytesOut.write(buffer, 0, len);
            }
            /**
             * 解码TCP响应的完整报文
             */
            byte[] bytes = bytesOut.toByteArray();
            respData = NumberStringUtil.bcd2Str(bytes);
        } catch (Exception e) {
            System.out.println("与[" + ip + ":" + port + "]通信遇到异常,堆栈信息如下");
            e.printStackTrace();
        } finally {
            if (null != socket && socket.isConnected() && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("关闭客户机Socket时发生异常,堆栈信息如下");
                    e.printStackTrace();
                }
            }
        }
        respMap.put("localPort", localPort);
        respMap.put("reqData", new String(reqData));
        respMap.put("respData", respData);
        respMap.put("respDataHex", respDataHex);
        return respMap;
    }

    public static String byte2hex(byte[] b) // 二进制转字符串
    {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));

            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }
}

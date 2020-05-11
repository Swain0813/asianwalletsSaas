package com.asianwallets.common.dto.th.ISO8583;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:53
 **/


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ISO8583报文组包/解包工具类
 */
@Slf4j
public class ISO8583Util {

    private ISO8583Util() {
    }

    /**
     * 128域组包
     * @param iso8583DTO128   报文交互DTO，128域
     *                        4位报文长度 + 4位消息类型 + 32位BITMAP + 报文信息
     * @return
     */
    public static String packISO8583DTO(ISO8583DTO iso8583DTO128) throws IncorrectLengthException {
        StringBuilder sendMsg = new StringBuilder();
        // 先拼接消息类型
        sendMsg.append(iso8583DTO128.getMessageType());
        // 拼接BITMAP + 报文信息
        sendMsg.append(getBitMapAndMsg(iso8583DTO128, 64));
        // 计算报文长度，长度占4个字节，不足4字节左补0
        int sendMsgLen = sendMsg.length();
        String sendMsgLenStr = Integer.toString(sendMsgLen);
        sendMsgLenStr = NumberStringUtil.addLeftChar(sendMsgLenStr, 8, '0');
        System.out.println("报文长度 = "+ sendMsgLenStr);
        // 将4位报文长度插到最前边
        sendMsg.insert(0,sendMsgLenStr);

        return sendMsg.toString();
    }

    /**
     * 128域解包
     * @param receivedMsg 收到的报文消息
     *                    4位报文长度 + 4位消息类型 + 32位BITMAP + 报文信息
     * @return
     * @throws IncorrectMessageException
     */
    public static ISO8583DTO unpackISO8583DTO(String receivedMsg) throws IncorrectMessageException{

        if(null == receivedMsg){
            throw new IncorrectMessageException("报文为空");
        }
        int totalLen = receivedMsg.length();
        if(totalLen < 40){
            throw new IncorrectMessageException("报文格式不正确，报文长度最少为40");
        }

        int msgLen = Integer.valueOf(receivedMsg.substring(87,95));
        //if(msgLen != totalLen - 4){
        //    throw new IncorrectMessageException("报文长度不匹配");
        //}
        String messageType = receivedMsg.substring(95,99);
        String hexBitMap = receivedMsg.substring(99,115);
        String binaryBitMap = NumberStringUtil.hexToBinaryString(hexBitMap);
        //String binaryBitMap = hexBitMap;
        String[] binaryBitMapArgs = binaryBitMap.split("");
        String msg = receivedMsg.substring(115);

        ISO8583DTO iso8583DTO128 = (ISO8583DTO) msgToObject(ISO8583DTO.class, binaryBitMapArgs, msg);
        iso8583DTO128.setMessageType(messageType);

        return iso8583DTO128;
    }

    private static String getBitMapAndMsg(Object iso8583DTO, int bitLen) throws IncorrectLengthException {
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
        // 将每个域对应的值，保存到对应下标中
        String[] fldSendValues = new String[bitLen];
        try {
            // 循环判断哪个字段有值
            for(Map.Entry<Integer, String> entry : iso8583DTOFldMap.entrySet()){
                fldName = entry.getValue();
                propertyDescriptor = new PropertyDescriptor(fldName, iso8583DTO.getClass());
                fldValue = (String) propertyDescriptor.getReadMethod().invoke(iso8583DTO);

                if(StringUtils.isNotEmpty(fldValue)){
                    // 如果此域有值，将对应的位图位置修改为1
                    bitMap = bitMap.replace(entry.getKey()-1,entry.getKey(),"1");
                    // 根据注解对值进行处理
                    Field field = iso8583DTO.getClass().getDeclaredField(fldName);
                    fldSendValue = verifyAndTransValue(field,fldValue);
                    fldSendValues[entry.getKey()-1] = fldSendValue;
                }
            }
        } catch (IncorrectLengthException e) {
            throw e;
        } catch (Exception e){
            log.info("对象序列化报错");
            return "";
        }

        // 将128位2进制位图转换为32位16进制数据
        String bitMapHexStr = NumberStringUtil.binaryToHexString(bitMap.toString());
        //String bitMapHexStr = bitMap.toString();
        log.info("bitmap = "+ bitMapHexStr);
        StringBuffer bitMapAndMsg = new StringBuffer();
        // 位图在前，先拼接位图
        bitMapAndMsg.append(bitMapHexStr);
        // 拼接报文数据
        for(String value : fldSendValues){
            if(StringUtils.isNotEmpty(value)){
                bitMapAndMsg.append(value);
            }
        }

        return bitMapAndMsg.toString();
    }

    /**
     * 获取ISO8583DTO类的属性，key为fldIndex，value为属性名
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

    private static Object msgToObject(Class clazz, String[] binaryBitMapArgs, String msg) {

        try{
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
            // 循环位图，第一位表示64域或者128域，所以从第二位开始
            for(int i=1,len=binaryBitMapArgs.length; i<len; i++){
                if(Objects.equals(binaryBitMapArgs[i], "0")){
                    // 0表示没有值，跳过
                    continue;
                }

                // 位图下标从1开始，所以需要+1
                fldName = iso8583DTOFldMap.get(i+1);
                field = clazz.getDeclaredField(fldName);
                fldAnnotation = field.getAnnotation(ISO8583Annotation.class);
                fldFlag = fldAnnotation.fldFlag();
                if(Objects.equals(FldFlag.FIXED, fldFlag)){
                    dataLength = fldAnnotation.dataFldLength();
                }else if(Objects.equals(FldFlag.UNFIXED_2, fldFlag)){
                    dataLength = Integer.valueOf(msg.substring(indexFlag, indexFlag=indexFlag+2));
                }else if(Objects.equals(FldFlag.UNFIXED_3, fldFlag)){
                    dataLength = Integer.valueOf(msg.substring(indexFlag, indexFlag=indexFlag+3));
                }else{
                    //未知类型，不做处理
                    continue;
                }

                fldValue = msg.substring(indexFlag,indexFlag+dataLength);
                indexFlag += dataLength;
                field.setAccessible(true);
                field.set(retObject,fldValue);
            }

            return retObject;
        }catch (Exception e){
            log.info("对象反序列化报错");
            return null;
        }


    }

    /**
     * 组包时根据字段原值按照其配置规则转为十六进制 PACK
     * @param field
     * @param fldValue          字段值
     * @exception Exception .
     * */
    private static String verifyAndTransValue(Field field, String fldValue)
            throws IncorrectLengthException {

        boolean fldHasAnnotation = field.isAnnotationPresent(ISO8583Annotation.class);
        if (!fldHasAnnotation) {
            return fldValue;
        }

        ISO8583Annotation iso8583Annotation = field.getAnnotation(ISO8583Annotation.class);
        FldFlag fldFlag = iso8583Annotation.fldFlag();
        int expectLen = iso8583Annotation.dataFldLength();
        String type = iso8583Annotation.type();
        int actualLen = fldValue.length();

        // 固定长度，则校验一下长度是否一致
        if(Objects.equals(FldFlag.FIXED, fldFlag)){
            if(actualLen != expectLen){
                String msg = String.format("%s长度不正确，期望长度为[%d]，实际长度为[%d]。"
                        ,field.getName(),expectLen,actualLen);
                throw new IncorrectLengthException(msg);
            }
            if(type.equals("ASC")){
                fldValue=NumberStringUtil.stringToAscii(fldValue);
            }
            return fldValue;
        }

        // 可变长度，校验一下长度是否超过上限。如果长度符合，则在前边拼接长度值
        if (Objects.equals(FldFlag.UNFIXED_2,fldFlag) || Objects.equals(FldFlag.UNFIXED_3,fldFlag)) {
            if(actualLen > expectLen){
                String msg = String.format("%s长度不正确，最大长度为[%d]，实际长度为[%d]。"
                        ,field.getName(),expectLen,actualLen);
                throw new IncorrectLengthException(msg);
            }

            int len = 2;
            if(Objects.equals(FldFlag.UNFIXED_3,fldFlag)){
                len = 3;
            }
            // 在报文前边拼接长度
            fldValue = NumberStringUtil.addLeftChar(String.valueOf(actualLen),len, '0') + fldValue;
            if(type.equals("ASC")){
                fldValue=NumberStringUtil.stringToAscii(fldValue);
            }
            return fldValue;
        }
        if(type.equals("ASC")){
            fldValue=NumberStringUtil.stringToAscii(fldValue);
        }
        return fldValue;
    }

    /**
     * 初始64和128域位图。
     * 64域的全是0；128域的除第一位为1，其它位全为0
     * @param d
     * @return
     */
    private static StringBuffer initBitMap(int d) {

        StringBuffer bf = new StringBuffer();

        if(d != 64 && d != 128){
            return bf;
        }

        if(d == 64){
            bf.append("0");
        }else{
            // 128域的第一位为"1"
            bf.append("1");
        }

        for(int i = 1; i < d; i++){
            bf.append("0");
        }

        return bf;
    }

    /**
     * 向服务器 发送8583报文
     *
     * @param send8583Str 发送给服务器的报文
     *
     * @param host 主机地址IP
     *
     * @param port 端口号
     *
     * @return 返回的数据
     * */
    public static String send8583(String send8583Str,String host,int port) throws Exception{
        //客户端请求与本机在20011端口建立TCP连接
        Socket client = new Socket(host, port);
        client.setSoTimeout(70000);
        //获取Socket的输出流，用来发送数据到服务端
        PrintStream out = new PrintStream(client.getOutputStream());
        //获取Socket的输入流，用来接收从服务端发送过来的数据
        InputStream buf =  client.getInputStream();
        String str = "mpos-"+send8583Str;
        //发送数据到服务端
        out.println(str);
        try{
            byte[] b = new byte[1024];
            int rc=0;
            int c = 0;
            while( (rc = buf.read(b, c, 1024) )>=0){
                c = buf.read(b, 0, rc);
            }
            String returnStr = byte2hex(b);
            String string = returnStr;
            String str16 = string.substring(0, 4);
            int leng = Integer.parseInt(str16,16);
            String result = string.substring(0, leng*2 + 4);
            if (client!=null) {
                client.close();
            }
            return result;
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Time out, No response");
        }
        if (client!=null) {
            client.close();
        }
        return null;
    }
    public static String byte2hex(byte[] b) // 二进制转字符串
    {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));

            if (stmp.length() == 1){
                hs = hs + "0" + stmp;
            }
            else{
                hs = hs + stmp;
            }
        }
        return hs;
    }

}


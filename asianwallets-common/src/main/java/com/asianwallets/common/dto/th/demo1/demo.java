package com.asianwallets.common.dto.th.demo1;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:58
 **/
public class demo {

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
    public static void main(String[] args) {

        ISO8583DTO iso8583DTO = new ISO8583DTO();
        iso8583DTO.setMessageType("0820");
        iso8583DTO.setTransmissionDateAndTime7("0303145843");
        iso8583DTO.setSystemTraceAuditNumber11("000042");
        iso8583DTO.setAcquiringInstitutionDentificationCode32("03090000");

        String sendMsg;
        try {
            // 组包
            sendMsg = ISO8583Util.packISO8583DTO(iso8583DTO);
            System.out.println(sendMsg);

            // 解包
            ISO8583DTO iso8583DTO1281 = ISO8583Util.unpackISO8583DTO(sendMsg);
            System.out.println(iso8583DTO1281.toString());
        } catch (IncorrectLengthException e) {
            System.out.println(e.getMsg());
        } catch (IncorrectMessageException e) {
            System.out.println(e.getMsg());
        }

    }
}

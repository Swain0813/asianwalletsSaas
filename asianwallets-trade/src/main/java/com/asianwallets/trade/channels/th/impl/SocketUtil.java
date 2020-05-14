package com.asianwallets.trade.channels.th.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Slf4j
public class SocketUtil{
    /**
     * 发送socket请求
     * @param clientIp
     * @param clientPort
     * @param msg
     * @return
     */
    private static synchronized String tcpPost(String clientIp,String clientPort,String msg){
        String rs = "";
        
        if(clientIp==null||"".equals(clientIp)||clientPort==null||"".equals(clientPort)){
            log.error("Ip或端口不存在...");
            return null;
        }
        
        int clientPortInt = Integer.parseInt(clientPort);
        
        log.info("clientIp："+clientIp+" clientPort："+clientPort);

        Socket s = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            s = new Socket(clientIp, clientPortInt);
            s.setSendBufferSize(4096);
            s.setTcpNoDelay(true);
            s.setSoTimeout(60*1000);
            s.setKeepAlive(true);
            out = s.getOutputStream();
            in = s.getInputStream();
            
            //准备报文msg
            log.info("准备发送报文："+msg);
            
            out.write(msg.getBytes("GBK"));
            out.flush();
            
            byte[] rsByte = readStream(in);
            
            if(rsByte!=null){
                rs = new String(rsByte, "GBK");
            }
            
            
        } catch (Exception e) {
            log.error("tcpPost发送请求异常："+e.getMessage());
        }finally{
            log.info("tcpPost(rs)："+rs);
            try {
                if(out!=null){
                    out.close();
                    out = null;
                }
                if(in!=null){
                    in.close();
                    in = null;
                }
                if(s!=null){
                    s.close();
                    s = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return rs;

    }
    
    /**
     * 读取输入流
     * @param in
     * @return
     */
    private static byte[] readStream(InputStream in){
        if(in==null){
            return null;
        }
        
        byte[] b = null;
        ByteArrayOutputStream outSteam = null;
        try {
            byte[] buffer = new byte[1024];
            outSteam = new ByteArrayOutputStream();
            
            int len = -1; 
            while ((len = in.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            
            b = outSteam.toByteArray();
        } catch (IOException e) {
            log.error("读取流信息异常"+e);
            e.printStackTrace();
        } finally{
            try {
                if(outSteam!=null){
                    outSteam.close();
                    outSteam = null;
                }
                if(in!=null){
                    in.close();
                    in = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;  
    }

    public static void main(String[] args) {
        String reqData = "0087600609000080010000000038353239393939353831323035303130303031383634343030303030303030383630303030353030303030303030383532393939393538313230353031303030303030353708000020000100c00012198124110000860000530303031383634343835323939393935383132303530310011500000010030003303031";
        String IP = "58.248.241.169";
        String port = "10089";
        String re = tcpPost(IP, port, reqData);
        System.out.println(re);
    }
}
package com.asianwallets.common.dto.th.tlv;

import com.asianwallets.common.dto.th.ISO8583.NumberStringUtil;
import com.payneteasy.tlv.*;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-20 16:08
 **/
public class TlvDemo {

    public static void main(String[] args) throws Exception{
        ////测试数据
        //String data = "5F5221303002020232303139303631333030303030313130353230303035323830310202";
        //
        //TLVTools  tlvTools = new TLVTools();
        //TLV tlv;
        //tlvTools.unpack(data);
        //
        //for(int i=0;i<tlvTools.tlvList.size();i++)
        //{
        //    tlv = tlvTools.tlvList.get(i);
        //    System.out.print("   标签:["+i+"]"+"\n");
        //    System.out.print("  tag:"+tlv.getTag()+"\n");
        //    System.out.print("  len:"+tlv.getLen()+"\n");
        //    System.out.print("value:"+ tlvTools.bytesToHex(tlv.getValue())+"\n");
        //}

        System.out.println("----------------------------- 组码 ------------------------------------------------");
        BerTlvBuilder berTlvBuilder = new BerTlvBuilder();
        //这里的Tag要用16进制,Length是自动算出来的,最后是要存的数据
        //berTlvBuilder.addText(new BerTag(0x5F52), "00\u0002\u0002\u000220190613000001105200052801\u0002\u0002");
        //berTlvBuilder.addText(new BerTag(0x5F52), "303002020232303139303631333030303030313130353230303035323830310202");
        berTlvBuilder.addBytes(new BerTag(0x5F52),"303002020232303139303631333030303030313130353230303035323830310202".getBytes());
        //berTlvBuilder.addText(new BerTag(0x2), "3213");
        ////add value的时候可以选择是text格式或者是data byte[]等等其他都可以
        //berTlvBuilder.addDate(new BerTag(0x3), new Date());
        ////默认是不支持汉字的，所以我们要把他转成字节
        //byte[] bytes3 = "我爱java，java爱我".getBytes("UTF-8");
        //berTlvBuilder.addBytes(new BerTag(0x4),bytes3);
        ////这里就完成参数的输入了，然后将它转成字节
        byte[] bytes = berTlvBuilder.buildArray();
        ////转成Hex码来传输
        String hexString = HexUtil.toHexString(bytes);
        System.out.println(hexString);

        System.out.println("----------------------------- 解码 ------------------------------------------------");
        //将hex码转成byte字节
        byte[] bytes2 = HexUtil.parseHex("5F5221303002020232303139303631333030303030313130353230303035323830310202");
        BerTlvParser parser = new BerTlvParser();
        BerTlvs tlvs = parser.parse(bytes2, 0, bytes2.length);

        //如果value的数据类型都一样的花可以通过getList来获取然后便利输出
        List<BerTlv> list = tlvs.getList();
        for (BerTlv berTlv : list) {
            System.out.println("tag = "+berTlv.getTag());
            byte[] bytesValue1 = berTlv.getBytesValue();
            String s = new String(bytesValue1,"UTF-8");
            System.out.println("value1 = "+s);
            System.out.println("value2 = "+NumberStringUtil.bcd2Str(bytesValue1));
        }

        //也可以指定Tag来获得数据
        //BerTlv berTlv = tlvs.find(new BerTag(0x3));
        //System.out.println(berTlv.getHexValue());


    }
}

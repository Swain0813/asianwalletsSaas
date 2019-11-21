package com.asianwallets.common.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-06-12 16:30
 **/
@Slf4j
public class XMLUtil {

    public XMLUtil() {
    }

    public static Map<String, Object> xml2Map(String xml) throws Exception {
        return xmlDoc2Map(DocumentHelper.parseText(xml));
    }

    public static Map<String, Object> xmlDoc2Map(Document xmlDoc) {
        Map<String, Object> map = new HashMap();
        if (xmlDoc == null) {
            return map;
        } else {
            Element root = xmlDoc.getRootElement();
            Iterator iterator = root.elementIterator();

            while(iterator.hasNext()) {
                Element e = (Element)iterator.next();
                List list = e.elements();
                if (list.size() > 0) {
                    map.put(e.getName(), Dom2Map(e, map));
                } else {
                    map.put(e.getName(), e.getText());
                }
            }

            return map;
        }
    }

    private static Map Dom2Map(Element e, Map map) {
        List list = e.elements();
        if (list.size() > 0) {
            for(int i = 0; i < list.size(); ++i) {
                Element iter = (Element)list.get(i);
                List mapList = new ArrayList();
                if (iter.elements().size() > 0) {
                    Map m = Dom2Map(iter, map);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            ((List)mapList).add(obj);
                            ((List)mapList).add(m);
                        }

                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List)obj;
                            ((List)mapList).add(m);
                        }

                        map.put(iter.getName(), mapList);
                    } else {
                        map.putAll(m);
                    }
                } else if (map.get(iter.getName()) != null) {
                    Object obj = map.get(iter.getName());
                    if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                        mapList = new ArrayList();
                        ((List)mapList).add(obj);
                        ((List)mapList).add(iter.getText());
                    }

                    if (obj.getClass().getName().equals("java.util.ArrayList")) {
                        mapList = (List)obj;
                        ((List)mapList).add(iter.getText());
                    }

                    map.put(iter.getName(), mapList);
                } else {
                    map.put(iter.getName(), iter.getText());
                }
            }
        } else {
            map.put(e.getName(), e.getText());
        }

        return map;
    }

    public static String objectToXml(Object obj) {
        XStream xStream = new XStream();
        xStream.processAnnotations(obj.getClass());
        return xStream.toXML(obj);
    }

    public static <T> T xmlToObject(String xml, Class<T> cls) {
        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(cls);
        return (T) xstream.fromXML(xml);
    }

    public static String map2XMLForWeChat(Map<String, String> map) {
        log.info("into com.cbpay.frame.util.XMLUtil.map2XMLForWeChat#进方法");
        String str = null;
        if (map != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("<xml>");
            Set set = map.keySet();
            Iterator it = set.iterator();

            while(it.hasNext()) {
                String key = (String)it.next();
                String value = (String)map.get(key);
                sb.append("<" + key + ">" + value + "</" + key + ">");
            }

            sb.append("</xml>");
            str = sb.toString();
        }

        log.info("out com.cbpay.frame.util.XMLUtil.map2XMLForWeChat#出方法：XML=" + str);
        return str;
    }

    public static Map<String, String> xml2MapForWeChat(String xmlstr) {
        Map<String, String> map = new HashMap();
        if (xmlstr != null && !xmlstr.equals("")) {
            try {
                SAXReader reader = new SAXReader();
                reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
                reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                Document doc = reader.read(new ByteArrayInputStream(xmlstr.getBytes("UTF-8")));
                Element root = doc.getRootElement();
                List children = root.elements();
                if (children != null && children.size() > 0) {
                    for(int i = 0; i < children.size(); ++i) {
                        Element child = (Element)children.get(i);
                        map.put(child.getName(), child.getTextTrim());
                    }
                }
            } catch (Exception var8) {
                var8.printStackTrace();
            }
        }

        return map;
    }

    public static Map<String, String> xmlToMap(String xml, String charset) throws UnsupportedEncodingException, DocumentException {
        HashMap respMap = new HashMap();

        try {
            SAXReader reader = new SAXReader();
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            Document doc = reader.read(new ByteArrayInputStream(xml.getBytes(charset)));
            Element root = doc.getRootElement();
            xmlToMap((Element)root, (Map)respMap);
        } catch (SAXException var6) {
            var6.printStackTrace();
        }

        return respMap;
    }

    public static Map<String, String> xmlToMap(Element tmpElement, Map<String, String> respMap) {
        if (tmpElement.isTextOnly()) {
            respMap.put(tmpElement.getName(), tmpElement.getText());
            return respMap;
        } else {
            Iterator eItor = tmpElement.elementIterator();

            while(eItor.hasNext()) {
                Element element = (Element)eItor.next();
                xmlToMap(element, respMap);
            }

            return respMap;
        }
    }

    public static Map<String, String> parseAlipayXML(String xml) throws DocumentException {
        log.info("into com.cbpay.frame.util.XMLUtil.parseAlipayXML#进入方法");
        Map<String, String> map = new HashMap();
        Document doc = null;

        try {
            if (xml != null && !xml.equals("")) {
                SAXReader reader = new SAXReader();
                reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
                reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                reader.setEncoding("UTF-8");
                doc = reader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
                if (doc != null && doc.getRootElement() != null) {
                    Element rootElt = doc.getRootElement();
                    log.debug("into com.cbpay.frame.util.XMLUtil.parseAlipayXML#进入方法,根节点：" + rootElt.getName());
                    Element iter1 = rootElt.element("is_success");
                    if (iter1 != null && iter1.getText() != null) {
                        map.put("is_success", iter1.getText());
                        log.info("into com.cbpay.frame.util.XMLUtil.parseAlipayXML#进入方法，is_success=" + iter1.getText());
                        if (iter1.getText().equals("T")) {
                            Element iter2 = rootElt.element("request");
                            Iterator iter2_child = iter2.elementIterator("param");

                            Element iter3;
                            while(iter2_child.hasNext()) {
                                iter3 = (Element)iter2_child.next();
                                Attribute attr = iter3.attribute("name");
                                map.put(attr.getValue(), iter3.getText());
                            }

                            iter3 = rootElt.element("response");
                            Element iter4 = iter3.element("alipay");
                            Iterator iter4_child = iter4.elementIterator();

                            Element iter5;
                            while(iter4_child.hasNext()) {
                                iter5 = (Element)iter4_child.next();
                                map.put(iter5.getName(), iter5.getText());
                            }

                            iter5 = rootElt.element("sign");
                            map.put("sign", iter5.getText());
                            Element iter6 = rootElt.element("sign_type");
                            map.put("sign_type", iter6.getText());
                        } else {
                            log.info("out com.cbpay.frame.util.XMLUtil.parseAlipayXML#请求失败");
                        }
                    } else {
                        log.info("out com.cbpay.frame.util.XMLUtil.parseAlipayXML#is_success节点为空");
                    }
                } else {
                    log.info("out com.cbpay.frame.util.XMLUtil.parseAlipayXML#doc对象为空");
                }
            } else {
                log.info("out com.cbpay.frame.util.XMLUtil.parseAlipayXML#输入参数为空");
            }
        } catch (SAXException var13) {
            var13.printStackTrace();
            log.error("out com.cbpay.frame.util.XMLUtil.parseAlipayXML#异常：", var13);
        } catch (Exception var14) {
            var14.printStackTrace();
        }

        log.info("out com.cbpay.frame.util.XMLUtil.parseAlipayXML#出方法");
        return map;
    }

    public static Map<String, String> parseAlipayResponseXML(String xml) throws DocumentException {
        log.info("into com.cbpay.frame.util.XMLUtil.parseAlipayResponseXML#进入方法");
        Map<String, String> map = new HashMap();
        Document doc = null;

        try {
            if (xml != null && !xml.equals("")) {
                SAXReader reader = new SAXReader();
                reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
                reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                doc = reader.read(new ByteArrayInputStream(xml.getBytes()));
                if (doc != null && doc.getRootElement() != null) {
                    Element rootElt = doc.getRootElement();
                    log.debug("into com.cbpay.frame.util.XMLUtil.parseAlipayResponseXML#进入方法,根节点：" + rootElt.getName());
                    Element iter1 = rootElt.element("is_success");
                    if (iter1 != null && iter1.getText() != null) {
                        map.put("is_success", iter1.getText());
                        if (iter1.getText().equals("T")) {
                            Element iter3 = rootElt.element("response");
                            Element iter4 = iter3.element("alipay");
                            Iterator iter4_child = iter4.elementIterator();

                            Element iter5;
                            while(iter4_child.hasNext()) {
                                iter5 = (Element)iter4_child.next();
                                map.put(iter5.getName(), iter5.getText());
                            }

                            iter5 = rootElt.element("sign");
                            map.put("sign", iter5.getText());
                            Element iter6 = rootElt.element("sign_type");
                            map.put("sign_type", iter6.getText());
                        } else {
                            log.info("out com.cbpay.frame.util.XMLUtil.parseAlipayResponseXML#is_success=F请求过程异常");
                        }
                    } else {
                        log.info("out com.cbpay.frame.util.XMLUtil.parseAlipayResponseXML#is_success节点为空");
                    }
                } else {
                    log.info("out com.cbpay.frame.util.XMLUtil.parseAlipayResponseXML#doc对象为空");
                }
            } else {
                log.info("out com.cbpay.frame.util.XMLUtil.parseAlipayResponseXML#输入参数为空");
            }
        } catch (SAXException var11) {
            var11.printStackTrace();
            log.error("out com.cbpay.frame.util.XMLUtil.parseAlipayResponseXML#异常：", var11);
        }

        log.info("out com.cbpay.frame.util.XMLUtil.parseAlipayResponseXML#出方法");
        return map;
    }

    public static void main(String[] args) {
        try {
            String xmlstr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <alipay> <is_success>T</is_success> <request> <param name=\"_input_charset\">UTF-8</param> <param name=\"currency\">USD</param> </request> <response> <alipay> <alipay_trans_id>2011091703338463</alipay_trans_id> <partner_trans_id>201311221000000002</partner_trans_id> <result_code>SUCCESS</result_code> </alipay> </response> <sign>6fb8a322f15cfd0fcfe65301b10f6994</sign> <sign_type>MD5</sign_type> </alipay>";
            String xmlstr2 = "<?xml version=\"1.0\" encoding=\"GBK\"?><alipay><is_success>F</is_success><error>ILLEGAL_SIGN</error></alipay>";
            String xmlstr3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><alipay><is_success>T</is_success><request><param name=\"extend_params\">{\"secondary_merchant_industry\":\"5812\",\"secondary_merchant_id\":\"20170828500054\",\"secondary_merchant_name\":\"蔡豆豆\",\"store_id\":\"20170828500054\",\"store_name\":\"蔡豆豆\"}</param><param name=\"_input_charset\">UTF-8</param><param name=\"subject\">SALE</param><param name=\"sign\">882660eafe34ae6d3932cc1e0448f957</param><param name=\"terminal_timestamp\">1540549333201</param><param name=\"notify_url\">http://119.23.136.80/ITSBoss/alipayPayNotify/aliPayCB_TPMQRCReturn.do</param><param name=\"product_code\">OVERSEAS_MBARCODE_PAY</param><param name=\"out_trade_no\">CBO20181026207446</param><param name=\"trans_currency\">SGD</param><param name=\"partner\">2088421920790891</param><param name=\"service\">alipay.acquire.precreate</param><param name=\"total_fee\">0.02</param><param name=\"currency\">SGD</param><param name=\"sign_type\">MD5</param><param name=\"timestamp\">1540549333201</param></request><response><alipay><big_pic_url>https://mobilecodec.alipay.com/show.htm?code=bax08616tgysbwsaty4o009b&amp;picSize=L</big_pic_url><out_trade_no>CBO20181026207446</out_trade_no><pic_url>https://mobilecodec.alipay.com/show.htm?code=bax08616tgysbwsaty4o009b&amp;picSize=M</pic_url><qr_code>https://qr.alipay.com/bax08616tgysbwsaty4o009b</qr_code><result_code>SUCCESS</result_code><small_pic_url>https://mobilecodec.alipay.com/show.htm?code=bax08616tgysbwsaty4o009b&amp;picSize=S</small_pic_url><voucher_type>qrcode</voucher_type></alipay></response><sign>47004d1f76eb470db5eda0825e8a3708</sign><sign_type>MD5</sign_type></alipay>";
            Map<String, String> map = parseAlipayResponseXML(xmlstr3);
            String result_code = ((String)map.get("result_code")).toString();
            System.out.println(result_code);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }
}
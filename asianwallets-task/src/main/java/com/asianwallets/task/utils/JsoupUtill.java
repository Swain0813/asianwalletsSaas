package com.asianwallets.task.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * jsoup处理工具类
 *
 * @author RyanCai
 */
public class JsoupUtill {

    /**
     * 获得指定url的document对象
     *
     * @param url:网址
     * @return
     */
    public static Document getPageDocument(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).timeout(12000).get();// 设置连接超时时间
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * 将doc对象解析成节点list
     *
     * @param doc
     * @return
     */
    public static List<Element> getBocExRateNodeList(Document doc) {
        List<Element> list = new ArrayList<Element>();
        try {
            if (doc != null) {
                Elements trs = doc.select("body>table>tbody>tr>td[valign=top]>table[align=center]>tbody>tr>td[align=center][valign=top]>table>tbody>tr[align=center]");
                Iterator it = trs.iterator();
                while (it.hasNext()) {
                    Element tr = (Element) it.next();
                    list.add(tr);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将doc对象解析成节点list
     *
     * @param doc
     * @return
     */
    public static List<Element> getVndExRateList(Document doc) {
        List<Element> list = new ArrayList<Element>();
        try {
            if (doc != null) {
                Elements trs = doc.select("body>table>tbody>tr[valign!=top]");
                Iterator it = trs.iterator();
                while (it.hasNext()) {
                    Element tr = (Element) it.next();
                    list.add(tr);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        list.remove(0);//删除第一个元素
        list.remove(list.size() - 1);//删除最后一个元素
        return list;
    }

    /**
     * 将doc对象解析成节点list
     *
     * @param doc
     * @return
     */
    public static List<Element> getCourierList(Document doc) {
        List<Element> list = new ArrayList<Element>();
        try {
            if (doc != null) {//body[style=font-size:14px;]>div[style=background:#fdfdfd;]>
                Elements trs = doc.select("table[class=table_style]>tbody>tr[class!=firstRow]>td");
                Iterator it = trs.iterator();
                while (it.hasNext()) {
                    Element tr = (Element) it.next();
                    list.add(tr);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取html标签里的纯文本
     *
     * @param html
     * @return
     */
    public static String getText(String html) {
        if (html == null)
            return null;
        return Jsoup.clean(html, Whitelist.none());
    }

    public static void main(String[] args) {
//        Document pageDocument = JsoupUtill.getPageDocument("http://www.bankofchina.com/sourcedb/vnd/");
//        System.out.println(pageDocument);
//        System.out.println("------------------------------------------------------------------------------------------------------------------------");
//        List<Element> list = JsoupUtill.getVndExRateList(JsoupUtill.getPageDocument("http://www.bankofchina.com/sourcedb/vnd/"));
//        for (Element element : list) {
//            String text = JsoupUtill.getText(element.html());
//            System.out.println(text);
//        }
//        System.out.println("------------------------------------------------------------------------------------------------------------------------");
//        Document pageDocument = JsoupUtill.getPageDocument("http://www.boc.cn/sourcedb/whpj/enindex_4.html");
//        System.out.println(pageDocument);
//        System.out.println("------------------------------------------------------------------------------------------------------------------------");
//        List<Element> list2 = JsoupUtill.getBocExRateNodeList(JsoupUtill.getPageDocument("http://www.boc.cn/sourcedb/whpj/enindex_4.html"));
//        for (Element element : list2) {
//            String text = JsoupUtill.getText(element.html());
//            System.out.println(text);
//        }


//        Document pageDocument = JsoupUtill.getPageDocument("https://www.trackingmore.com/help_article-25-31-en.html");
//        //System.out.println(pageDocument);
//        List<Element> courierList = JsoupUtill.getCourierList(pageDocument);
//        for (Element element : courierList) {
//            String text = JsoupUtill.getText(element.html());
//            System.out.println(text);
//        }

        Document pageDocument = JsoupUtill.getPageDocument("https://www.xe.com/currency/usd-us-dollar");
        System.out.println(pageDocument);

    }
}

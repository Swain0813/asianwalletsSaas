package com.asianwallets.channels.bdt;

import com.octopuscards.oos.xml.JAXBUtility;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-23 11:44
 **/
public class XmlConverter {
    private static Map<String, JAXBContext> oosContextMap = new ConcurrentHashMap();

    private XmlConverter() {
    }

    private static synchronized <T> JAXBContext getContext(Class<T> c) {
        try {
            JAXBContext context = (JAXBContext)oosContextMap.get(c.getCanonicalName());
            if (context == null) {
                context = JAXBContext.newInstance(c);
                oosContextMap.put(c.getCanonicalName(), context);
            }

            return context;
        } catch (JAXBException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static <T> Object inStreamXmlObject(Class<T> c, InputStream inputStream) throws JAXBException {
        return JAXBUtility.createUnmarshaller(getContext(c)).unmarshal(inputStream);
    }

    public static <T> byte[] convertXmlObjectToBytes(T document) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            outStreamXmlObject(document, stream);
            return stream.toByteArray();
        } catch (JAXBException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    private static <T> void outStreamXmlObject(T document, OutputStream outputStream) throws JAXBException {
        JAXBUtility.createMarshaller(getContext(document.getClass())).marshal(document, outputStream);
    }
}

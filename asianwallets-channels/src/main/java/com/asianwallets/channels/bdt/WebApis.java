package com.asianwallets.channels.bdt;


import com.octopuscards.oos.mls.xml.EncryptedContent;
import com.octopuscards.oos.xml.XsDateTimeStamp;
import com.octopuscards.oos.xml.XsUnsignedInt;
import com.octopuscards.oos.xml.transaction.*;
import com.octopuscards.oos.xml.transaction.Currency;
import com.octopuscards.oos.xml.transaction.payment.*;
import org.bouncycastle.crypto.CryptoException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SignatureException;
import java.util.*;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-23 11:43
 **/
public class WebApis {
    private static DatatypeFactory datatypeFactory = null;

    static synchronized DatatypeFactory getDatatypeFactory() {
        if (datatypeFactory == null) {
            try {
                datatypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException var1) {
                var1.printStackTrace();
            }
        }

        return datatypeFactory;
    }

    private WebApis() {
    }

    public static PaymentRequest createPaymentRequest(int gatewayId, String gatewayRef, int merchantId, int amount, String returnUrl, Integer timeout, Calendar businessDate) {
        return createPaymentRequest(gatewayId, gatewayRef, merchantId, amount, returnUrl, timeout, (Calendar)null, businessDate, (List)null, (OosLanguageAbbreviation)null, (Currency)null, (Integer)null, (Integer)null, false);
    }

    public static PaymentRequest createPaymentRequest(int gatewayId, String gatewayRef, Integer merchantId, int amount, String returnUrl, Integer timeout, Calendar expiryTime, Calendar businessDate, List<String> requestDescriptions, OosLanguageAbbreviation lang, Currency currency, Integer locationId, Integer fee, Boolean mPos) {
        if (gatewayRef != null && !gatewayRef.equals("")) {
            if (returnUrl != null && !returnUrl.equals("")) {
                if (timeout == null && expiryTime == null) {
                    throw new IllegalArgumentException("either timeout or expiryTime must be present");
                } else if (businessDate == null) {
                    throw new IllegalArgumentException("businessDate must not be null");
                } else {
                    if (merchantId == null) {
                        merchantId = gatewayId;
                    }

                    PaymentRequest req = new PaymentRequest();
                    req.setGatewayId(gatewayId);
                    req.setGatewayRef(new GatewayRef(gatewayRef));
                    req.setMerchantId(merchantId);
                    req.setAmount(new XsUnsignedInt((long)amount));
                    req.setReturnUrl(new HttpUrl(returnUrl));
                    GregorianCalendar c;
                    if (timeout != null) {
                        req.setTimeout(new XsUnsignedInt((long)timeout));
                    } else if (expiryTime != null) {
                        c = new GregorianCalendar();
                        c.setTimeInMillis(expiryTime.getTimeInMillis());
                        c.setTimeZone(expiryTime.getTimeZone());
                        req.setExpiryTime(new XsDateTimeStamp(getDatatypeFactory().newXMLGregorianCalendar(c)));
                    }

                    c = new GregorianCalendar();
                    c.set(businessDate.get(1), businessDate.get(2), businessDate.get(5));
                    XMLGregorianCalendar d = getDatatypeFactory().newXMLGregorianCalendar(c);
                    d.setHour(-2147483648);
                    d.setMinute(-2147483648);
                    d.setSecond(-2147483648);
                    BusinessDate bd = new BusinessDate(d);
                    req.setBusinessDate(bd);
                    if (requestDescriptions != null) {
                        List<RequestDescription> descs = new ArrayList();
                        Iterator var20 = requestDescriptions.iterator();

                        while(var20.hasNext()) {
                            String desc = (String)var20.next();
                            descs.add(new RequestDescription(desc));
                        }

                        req.setRequestDescriptions(descs);
                    }

                    if (lang != null) {
                        req.setLang(lang);
                    }

                    if (currency != null) {
                        req.setCurrency(currency);
                    }

                    if (locationId != null) {
                        req.setLocationId(new XsUnsignedInt((long)locationId));
                    }

                    if (fee != null) {
                        req.setFee(new XsUnsignedInt((long)fee));
                    }

                    if (mPos != null) {
                        req.setMpos(mPos);
                    } else {
                        req.setMpos(false);
                    }

                    return req;
                }
            } else {
                throw new IllegalArgumentException("returnUrl must not be null or empty");
            }
        } else {
            throw new IllegalArgumentException("gatewayRef must not be null or empty");
        }
    }

    public static PaymentRequestCollection createPaymentRequestCollection(PaymentRequest... reqs) {
        List<PaymentRequest> list = new ArrayList();
        PaymentRequest[] var5 = reqs;
        int var4 = reqs.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            PaymentRequest req = var5[var3];
            list.add(req);
        }

        PaymentRequestCollection col = new PaymentRequestCollection();
        col.setRequests(list);
        col.setDocumentTime(new XsDateTimeStamp(getDatatypeFactory().newXMLGregorianCalendar(new GregorianCalendar())));
        return col;
    }

    public static PaymentEnquiry createPaymentEnquiry(int gatewayId, String gatewayRef) {
        if (gatewayRef != null && !gatewayRef.equals("")) {
            PaymentEnquiry enq = new PaymentEnquiry();
            enq.setGatewayId(gatewayId);
            enq.setGatewayRef(new GatewayRef(gatewayRef));
            return enq;
        } else {
            throw new IllegalArgumentException("gatewayRef must not be null or empty");
        }
    }

    public static PaymentEnquiryCollection createPaymentEnquiryCollection(PaymentEnquiry... enqs) {
        List<PaymentEnquiry> list = new ArrayList();
        PaymentEnquiry[] var5 = enqs;
        int var4 = enqs.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            PaymentEnquiry enq = var5[var3];
            list.add(enq);
        }

        PaymentEnquiryCollection col = new PaymentEnquiryCollection();
        col.setRequests(list);
        col.setDocumentTime(new XsDateTimeStamp(getDatatypeFactory().newXMLGregorianCalendar(new GregorianCalendar())));
        return col;
    }

    public static PaymentCancellation createPaymentCancellation(int gatewayId, String gatewayRef) {
        if (gatewayRef != null && !gatewayRef.equals("")) {
            PaymentCancellation enq = new PaymentCancellation();
            enq.setGatewayId(gatewayId);
            enq.setGatewayRef(new GatewayRef(gatewayRef));
            return enq;
        } else {
            throw new IllegalArgumentException("gatewayRef must not be null or empty");
        }
    }

    public static PaymentCancellationCollection createPaymentCancellationCollection(PaymentCancellation... cancellations) {
        List<PaymentCancellation> list = new ArrayList();
        PaymentCancellation[] var5 = cancellations;
        int var4 = cancellations.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            PaymentCancellation cancel = var5[var3];
            list.add(cancel);
        }

        PaymentCancellationCollection col = new PaymentCancellationCollection();
        col.setRequests(list);
        col.setDocumentTime(new XsDateTimeStamp(getDatatypeFactory().newXMLGregorianCalendar(new GregorianCalendar())));
        return col;
    }

    public static PaymentResultResponse createPaymentResultResponse(PaymentResult result) {
        return createPaymentResultResponse(result, (String)null);
    }

    public static PaymentResultResponse createPaymentResultResponse(PaymentResult result, String resultUrl) {
        PaymentResultResponse resultRsp = new PaymentResultResponse();
        resultRsp.setGatewayId(result.getGatewayId());
        resultRsp.setGatewayRef(result.getGatewayRef());
        if (resultUrl != null && !resultUrl.equals("")) {
            resultRsp.setResultUrl(new HttpUrl(resultUrl));
        }

        resultRsp.setDocumentTime(new XsDateTimeStamp(getDatatypeFactory().newXMLGregorianCalendar(new GregorianCalendar())));
        return resultRsp;
    }

    public static PaymentRequestResponseCollection doPaymentRequest(WebApiCaller caller, MlsService secSrv, String url, PaymentRequestCollection reqs) throws JAXBException, CryptoException, IOException, SignatureException {
        EncryptedContent encryptedReq = secSrv.signAndEncrypt(reqs);
        byte[] enc_req = XmlConverter.convertXmlObjectToBytes(encryptedReq);
        EncryptedContent encryptedRsp = (EncryptedContent)XmlConverter.inStreamXmlObject(EncryptedContent.class, caller.call(new URL(url), enc_req));
        DecryptedContent<PaymentRequestResponseCollection> decryptedRsp = secSrv.decryptAndVerifyDocument(PaymentRequestResponseCollection.class, encryptedRsp);
        return (PaymentRequestResponseCollection)decryptedRsp.getContent();
    }

    public static PaymentEnquiryResponseCollection doPaymentEnquiry(WebApiCaller caller, MlsService secSrv, String url, PaymentEnquiryCollection reqs) throws JAXBException, CryptoException, IOException, SignatureException {
        EncryptedContent encryptedReq = secSrv.signAndEncrypt(reqs);
        byte[] enc_req = XmlConverter.convertXmlObjectToBytes(encryptedReq);
        EncryptedContent encryptedRsp = (EncryptedContent)XmlConverter.inStreamXmlObject(EncryptedContent.class, caller.call(new URL(url), enc_req));
        DecryptedContent<PaymentEnquiryResponseCollection> decryptedRsp = secSrv.decryptAndVerifyDocument(PaymentEnquiryResponseCollection.class, encryptedRsp);
        return (PaymentEnquiryResponseCollection)decryptedRsp.getContent();
    }

    public static PaymentCancellationResponseCollection doPaymentCanellation(WebApiCaller caller, MlsService secSrv, String url, PaymentCancellationCollection reqs) throws JAXBException, CryptoException, IOException, SignatureException {
        EncryptedContent encryptedReq = secSrv.signAndEncrypt(reqs);
        byte[] enc_req = XmlConverter.convertXmlObjectToBytes(encryptedReq);
        EncryptedContent encryptedRsp = (EncryptedContent)XmlConverter.inStreamXmlObject(EncryptedContent.class, caller.call(new URL(url), enc_req));
        DecryptedContent<PaymentCancellationResponseCollection> decryptedRsp = secSrv.decryptAndVerifyDocument(PaymentCancellationResponseCollection.class, encryptedRsp);
        return (PaymentCancellationResponseCollection)decryptedRsp.getContent();
    }

    public static PaymentResult getPaymentResult(MlsService secSrv, InputStream is) throws JAXBException, CryptoException, SignatureException {
        EncryptedContent encryptedContent = (EncryptedContent)XmlConverter.inStreamXmlObject(EncryptedContent.class, is);
        DecryptedContent<PaymentResult> decryptedCmd = secSrv.decryptAndVerifyDocument(PaymentResult.class, encryptedContent);
        return (PaymentResult)decryptedCmd.getContent();
    }

    public static byte[] respondToPaymentResultSubmission(MlsService secSrv, PaymentResult result) throws CryptoException {
        PaymentResultResponse resultRsp = createPaymentResultResponse(result);
        EncryptedContent encryptedReq = secSrv.signAndEncrypt(resultRsp);
        return XmlConverter.convertXmlObjectToBytes(encryptedReq);
    }
}

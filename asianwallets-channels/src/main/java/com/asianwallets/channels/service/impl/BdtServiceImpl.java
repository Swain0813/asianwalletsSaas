package com.asianwallets.channels.service.impl;

import com.asianwallets.channels.bdt.DecryptedContent;
import com.asianwallets.channels.bdt.Utils;
import com.asianwallets.channels.bdt.XmlConverter;
import com.asianwallets.channels.service.BdtService;
import com.asianwallets.common.dto.bdt.BdtDTO;
import com.asianwallets.common.response.BaseResponse;
import com.octopuscards.oos.mls.AesUtility;
import com.octopuscards.oos.mls.AuthenticatedCiphertext;
import com.octopuscards.oos.mls.RsaUtility;
import com.octopuscards.oos.mls.xml.EncryptedContent;
import com.octopuscards.oos.mls.xml.EncryptedKey;
import com.octopuscards.oos.mls.xml.SignedContent;
import com.octopuscards.oos.xml.transaction.payment.PaymentRequestResponseCollection;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-29 10:47
 **/
@Slf4j
@Service
public class BdtServiceImpl implements BdtService {

    private static final String VERSION = "1.3";
    private static String OOS_URL;
    private static String OOSWS_KEYVER;
    private static String PGWS_KEYVER;
    private static String CLIENT_CERT_KEYSTORE;
    public static DatatypeFactory datatypeFactory = null;

    /**
     * @return
     * @Author YangXu
     * @Date 2020/6/29
     * @Descripate 八达通付款
     **/
    @Override
    public BaseResponse payMent(BdtDTO BdtDTO) {

        // -------------- configuration here -----------------
        boolean readKeysFromFile = true;
        boolean hasOOSPrivateKey = false;
        String oosPubKeyFilename = "OOS Transaction Web API (Testing) v3.crt"; // OOS
        // WS
        // public
        // key

        String pgPrivKeyFilename = "196914_private.pem"; // test PG WS public
        // key
        String pgPubKeyFilename = "196914_public.pem"; // test PG WS public key

        int gatewayId = 196914;
        int merchantId = 196914;
        CLIENT_CERT_KEYSTORE = "196914.p12";
        OOSWS_KEYVER = "3";
        PGWS_KEYVER = "1";
        OOS_URL = "https://integration.online.octopus.com.hk:7443/webapi-restricted/payment/request/";
        // OOS_URL =
        // "https://10.46.4.218:7001/webapi-restricted/payment/request/";

        // String pgPrivKeyFilename = "192000v2.pkcs8"; // test PG WS public key
        // String pgPubKeyFilename = "192000v2.cer"; // test PG WS public key
        //
        // String gatewayId = "192000";
        // String merchantId = "192001";
        // OOSWS_KEYVER = "3";
        // PGWS_KEYVER = "2";
        //
        // CLIENT_CERT_KEYSTORE = "196914.p12";
        // OOS_URL =
        // "https://10.46.4.51:47001/wildfly/7101/webapi-restricted/payment/request/";

        log.info("Demo version:" + VERSION + "\n");
        // Preparation (A) ----------------------------------------

        // Generate PG WS key pair (A.1)
        RSAKeyParameters pbPubParams = null;
        RSAPrivateCrtKeyParameters pgPrivParams = null;

        if (readKeysFromFile) { // read from existing keys
            try {
                pbPubParams = Utils.findSignerPublicKey(pgPubKeyFilename);
                pgPrivParams = Utils.findOosPrivateKey(pgPrivKeyFilename);
            } catch (Exception e) {
                log.info("Read from pbPubParams pgPrivParams keys Exception" + e + "\n");
            }
        } else { // generate afresh for testing
            RSAKeyPairGenerator rsakpg = new RSAKeyPairGenerator();
            RSAKeyGenerationParameters params = new RSAKeyGenerationParameters(new BigInteger("35"), new SecureRandom(),
                    2048, 8);
            rsakpg.init(params);
            AsymmetricCipherKeyPair pgKp = rsakpg.generateKeyPair();
            pgPrivParams = (RSAPrivateCrtKeyParameters) pgKp.getPrivate();
            pbPubParams = (RSAKeyParameters) pgKp.getPublic();
        }

        // Generate OOS WS key pair (by OOS)
        RSAKeyParameters oosPubParams = null;
        RSAPrivateCrtKeyParameters oosPrivParams = null;

        if (readKeysFromFile) { // read from existing keys
            try {
                oosPubParams = Utils.findSignerPublicKey(oosPubKeyFilename);
            } catch (Exception e) {
                log.info("Read from oosPubParams keys Exception" + e + "\n");
            }
            // RSAPrivateCrtKeyParameters oosPrivParams = findOosPrivateKey();
            hasOOSPrivateKey = false;
        } else { // generate afresh for testing
            RSAKeyPairGenerator rsakpg = new RSAKeyPairGenerator();
            RSAKeyGenerationParameters params = new RSAKeyGenerationParameters(new BigInteger("35"), new SecureRandom(),
                    2048, 8);
            rsakpg.init(params);
            AsymmetricCipherKeyPair oosKp = rsakpg.generateKeyPair();
            oosPrivParams = (RSAPrivateCrtKeyParameters) oosKp.getPrivate();
            oosPubParams = (RSAKeyParameters) oosKp.getPublic();
            hasOOSPrivateKey = true;
        }

        //--------------------------------------------------------------------------------
        // Construct request (B.1.1)

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Hong_Kong"));

        int amount = 1;
        String documentTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S+'08:00'").format(c.getTime());
        c.add(Calendar.MINUTE, 10); // 10-minute expiry time
        String expiryTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S+'08:00'").format(c.getTime());
        String businessDate = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        String pgRef = "PG" + (new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date())); // unique
        // PG
        // reference
        // number
        String returnUrl = "https://payment.example.octopus.com.hk/result";
        byte[] req_doc = ("<paymentRequestCollection xmlns=\"http://namespace.oos.online.octopus.com.hk/transaction/\" "
                + "documentTime=\"" + documentTime + "\"><paymentRequest>" + "<gatewayId>" + Integer.toString(gatewayId) + "</gatewayId>"
                + "<gatewayRef>" + pgRef + "</gatewayRef>" + "<merchantId>" + Integer.toString(merchantId) + "</merchantId>"
                + "<expiryTime>" + expiryTime + "</expiryTime>" + "<businessDate>" + businessDate + "</businessDate>"
                + "<amount>" + Integer.toString(amount) + "</amount>"
                // + "<currency code=\"USD\">" +
                // "<exchangeRate>7.8</exchangeRate>"
                // + "<localAmount>128.20512</localAmount>"
                // + "</currency>" +
                // "<mpos>1</mpos>" +
                + "<returnUrl>" + returnUrl + "</returnUrl>"
                + "</paymentRequest></paymentRequestCollection>").getBytes();

        Integer timeout = 600;

        log.info("Payment Request:\n" + new String(req_doc) + "\n");
        log.info("Payment Request (base64):\n" + Base64.toBase64String(req_doc) + "\n");
        // Sign (B.2) ----------------------------------------
        // sign with PG WS Private Key (B.2.1)
        try {

            byte[] signature = RsaUtility.signData(pgPrivParams, req_doc);
            log.info("Signature:\n" + Utils.bytesToString(signature) + "\n");
            log.info("Signature (base64):\n" + Base64.toBase64String(signature) + "\n");


            // Generate Signed Request Document (B.2.2)
            String signedContent = "<signedContent xmlns=\"http://namespace.oos.online.octopus.com.hk/mls/\"><content>"
                    + Base64.toBase64String(req_doc) + "</content><signature signerId=\"" + gatewayId
                    + "\" keyId=\"" + PGWS_KEYVER + "\">" + Base64.toBase64String(signature)
                    + "</signature></signedContent>";

            log.info("Signed Request Document:\n" + signedContent + "\n");

            // verify with PG WS Public Key (by OOS)
            if (RsaUtility.verifyData(pbPubParams, req_doc, signature)) {
                log.info("Verify: correct\n");
            } else {
                throw new SecurityException("Signature verification failed");
            }

            // Generate request key (B.3.1)
            SecureRandom random = new SecureRandom();
            byte key[] = new byte[32];
            byte iv[] = new byte[12];

            // random.nextBytes(key);
            log.info("Plain Request key:\n" + Utils.bytesToString(key) + "\n");

            // Encrypt (B.3) ----------------------------------------
            // Generate IV (B.3.2)
            // random.nextBytes(iv);

            // Encrypted message (B.3.3)

            AuthenticatedCiphertext cipherText = AesUtility.encryptData(key, iv, signedContent.getBytes());
            byte[] encryptedMsg = cipherText.getCiphertext();

            log.info("Encrypted message:\n" + Utils.bytesToString(encryptedMsg) + "\n");
            log.info("Encrypted message (base64):\n" + Utils.bytesToString(cipherText.getCiphertext()) + "\n");

            // Decrypt message (by OOS)
            byte[] decrypted = AesUtility.decryptData(key, iv, cipherText);
            log.info("Decrypted message:\n" + new String(decrypted) + "\n");
            // Utils.inStreamXmlObject(JAXBContext.newInstance(SignedContent.class),
            // new ByteArrayInputStream(decrypted));

            // encrypt the 'request key' with OOS WS Public Key (B.3.4)
            byte[] encryptedRequestKey = RsaUtility.encryptData(oosPubParams, key);
            log.info("Encrypted Request key:\n" + Utils.bytesToString(encryptedRequestKey) + "\n");

            // decrypt the 'request key' with OOS WS private Key (by OOS)
            if (hasOOSPrivateKey) {
                byte[] decryptedRequestKey = RsaUtility.decryptData(oosPrivParams, encryptedRequestKey);
                log.info("Decrypted Request key:\n" + Utils.bytesToString(decryptedRequestKey) + "\n");
            }
            String enc_req = "<encryptedContent xmlns=\"http://namespace.oos.online.octopus.com.hk/mls/\">" + "<iv>"
                    + Base64.toBase64String(iv) + "</iv>" + "<ciphertext>" + Base64.toBase64String(encryptedMsg)
                    + "</ciphertext>" + "<authenticationTag>" + Base64.toBase64String(cipherText.getAuthenticationTag())
                    + "</authenticationTag>" + "<encryptedKey keyId=\"" + OOSWS_KEYVER + "\">"
                    + Base64.toBase64String(encryptedRequestKey) + "</encryptedKey>" + "</encryptedContent>";

            log.info("Signed then Encrypted Request Document:\n" + enc_req);

            EncryptedContent encryptedContent = sendRequest(enc_req);
            PaymentRequestResponseCollection document = parseResponse(pgPrivParams, encryptedContent);

            log.info("Status: " + document.getResponses().get(0).getStatus().getCode());
            log.info("Landing URL: " + document.getResponses().get(0).getLandingUrl());
        } catch (Exception e) {
            log.info("Landing URL: " + e);
        }
        return null;
    }

    private static EncryptedContent sendRequest(String enc_req) throws Exception {
        EncryptedContent encryptedContent = null;

        try {
            // keystore storing client certificate
            String keyStorePassword = ""; // no password for test keystore
            String keyStore = ""; // no password for test keystore
            String OosWsKeyStoreType = "JKS";
            URL url = new URL(OOS_URL);
            boolean unlimited = Cipher.getMaxAllowedKeyLength("RC5") >= 256;
            // System.out.println("Unlimited cryptography enabled: " +
            // unlimited);
            System.setProperty("javax.net.ssl.keyStore", keyStore);
            System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
            System.setProperty("javax.net.ssl.keyStoreType", OosWsKeyStoreType);
            // System.setProperty("javax.net.debug", "all");
            SSLContext sslCtx = SSLContext.getInstance("TLS");

            String p12Password = "8";
            KeyStore clientStore = KeyStore.getInstance("PKCS12"); // or JKS
            // demo.p12 contains OOS-signed client cert and its private key
            clientStore.load(new FileInputStream(CLIENT_CERT_KEYSTORE), p12Password.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientStore, p12Password.toCharArray());
            KeyManager[] kms = kmf.getKeyManagers();

            // sslCtx.init(kms, new TrustManager[] { new DumbX509TrustManager()
            // }, new java.security.SecureRandom());
            sslCtx.init(kms, null, null);

            HttpsURLConnection.setDefaultSSLSocketFactory(sslCtx.getSocketFactory());

            URLConnection con = url.openConnection();
            HttpsURLConnection http = (HttpsURLConnection) con;
            http.setConnectTimeout(0);
            http.setReadTimeout(0);
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);
            http.setFixedLengthStreamingMode(enc_req.length());
            http.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");
            http.connect();
            OutputStream os = http.getOutputStream();
            os.write(enc_req.getBytes());

            encryptedContent = (EncryptedContent) XmlConverter
                    .inStreamXmlObject(EncryptedContent.class, http.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
            log.info("Send failure");
            throw e;
        }
        return encryptedContent;
    }

    private static PaymentRequestResponseCollection parseResponse(RSAPrivateCrtKeyParameters pgPrivParams,
                                                                  EncryptedContent encryptedContent) throws InvalidCipherTextException, JAXBException, XMLStreamException {
        EncryptedKey encryptedKey = encryptedContent.getEncryptedKey();

        byte[] sessionKey = RsaUtility.decryptData(pgPrivParams, encryptedKey.getContent());
        byte[] signedContentBytes = AesUtility.decryptData(sessionKey, encryptedContent.getIv(),
                encryptedContent.getCiphertext(), encryptedContent.getAuthenticationTag());

        DecryptedContent<SignedContent> decryptedContent = new DecryptedContent<SignedContent>();
        decryptedContent.setEncryptionKeyId(encryptedKey.getKeyId());
        decryptedContent.setRawContent(signedContentBytes);
        SignedContent signedContent2 = (SignedContent) XmlConverter.inStreamXmlObject(SignedContent.class,
                new ByteArrayInputStream(decryptedContent.getRawContent()));

        PaymentRequestResponseCollection document = (PaymentRequestResponseCollection) (XmlConverter.inStreamXmlObject(PaymentRequestResponseCollection.class,
                new ByteArrayInputStream(signedContent2.getContent())));

        // PaymentEnquiryCollection enquiry = new (PaymentEnquiryCollection)
        log.info("Payment Response:\n" + new String(signedContent2.getContent()) + "\n");
        return document;
    }
}

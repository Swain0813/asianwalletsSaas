package com.asianwallets.channels.bdt;


import com.octopuscards.oos.mls.AesUtility;
import com.octopuscards.oos.mls.AuthenticatedCiphertext;
import com.octopuscards.oos.mls.RsaUtility;
import com.octopuscards.oos.mls.xml.EncryptedContent;
import com.octopuscards.oos.mls.xml.EncryptedKey;
import com.octopuscards.oos.mls.xml.Signature;
import com.octopuscards.oos.mls.xml.SignedContent;
import com.octopuscards.oos.xml.XsDateTimeStamp;
import com.octopuscards.oos.xml.transaction.TimestampedDocument;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.GregorianCalendar;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-23 11:41
 **/
public class MlsService {
    private final SecureRandom random = new SecureRandom();
    private RSAPrivateCrtKeyParameters pgWsPrivKey;
    private RSAKeyParameters oosWsPubKey;
    private int gatewayId;
    private int gatewayKeyId;
    private int oosKeyId;

    public MlsService(RSAPrivateCrtKeyParameters pgPrivParams, RSAKeyParameters oosPubParams, int gatewayId, int gatewayKeyId, int oosKeyId) {
        this.pgWsPrivKey = pgPrivParams;
        this.oosWsPubKey = oosPubParams;
        this.gatewayKeyId = gatewayKeyId;
        this.gatewayId = gatewayId;
        this.oosKeyId = oosKeyId;
    }

    public SignedContent signDocument(RSAPrivateCrtKeyParameters signKey, int keyId, int gatewayId, byte[] docBytes) throws CryptoException {
        byte[] signature = RsaUtility.signData(signKey, docBytes);
        Signature signatureElement = new Signature();
        signatureElement.setSignerId(gatewayId);
        signatureElement.setKeyId(keyId);
        signatureElement.setContent(signature);
        SignedContent signedContent = new SignedContent();
        signedContent.setSignature(signatureElement);
        signedContent.setContent(docBytes);
        return signedContent;
    }

    public <T extends TimestampedDocument> DecryptedContent<T> verifyDocument(RSAKeyParameters verifyKey, Class<T> c, DecryptedContent<T> decryptedContent) throws JAXBException, SignatureException {
        SignedContent signedContent = (SignedContent)XmlConverter.inStreamXmlObject(SignedContent.class, new ByteArrayInputStream(decryptedContent.getRawContent()));
        Signature signature = signedContent.getSignature();
        if (!RsaUtility.verifyData(verifyKey, signedContent.getContent(), signature.getContent())) {
            try {
                throw new SignatureException("Signature verification failed. Content=" + new String(signedContent.getContent(), "UTF-8"));
            } catch (UnsupportedEncodingException var7) {
                var7.printStackTrace();
            }
        }

        T document = c.cast(XmlConverter.inStreamXmlObject(c, new ByteArrayInputStream(signedContent.getContent())));
        document.enforceConstraints();
        decryptedContent.setSignerId(signature.getSignerId());
        decryptedContent.setSignKeyId(signature.getKeyId());
        decryptedContent.setContent(document);
        return decryptedContent;
    }

    public EncryptedContent encryptDocument(RSAKeyParameters encryptKey, int keyId, byte[] signedDocBytes) throws InvalidCipherTextException {
        byte[] sessionKey = new byte[32];
        this.random.nextBytes(sessionKey);
        byte[] encryptedSessionKey = RsaUtility.encryptData(encryptKey, sessionKey);
        EncryptedKey encryptedKeyElement = new EncryptedKey();
        encryptedKeyElement.setKeyId(keyId);
        encryptedKeyElement.setContent(encryptedSessionKey);
        EncryptedContent encryptedContentElement = new EncryptedContent();
        encryptedContentElement.setEncryptedKey(encryptedKeyElement);
        byte[] iv = new byte[12];
        this.random.nextBytes(iv);
        encryptedContentElement.setIv(iv);
        AuthenticatedCiphertext encryptedContent = AesUtility.encryptData(sessionKey, iv, signedDocBytes);
        encryptedContentElement.setCiphertext(encryptedContent.getCiphertext());
        encryptedContentElement.setAuthenticationTag(encryptedContent.getAuthenticationTag());
        return encryptedContentElement;
    }

    public <T extends TimestampedDocument> DecryptedContent<T> decryptDocument(RSAPrivateCrtKeyParameters decryptKey, Class<T> c, EncryptedContent encryptedContent) throws InvalidCipherTextException {
        encryptedContent.enforceConstraints();
        EncryptedKey encryptedKey = encryptedContent.getEncryptedKey();
        byte[] sessionKey = RsaUtility.decryptData(decryptKey, encryptedKey.getContent());
        byte[] signedContentBytes = AesUtility.decryptData(sessionKey, encryptedContent.getIv(), encryptedContent.getCiphertext(), encryptedContent.getAuthenticationTag());
        DecryptedContent<T> decryptedContent = new DecryptedContent();
        decryptedContent.setEncryptionKeyId(encryptedKey.getKeyId());
        decryptedContent.setRawContent(signedContentBytes);
        return decryptedContent;
    }

    public <T extends TimestampedDocument> DecryptedContent<T> decryptAndVerifyStream(Class<T> c, InputStream inputStream) throws JAXBException, InvalidCipherTextException, SignatureException {
        EncryptedContent encryptedContent = (EncryptedContent)XmlConverter.inStreamXmlObject(c, inputStream);
        return this.decryptAndVerifyDocument(c, encryptedContent);
    }

    public <T extends TimestampedDocument> DecryptedContent<T> decryptAndVerifyDocument(Class<T> c, EncryptedContent encryptedContent) throws JAXBException, InvalidCipherTextException, SignatureException {
        DecryptedContent<T> decryptedContent = this.verifyDocument(this.oosWsPubKey, c, this.decryptDocument(this.pgWsPrivKey, c, encryptedContent));
        return decryptedContent;
    }

    public <T extends TimestampedDocument> EncryptedContent signAndEncrypt(T document, boolean updateDocTime) throws CryptoException {
        if (updateDocTime) {
            this.updateDocumentTime(document);
        }

        byte[] docBytes = XmlConverter.convertXmlObjectToBytes(document);
        byte[] signDocBytes = XmlConverter.convertXmlObjectToBytes(this.signDocument(this.pgWsPrivKey, this.gatewayKeyId, this.gatewayId, docBytes));
        return this.encryptDocument(this.oosWsPubKey, this.oosKeyId, signDocBytes);
    }

    public <T extends TimestampedDocument> EncryptedContent signAndEncrypt(T document) throws CryptoException {
        return this.signAndEncrypt(document, true);
    }

    private void updateDocumentTime(TimestampedDocument document) {
        XsDateTimeStamp timestamp = new XsDateTimeStamp();
        timestamp.setValue(WebApis.getDatatypeFactory().newXMLGregorianCalendar(new GregorianCalendar()));
        document.setDocumentTime(timestamp);
    }
}

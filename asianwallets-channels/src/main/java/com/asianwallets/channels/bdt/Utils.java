package com.asianwallets.channels.bdt;

import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.encoders.Base64;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;


/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-23 11:41
 **/
public class Utils {
    private Utils() {
    }

    private static String readFile(String filepath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            StringBuilder sb = new StringBuilder();

            for(String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line).append("\n");
            }

            br.close();
            return sb.toString();
        } catch (FileNotFoundException var4) {
            var4.printStackTrace();
            return null;
        } catch (IOException var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static PrivateKey getPrivateTestkey(String filepath) throws IOException, NoSuchAlgorithmException, GeneralSecurityException {
        String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
        String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";
        String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
        String PEM_RSA_PRIVATE_END = "-----END RSA PRIVATE KEY-----";
        String privateKeyPem = readFile(filepath);
        if (privateKeyPem.indexOf("-----BEGIN PRIVATE KEY-----") != -1) {
            privateKeyPem = privateKeyPem.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
            privateKeyPem = privateKeyPem.replaceAll("\\s", "");
            byte[] pkcs8EncodedKey = Base64.decode(privateKeyPem);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePrivate(new PKCS8EncodedKeySpec(pkcs8EncodedKey));
        } else if (privateKeyPem.indexOf("-----BEGIN RSA PRIVATE KEY-----") != -1) {
            privateKeyPem = privateKeyPem.replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "");
            privateKeyPem = privateKeyPem.replaceAll("\\s", "");
            DerInputStream derReader = new DerInputStream(Base64.decode(privateKeyPem));
            DerValue[] seq = derReader.getSequence(0);
            if (seq.length < 9) {
                throw new GeneralSecurityException("Could not parse a PKCS1 private key.");
            } else {
                BigInteger modulus = seq[1].getBigInteger();
                BigInteger publicExp = seq[2].getBigInteger();
                BigInteger privateExp = seq[3].getBigInteger();
                BigInteger prime1 = seq[4].getBigInteger();
                BigInteger prime2 = seq[5].getBigInteger();
                BigInteger exp1 = seq[6].getBigInteger();
                BigInteger exp2 = seq[7].getBigInteger();
                BigInteger crtCoef = seq[8].getBigInteger();
                RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);
                KeyFactory factory = KeyFactory.getInstance("RSA");
                return factory.generatePrivate(keySpec);
            }
        } else {
            throw new GeneralSecurityException("Not supported format of a private key");
        }
    }

    public static PublicKey getPublicKey(String filepath) throws IOException, CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        FileInputStream is = new FileInputStream(filepath);
        X509Certificate cer = (X509Certificate)fact.generateCertificate(is);
        PublicKey key = cer.getPublicKey();
        return key;
    }

    public static RSAKeyParameters findSignerPublicKey(String filename) throws IOException, CertificateException {
        RSAPublicKey gatewayPublicKey = (RSAPublicKey)getPublicKey(filename);
        return new RSAKeyParameters(false, gatewayPublicKey.getModulus(), gatewayPublicKey.getPublicExponent());
    }

    public static RSAPrivateCrtKeyParameters findOosPrivateKey(String filename) throws IOException, GeneralSecurityException {
        RSAPrivateCrtKey oosPrivateKey = (RSAPrivateCrtKey)getPrivateTestkey(filename);
        RSAPrivateCrtKeyParameters rckp = new RSAPrivateCrtKeyParameters(oosPrivateKey.getModulus(), oosPrivateKey.getPublicExponent(), oosPrivateKey.getPrivateExponent(), oosPrivateKey.getPrimeP(), oosPrivateKey.getPrimeQ(), oosPrivateKey.getPrimeExponentP(), oosPrivateKey.getPrimeExponentQ(), oosPrivateKey.getCrtCoefficient());
        return rckp;
    }

    public static String bytesToString(byte[] bytes) {
        return bytes == null ? "(none)" : bytesToString(bytes, bytes.length);
    }

    private static String bytesToString(byte[] bytes, int len) {
        return _bytesToString(bytes, 0, len, true);
    }

    private static String _bytesToString(byte[] bytes, int offset, int len, boolean hex) {
        if (bytes == null) {
            return "(none)";
        } else {
            StringBuffer sb = new StringBuffer();

            for(int i = 0; i < len; ++i) {
                byte bb = bytes[offset + i];
                if (i != 0 && i % 16 == 0) {
                    sb.append("\n");
                }

                sb.append("");
                if (hex) {
                    sb.append(byteToHex(bb));
                } else {
                    sb.append(byteToHex(bb < 0 ? (byte)(bb + 256) : bb));
                }

                sb.append(" ");
            }

            return sb.toString();
        }
    }

    private static String byteToHex(byte b) {
        int i = b & 255;
        return pad(Integer.toHexString(i), -2, "0").toUpperCase();
    }

    private static String pad(String str, int padlen, String pad) {
        String padding = new String();
        int len = Math.abs(padlen) - str.toString().length();
        if (len < 1) {
            return str.toString();
        } else {
            for(int i = 0; i < len; ++i) {
                padding = padding + pad;
            }

            return padlen < 0 ? padding + str : str + padding;
        }
    }
}

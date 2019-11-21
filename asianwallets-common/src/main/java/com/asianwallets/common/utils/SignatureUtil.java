package com.asianwallets.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignatureUtil {

	private static final Logger logger = LoggerFactory.getLogger(SignatureUtil.class);
	private static final String ALGORITHM_SHA_256 = "SHA-256";

	public static String calculateSignature(String input) {
		String signature = null;

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(ALGORITHM_SHA_256);
		} catch (NoSuchAlgorithmException e) {
			logger.error("HeaderEnricherService-No Provider supports this Algorithm: ", e);
		}

		byte[] hashcode = null;
		if (md != null) {
			md.update(input.getBytes());
			hashcode = md.digest();
		}

		if (hashcode != null) {
			signature = DatatypeConverter.printBase64Binary(hashcode);
		}

		return signature;
	}

	/**
	    * enetsPOS扫码加密
	    * @author myy
	    * @param txnReq
	    * @param secretKey
	    * @return
	    * @throws Exception
	    */
	   public static String generateSignature(String txnReq, String secretKey)
				throws Exception {

			System.out.println("txnReq: " + txnReq);

			String concatPayloadAndSecretKey = txnReq + secretKey;

			System.out.println("concatPayloadAndSecretKey: "
					+ concatPayloadAndSecretKey);

			byte[] bytehashSHA256ToBytes = hashSHA256ToBytes(concatPayloadAndSecretKey
					.getBytes());

			String hmac = encodeBase64(bytehashSHA256ToBytes);
			System.out.println("hmac: " + hmac);
			return hmac;

		}
	    
	   /**
	    * enetsPOS扫码加密
	    * @author myy
	    * @param input
	    * @return
	    * @throws Exception
	    */
		public static byte[] hashSHA256ToBytes(byte[] input) throws Exception {

			byte[] byteData = null;
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(input);
			byteData = md.digest();
			return byteData;
		}

		/**
		 * enetsPOS扫码加密
		 * @author myy
		 * @param data
		 * @return
		 * @throws Exception
		 */
		public static String encodeBase64(byte[] data) throws Exception {
			return DatatypeConverter.printBase64Binary(data);
		}
}

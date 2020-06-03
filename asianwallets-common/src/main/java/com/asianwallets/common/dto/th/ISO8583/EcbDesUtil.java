package com.asianwallets.common.dto.th.ISO8583;

import cn.hutool.core.util.HexUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EcbDesUtil {

	public static void main(String[] args) {
/*		String key1 = "0000000000000000";
		String data = "3207FEBFC350BA8ACFC96F2867D32E1CC0122D1274BC996BAAC9485EC41917E861C
    61CA8601630A38586B7733740FB1F7824CAF97396791ED7A1EF6C48782E3B12DFFFECDF71C3181
FA17D37AA6AAA4B41BB1CC9C1A923FC602AADCDF0F5E0A5AF77C5A847476673E66F724C59A539AF3D88074D
219B3612F09D009713BD3EBA8CD45FEBBFA225FB534D9EC067199399B772732E03AD83908C671D8BB751175D
2122AD9CC5B32140AC5423D651CBD00D5315A4ACA49898A2B6DE2BD86BC4A869B54CA454C8AC886213E0
A56358A1B6A2B54CA454C8AC886213E0A56358A1B6A28351520A185DD48F46D898D9CA3004686BAF
3FAAB7CBE70ED6B973442A8752DD195E0FF16777FDBA7CAD750758F6655722462CB89C6E5080E2CBD
FC09BB5C52E924322438D247C2B2FCDED9FA0CD923A82115C5B517625A12E3744EE5
8EEE67CD29A22F232F31F04F0D9A1F809CD496869F52D981014B5FB1F1A1BEA413DF1
EC20571F101401A9D60EC21803EA074696F09F60A8DDA86F44B8A8044C57081F4034FE195E43B3B6EC7C8A11C95049
29230CB486C972B8FE5B3D6F5866B53B7C0945B9DEF3B52253F0AF1FCC4B6FEFB2A3";
		String eninfo = encodeDEA(key1, data);
		System.out.println("key=" + key1);
		System.out.println("data=" + data);
		System.out.println("des加密后：" + eninfo);

		String decodeStr = decodeDEA(key1, eninfo);
		System.out.println("des解密后：" + decodeStr);*/

//		String srcStr="<execStatus><statusCode>000000</statusCode><statusDescription>更新订单成功</statusDescription></execStatus>";
//		//将字节数组转换为十六进制字符串
//		String data=HexUtil.encodeHexStr(srcStr.getBytes());
//		System.out.println("data=" + data);
//
//		String key2 = "EC35E9100872BD3FCC9663D86397C2A5";
//		String eninfo = encode3DEA(key2, data);
//		System.out.println("3des加密后：" + eninfo);
//
//
//
//		String data2 = decode3DEA(key2, eninfo);
//		//将十六进制字符数组转换为字节数组,再转换成字符串
//		String decodeStr = new String(HexUtil.decodeHex(data2));
//		System.out.println("data2=" + data2);
//
//		System.out.println("3des解密后：" + decodeStr);

		String clearKey = decode3DEA("38D57B7C1979CF7910677DE5BB6A56DF", "43FA3050936615D4").toUpperCase();
		System.out.println(clearKey);
		String checkValue = decodeDEA(clearKey, "0000000000000000").toUpperCase();
		System.out.println(checkValue);

	}

	/**
	 * 补位填充最后数据块
	 * @param hexData
	 * @return
	 */
	private static String fillLastBlock(String hexData) {
		// 先检查最后一个数据块长度是否为8字节，进行补齐处理
		int n = hexData.length() % 16;
		if (n != 0) { // 最后的数据块为1-7字节长即为2-14位

			// 先其后加入‘80’刚好达到8字节，则继续，否则继续在后面补0直到8字节
			hexData += "00";
			n = hexData.length() % 16;
			if (n != 0) {
				for (int i = 0; i < (16 - n) / 2; i++) {
					hexData += "00";
				}
			}
		}
		return hexData;
	}
	/**
	 * 单倍长密钥DEA加密算法
	 *
	 * @param hexKey
	 *            16进制密钥(单倍长密钥8字节)
	 * @param hexData
	 *            16进制的明文数据
	 */
	public static String encodeDEA(String hexKey, String hexData) {
		//log.info("单倍长des密钥算法,密钥="+hexKey+",数据="+hexData);
		// log.debug("key size="+hexKey.length());
		// log.debug("data size="+hexData.length());
		if (hexKey.length() != 16) {
			throw new RuntimeException("单倍长密钥DEA加密算法密钥必须为16位十六进制数,此密钥长度为："+hexKey.length());
		}
		byte[] key = HexUtil.decodeHex(hexKey.toCharArray());
		byte[] buffer;
		StringBuilder sb = new StringBuilder();
		try {
			/**
			 * 数据加密的方法： 1，用LD（1字节）表示明文数据的长度，在明文数据前加上LD产生新的数据块。
			 * 2，将此数据块分成8字节为单位的数据块，表示为block1、block2、block3、block4等，最后的数据块有可能是1-
			 * 8字节。
			 * 3，如果最后（或唯一）的数据块长度是8字节的话，转到第4步；如果不足8字节，则在其后加入十六进制数‘80’，如果达到8字节长度，则
			 * 转到第4步；否则在其后加入十六进制数‘00’直到长度达到8字节。 4，按照图所述的算法使用指定密钥对每一个数据块进行加密。
			 * 5，计算结束后，所有加密后的数据块按照顺序连接在一起。
			 */
			// 严格来说应该在数据块前面先加上1字节表示数据的长度
			// hexData=NumberUtil.format2Hex(hexData.length(),1)+hexData;

			hexData=fillLastBlock(hexData);
			//log.info("des数据补齐后="+hexData);
			for (int i = 0; i < hexData.length(); i = i + 16) {
				buffer = HexUtil.decodeHex(hexData.substring(i, i + 16).toCharArray());
				buffer = DesUtils.encrypt(buffer, key);
				sb.append(HexUtil.encodeHexStr(buffer));
			}
		} catch (Exception e) {
			log.error("单倍长密钥DEA加密算法出错了,key=" + hexKey + ",data=" + hexData, e);
			return null;
		}
		//log.info("单倍长des算法加密结果="+sb.toString());
		return sb.toString();
	}
	/**
	 * 单倍长密钥DEA解密算法
	 *
	 * @param hexKey
	 *            十六进制密钥(16字节)
	 * @param hexData
	 *            十六进制密文(长度为8字节倍数)
	 */
	public static String decodeDEA(String hexKey, String hexData) {
		// log.debug("key size="+hexKey.length());
		// log.debug("data size="+hexData.length());
		if (hexKey.length() != 16) {
			throw new RuntimeException("单倍长密钥DEA解密算法密钥必须为16位十六进制数,此密钥长度为："+hexKey.length());
		}
		byte[] key = HexUtil.decodeHex(hexKey.toCharArray());
		byte[] buffer;
		StringBuilder sb = new StringBuilder();
		try {
			for (int i = 0; i < hexData.length(); i = i + 16) {
				buffer = HexUtil.decodeHex(hexData.substring(i, i + 16).toCharArray());
				buffer = DesUtils.decrypt(buffer, key);
				sb.append(HexUtil.encodeHexStr(buffer));
			}
		} catch (Exception e) {
			log.error("单倍长密钥DEA解密算法出错了,key=" + hexKey + ",data=" + hexData, e);
			return null;
		}
		return sb.toString();
	}

	/**
	 * 双倍长密钥（16字节长）3DEA加密算法
	 *
	 * @param hexKey
	 *            16进制密钥(16字节32位长)
	 * @param hexData
	 *            16进制的明文数据
	 */
	public static String encode3DEA(String hexKey, String hexData) {

		if (hexKey.length() != 32) {
			throw new RuntimeException("双倍长密钥（16字节长）3DEA加密算法密钥必须为32位十六进制数,当前密钥长度为："+hexKey.length());
		}
		byte[] lkey = HexUtil.decodeHex(hexKey.substring(0, 16).toCharArray());
		byte[] rkey = HexUtil.decodeHex(hexKey.substring(16, 32).toCharArray());
		byte[] buffer;
		StringBuilder sb = new StringBuilder();
		try {
			/**
			 * 数据加密的方法： 1，用LD（1字节）表示明文数据的长度，在明文数据前加上LD产生新的数据块。
			 * 2，将此数据块分成8字节为单位的数据块，表示为block1、block2、block3、block4等，最后的数据块有可能是1-
			 * 8字节。
			 * 3，如果最后（或唯一）的数据块长度是8字节的话，转到第4步；如果不足8字节，则在其后加入十六进制数‘80’，如果达到8字节长度，则
			 * 转到第4步；否则在其后加入十六进制数‘00’直到长度达到8字节。 4，按照图所述的算法使用指定密钥对每一个数据块进行加密。
			 * 5，计算结束后，所有加密后的数据块按照顺序连接在一起。
			 */
			// 严格来说应该在数据块前面先加上1字节表示数据的长度
			// hexData=NumberUtil.format2Hex(hexData.length(),1)+hexData;
			hexData=fillLastBlock(hexData);
			for (int i = 0; i < hexData.length(); i = i + 16) {
				buffer = HexUtil.decodeHex(hexData.substring(i, i + 16).toCharArray());
				buffer = DesUtils.encrypt(buffer, lkey);
				buffer = DesUtils.decrypt(buffer, rkey);
				buffer = DesUtils.encrypt(buffer, lkey);
				sb.append(HexUtil.encodeHexStr(buffer));
			}
		} catch (Exception e) {
			log.error("双倍长密钥（16字节长）3DEA加密算法出错了,key=" + hexKey + ",data=" + hexData, e);
			return null;
		}
		String enc=sb.toString();
		//log.info("3des key="+hexKey+",hexData="+hexData+",encValue="+enc);
		return enc;
	}

	/**
	 * 双倍长密钥（16字节长）3DEA解密算法
	 *
	 * @param hexKey
	 *            十六进制密钥(32位16字节)
	 * @param hexData
	 *            十六进制密文(长度为8字节倍数)
	 */
	public static String decode3DEA(String hexKey, String hexData) {
		if (hexKey.length() != 32) {
			throw new RuntimeException("双倍长密钥（16字节长）3DEA解密算法密钥必须为32位十六进制数");
		}
		byte[] lkey = HexUtil.decodeHex(hexKey.substring(0, 16).toCharArray());
		byte[] rkey = HexUtil.decodeHex(hexKey.substring(16, 32).toCharArray());
		byte[] buffer;
		StringBuilder sb = new StringBuilder();
		try {
			for (int i = 0; i < hexData.length(); i = i + 16) {
				buffer = HexUtil.decodeHex(hexData.substring(i, i + 16).toCharArray());
				buffer = DesUtils.decrypt(buffer, lkey);
				buffer = DesUtils.encrypt(buffer, rkey);
				buffer = DesUtils.decrypt(buffer, lkey);
				sb.append(HexUtil.encodeHexStr(buffer));
			}
		} catch (Exception e) {
			log.error("双倍长密钥（16字节长）3DEA解密算法出错了,key=" + hexKey + ",data=" + hexData, e);
			return null;
		}
		return sb.toString();
	}

}

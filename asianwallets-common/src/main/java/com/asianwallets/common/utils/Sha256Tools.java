package com.asianwallets.common.utils;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @category：加密工具类
 * @author RyanCai
 * @日期：2014年11月28日 上午10:33:52
 */
public class Sha256Tools {

	/**
	 * 
	 * @category：加密算法
	 * @author RyanCai
	 * @Time :2014年11月28日 上午10:36:50
	 * @param strSrc
	 * @return
	 */
	public static String encrypt(String strSrc) {
		MessageDigest md = null;
		String strDes = null;
		String encName = "SHA-256";
		try {
			byte[] bt = strSrc.getBytes("UTF-8");
			md = MessageDigest.getInstance(encName);
			md.update(bt);
			strDes = bytes2Hex(md.digest()); // to HexString
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return strDes.replaceAll("\r|\n", "");
	}

	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

	public static void main(String[] args) {
		//System.out.println(Sha256Tools.encrypt("pay12345A07ABCDEFG123456789http://www.baidu.com228.00MYR192.168.13.237804444333322221111"));
		System.out.println(Sha256Tools.encrypt("Pa3yP7A2PAYDDhttp://192.168.124.60:9004http://192.168.124.60:9004100.00MYR192.168.124.64780"));
		//OXNvMWNvWnE2NVgwT0Q5bTFCbVhUbk1lOTVTQXYybEdTQW5PUHVMa2Nybzh6NmI2a05pa2JPS0xWOXNYTU92NEtHWERQbE02cTJ1YnlFZjNUTFFZSjFmNHFSc0Yyb05FdmtvallOZ3kwYnRZVGlNSFVwMzB4TWtJYVROeXM3NDFIMDAwMDAwMDAwNTQxLjAuMGVuLXVzMjAxNi8wMy8wNCAxNjowOTo0MzIwMTYwMzEwMTkwMjAwMDEyMDE2LTAzLTEwIDE5OjAzOjAxSEtEMTAwLjAwMTcuMjV3d3cuYmFpZHUuY29tQ0FJWVVOTE9OR1J5YW5DYWlBREQyMTQyMTEyMTE5ODkwMjAxNjMxNuW8oOS4iUNITuS4reWbveaUr+S7mOmAmjEyMDE2MDMxMTEwMjIyMDE2MDMxMTEwMjJVU0QxMDAwMDYyMjIwMjQwMDAwNjI2NDY5NTXkuK3lm73lt6XllYbpk7booYzkuK3lm73lt6XllYbpk7booYzmt7HlnLPopb/kuL3mlK/ooYwxMjAxNjAzMTExMDE255WZ5a2m57y06LS56Leo5aKD55WZ5a2mMTAwMDAxMDIwMzE=
		//OXNvMWNvWnE2NVgwT0Q5bTFCbVhUbk1lOTVTQXYybEdTQW5PUHVMa2Nybzh6NmI2a05pa2JPS0xWOXNYTU92NEtHWERQbE02cTJ1YnlFZjNUTFFZSjFmNHFSc0Yyb05FdmtvallOZ3kwYnRZVGlNSFVwMzB4TWtJYVROeXM3NDFIMDAwMDAwMDAwNTQxLjAuMGVuLXVzMjAxNi8wMy8wNCAxNjowOTo0MzIwMTYwMzEwMTkwMjAwMDEyMDE2LTAzLTEwIDE5OjAzOjAxSEtEMTAwLjAwMTcuMjV3d3cuYmFpZHUuY29tQ0FJWVVOTE9OR1J5YW5DYWlBREQyMTQyMTEyMTE5ODkwMjAxNjMxNuW8oOS4iUNITuS4reWbveaUr+S7mOmAmjEyMDE2MDMxMTEwMjIyMDE2MDMxMTEwMjJVU0QxMDAwMDYyMjIwMjQwMDAwNjI2NDY5NTXkuK3lm73lt6XllYbpk7booYzkuK3lm73lt6XllYbpk7booYzmt7HlnLPopb/kuL3mlK/ooYwxMjAxNjAzMTExMDE255WZ5a2m57y06LS56Leo5aKD55WZ5a2mMTAwMDAxMDIwMzE=
		//System.out.println(Sha256Tools.Encrypt("OXNvMWNvWnE2NVgwT0Q5bTFCbVhUbk1lOTVTQXYybEdTQW5PUHVMa2Nybzh6NmI2a05pa2JPS0xWOXNYTU92NEtHWERQbE02cTJ1YnlFZjNUTFFZSjFmNHFSc0Yyb05FdmtvallOZ3kwYnRZVGlNSFVwMzB4TWtJYVROeXM3NDFIMDAwMDAwMDAwNTQxLjAuMGVuLXVzMjAxNi8wMy8wNCAxNjowOTo0MzIwMTYwMzEwMTkwMjAwMDEyMDE2LTAzLTEwIDE5OjAzOjAxSEtEMTAwLjAwMTcuMjV3d3cuYmFpZHUuY29tQ0FJWVVOTE9OR1J5YW5DYWlBREQyMTQyMTEyMTE5ODkwMjAxNjMxNuW8oOS4iUNITuS4reWbveaUr+S7mOmAmjEyMDE2MDMxMTEwMjIyMDE2MDMxMTEwMjJVU0QxMDAwMDYyMjIwMjQwMDAwNjI2NDY5NTXkuK3lm73lt6XllYbpk7booYzkuK3lm73lt6XllYbpk7booYzmt7HlnLPopb/kuL3mlK/ooYwxMjAxNjAzMTExMDE255WZ5a2m57y06LS56Leo5aKD55WZ5a2mMTAwMDAxMDIwMzE="));
		// System.out.println(Sha256Tools.bytes2Hex("8D969EEF6ECAD3C29A3A629280E686CF0C3F5D5A86AFF3CA12020C923ADC6C92"));
		//System.out.println(Sha256Tools.Encrypt("OXNvMWNvWnE2NVgwT0Q5bTFCbVhUbk1lOTVTQXYybEdTQW5PUHVMa2Nybzh6NmI2a05pa2JPS0xWOXNYTU92NEtHWERQbE02cTJ1YnlFZjNUTFFZSjFmNHFSc0Yyb05FdmtvallOZ3kwYnRZVGlNSFVwMzB4TWtJYVROeXM3NDFIMDAwMDAwMDAwNTQxLjAuMGVuLXVzMjAxNi8wMy8wNCAxNjowOTo0MzIwMTYwMzEwMTkwMjAwMDEyMDE2LTAzLTEwIDE5OjAzOjAxSEtEMTAwLjAwMTcuMjV3d3cuYmFpZHUuY29tQ0FJWVVOTE9OR1J5YW5DYWlBREQyMTQyMTEyMTE5ODkwMjAxNjMxNuW8oOS4iUNITuS4reWbveaUr+S7mOmAmjEyMDE2MDMxMTEwMjIyMDE2MDMxMTEwMjJVU0QxMDAwMDYyMjIwMjQwMDAwNjI2NDY5NTXkuK3lm73lt6XllYbpk7booYzkuK3lm73lt6XllYbpk7booYzmt7HlnLPopb/kuL3mlK/ooYwxMjAxNjAzMTExMDE255WZ5a2m57y06LS56Leo5aKD55WZ5a2mMTAwMDAxMDIwMzE="));

	}
}

package io.etwallet.etpay.utils;

import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

public class MathUtils {

	/** 自定义进制（选择你想要的进制数，不能重复且最好不要0、1这些容易混淆的字符） */
	private static final char[] r = new char[] { '8', '2', '9', '7', '5', '3', '4', '6'};

	/** 定义一个字符用来补全邀请码长度（该字符前面是计算出来的邀请码，后面是用来补全用的） */
	private static final char b = '0';

	/** 进制长度 */
	private static final int binLen = r.length;

	/** 邀请码长度 */
	private static final int s = 6;

	/**
	 * 根据ID生成随机码
	 * 
	 * @param id
	 *            ID
	 * @return 随机码
	 */
	public static String inviteCode(long id) {
		char[] buf = new char[32];
		int charPos = 32;

		while ((id / binLen) > 0) {
			int ind = (int) (id % binLen);
			buf[--charPos] = r[ind];
			id /= binLen;
		}
		buf[--charPos] = r[(int) (id % binLen)];
		String str = new String(buf, charPos, (32 - charPos));
		// 不够长度的自动随机补全
		if (str.length() < s) {
			StringBuilder sb = new StringBuilder();
			sb.append(b);
			Random rnd = new Random();
			for (int i = 1; i < s - str.length(); i++) {
				sb.append(r[rnd.nextInt(binLen)]);
			}
			str += sb.toString();
		}
		return str;
	}

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
	
	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		}catch (Exception e) {
			return false;
		}
	}


	public static String MD5(String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(s.getBytes("utf-8"));
			return toHex(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String toHex(byte[] bytes) {
		final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
		StringBuilder ret = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
			ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
		}
		return ret.toString();
	}

	public static String phoneRep(String phone) {
		return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
	}

	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	public static int randomCode() {
		return (int) ((Math.random()*9+1)*1000);
	}
	
	public static int random(int seed) {
		return (int) ((Math.random()*9+1)*seed);
	}

//	public static void main(String args []) {
//		System.out.println(inviteCode(1));
//	}
	
	public static String genRandomCode() {
		int maxNum = 36;
		int i;
		int count = 0;
		char[] str = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
				'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < 8) {
			i = Math.abs(r.nextInt(maxNum));
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		return pwd.toString();
	}
}

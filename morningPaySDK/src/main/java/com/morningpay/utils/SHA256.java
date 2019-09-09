package com.morningpay.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author fgy
 *
 */
public class SHA256 {
      //   SECRET KEY
//private final static String secret_key = "ndE2jdZNFixH9G6Aidsfyf7lYT3PxW";
  /**
  * 将加密后的字节数组转换成字符串
  *
  * @param b 字节数组
  * @return 字符串
  */
 private static String byteArrayToHexString(byte[] b) {
      StringBuilder hs = new StringBuilder();
      String stmp;
  for (int n = 0; b!=null && n < b.length; n++) {
         stmp = Integer.toHexString(b[n] & 0XFF);
         if (stmp.length() == 1)
         hs.append('0');
         hs.append(stmp);
    }
   return hs.toString().toLowerCase();
 }
 /**
  * sha256_HMAC加密
  * @param message 消息
  * @param secret  秘钥
  * @return 加密后字符串
  */
 public static String getSha256(String message, String secret) {
   String hash = "";
   try {
       Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
       SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
       sha256_HMAC.init(secret_key);
       byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
       hash = byteArrayToHexString(bytes);
//       System.out.println(hash);
       } catch (Exception e) {
//       System.out.println("Error HmacSHA256 ===========" + e.getMessage());
     }
    return hash;
   }
	  public static void main(String[] args) {
		  String key = "fc27af70-8a02-4759-a72e-f51d9f0857b2";
		 String sign ="POST|/wallet/transfer|EOS|kpd1uhb1ulsj|0.0001|";
		 System.out.print(getSha256(sign,key));
	  }
 }

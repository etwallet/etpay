package com.morningpay.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author abill
 *
 */
public class DataUtils {
	public static String buildDataString(Map<String, String> param) {
		Set<String> keys = param.keySet();
		Object[] keyArray = keys.toArray();
		Arrays.sort(keyArray);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keyArray.length; i++) {
			String key = keyArray[i].toString();
			if (key.equalsIgnoreCase("sign")) {
				continue;
			}
			if(key.equalsIgnoreCase("rsaSign")) {
				continue;
			}
			if (i != 0) {
				sb.append("&");
			}
			sb.append(key);
			sb.append("=");
			String value = param.get(key);
			sb.append(value);
		}
		String data = sb.toString();
		return data;
	}
}

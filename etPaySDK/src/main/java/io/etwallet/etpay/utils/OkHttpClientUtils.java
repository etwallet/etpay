package io.etwallet.etpay.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 
 * @author abill
 *
 */
public class OkHttpClientUtils {
	private OkHttpClientUtils() {
	}

	private static OkHttpClient instance = null;

	public static OkHttpClient okHttps() {
		if (instance == null)
			instance = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS)
					.writeTimeout(30, TimeUnit.SECONDS).build();
		return instance;
	}
}

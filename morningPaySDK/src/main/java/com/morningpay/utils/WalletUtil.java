package com.morningpay.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.morningpay.config.Config;
import com.morningpay.form.WithdrawForm;
import com.morningpay.pojo.NoticeObj;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WalletUtil {
	public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
	public final static String BTC = "BTC";
	public final static String ETH = "ETH";
	public final static String EOS = "EOS";
	public final static String USDT = "USDT";
	public final static int BIG_FORMAT_8 = 8;
	
	/**
	 * 添加自定义冷钱包地址
	 * @param coinType 币类型
	 * @param address 地址
	 * @return
	 */
	public static String initConfig(String walletconfigJson) {
		try {
			walletconfigJson = walletconfigJson.trim();
			Map<String, String> dataMap = new HashMap<>();

			dataMap.put("appKey", Config.APP_KEY);
			dataMap.put("data", walletconfigJson);

			String dataString = DataUtils.buildDataString(dataMap);
			String sign = SHA256.getSha256(dataString, Config.SECRET_KEY);
			dataMap.put("sign", sign);

			RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, JSONObject.toJSONString(dataMap));

			Request request = new Request.Builder().url(Config.URL + "api/init").post(body).build();
			Response response = OkHttpClientUtils.okHttps().newCall(request).execute();
			String res = response.body().string();
			response.close();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 添加自定义冷钱包地址
	 * @param coinType 币类型
	 * @param address 地址
	 * @return
	 */
	public static String addSelfDefineAddress(String coinType, String address) {
		try {
			Map<String, String> dataMap = new HashMap<>();

			dataMap.put("appKey", Config.APP_KEY);
			dataMap.put("coinType", coinType);
			dataMap.put("address", address);

			String dataString = DataUtils.buildDataString(dataMap);
			String sign = SHA256.getSha256(dataString, Config.SECRET_KEY);
			dataMap.put("sign", sign);

			RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, JSONObject.toJSONString(dataMap));

			Request request = new Request.Builder().url(Config.URL + "api/addDefineAddress").post(body).build();
			Response response = OkHttpClientUtils.okHttps().newCall(request).execute();
			String res = response.body().string();
			response.close();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取冷钱包分配子地址。需要配了冷钱包公钥才可以
	 * @param coinType 币类型
	 * @return
	 */
	public static String getAddress(String coinType) {
		try {
			Map<String, String> dataMap = new HashMap<>();

			dataMap.put("appKey", Config.APP_KEY);
			dataMap.put("coinType", coinType);

			String dataString = DataUtils.buildDataString(dataMap);
			String sign = SHA256.getSha256(dataString, Config.SECRET_KEY);
			dataMap.put("sign", sign);

			RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, JSONObject.toJSONString(dataMap));

			Request request = new Request.Builder().url(Config.URL + "api/getAddress").post(body).build();
			Response response = OkHttpClientUtils.okHttps().newCall(request).execute();
			String res = response.body().string();
			response.close();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 提现
	 * @param withdrawFrom 
	 * @return
	 */
	public static String withdraw(WithdrawForm withdrawFrom) {
		String coinType = withdrawFrom.getCoinName();
		String serial_number = withdrawFrom.getSerial_number();
		String qty = withdrawFrom.getAmount().setScale(8, BigDecimal.ROUND_FLOOR).toPlainString();

		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("appKey", Config.APP_KEY);
		dataMap.put("coinType", coinType);
		dataMap.put("to", withdrawFrom.getAddress());
		dataMap.put("quantity", qty);
		dataMap.put("serialNumber", serial_number);
		dataMap.put("memo", withdrawFrom.getMemo());
		dataMap.put("contractAddress", "");
		dataMap.put("symbol", "");
		dataMap.put("feeCost", "");
		dataMap.put("gasLimit", "");
		dataMap.put("gasPrice", "");

		if (withdrawFrom.getCoinName().equals(WalletUtil.BTC)) {
			String btcFeeS = Config.btcFee.setScale(WalletUtil.BIG_FORMAT_8, BigDecimal.ROUND_FLOOR).toPlainString();
			dataMap.put("feeCost", btcFeeS);
		}
		if (withdrawFrom.getCoinName().equals(WalletUtil.USDT)) {
			String usdtFeeS = Config.usdtFee.setScale(WalletUtil.BIG_FORMAT_8, BigDecimal.ROUND_FLOOR).toPlainString();
			dataMap.put("feeCost", usdtFeeS);
		}
		if (withdrawFrom.getCoinName().startsWith(WalletUtil.ETH)) {
			String ethGaslimitS = Config.ethGaslimit.setScale(WalletUtil.BIG_FORMAT_8, BigDecimal.ROUND_FLOOR)
					.toPlainString();
			String ethGaspriceS = Config.ethGasprice.setScale(WalletUtil.BIG_FORMAT_8, BigDecimal.ROUND_FLOOR)
					.toPlainString();
			dataMap.put("gasLimit", ethGaslimitS);
			dataMap.put("gasPrice", ethGaspriceS);
		}

		try {
			String dataString = DataUtils.buildDataString(dataMap);

//			byte[] sourcepri_pub = RSATOOL.messageToByte(String.valueOf(dataString.hashCode()));
			byte[] sourcepri_pub = RSATOOL.messageToByte(MathUtils.MD5(dataString));
			// System.out.println( "signStr.hashCode() = " + signStr.hashCode() );
			byte[] signpri_pub = RSATOOL.encryptByRSA1(RSATOOL.strToByte(Config.HOT_WALLET_PRIVATE_KEY), sourcepri_pub);
			// System.out.println( "WalletUtil.priKey = " + WalletUtil.priKey );
			String rsaValue = RSATOOL.byteToStr(signpri_pub);
			// System.out.println( "rsaValue = " + rsaValue );
			rsaValue = rsaValue.replaceAll("\r\n", "");
			rsaValue = rsaValue.replaceAll("\r", "");
			rsaValue = rsaValue.replaceAll("\n", "");

			dataMap.put("rsaSign", rsaValue);
			String sign = SHA256.getSha256(dataString, Config.SECRET_KEY);
			dataMap.put("sign", sign);

			RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, JSONObject.toJSONString(dataMap));

			Request request = new Request.Builder().url(Config.URL + "api/withdraw").post(body).build();
			Response response = OkHttpClientUtils.okHttps().newCall(request).execute();
			String res = response.body().string();
			response.close();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取冷钱包记录
	 * @param coinType 币类型
	 * @param page 第几页
	 * @return
	 */
	public static String queryColdWalletRecord(String coinType, String page) {
		try {
			Map<String, String> dataMap = new HashMap<>();

			dataMap.put("appKey", Config.APP_KEY);
			dataMap.put("coinType", coinType);
			dataMap.put("page", page);

			String dataString = DataUtils.buildDataString(dataMap);
			String sign = SHA256.getSha256(dataString, Config.SECRET_KEY);
			dataMap.put("sign", sign);

			RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, JSONObject.toJSONString(dataMap));

			Request request = new Request.Builder().url(Config.URL + "api/queryColdRecord").post(body).build();
			Response response = OkHttpClientUtils.okHttps().newCall(request).execute();
			String res = response.body().string();
			response.close();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取提现记录
	 * @param coinType 币类型
	 * @param page 第几页
	 * @return
	 */
	public static String queryWithdrawRecord(String coinType, String page) {
		try {
			Map<String, String> dataMap = new HashMap<>();

			dataMap.put("appKey", Config.APP_KEY);
			dataMap.put("coinType", coinType);
			dataMap.put("page", page);

			String dataString = DataUtils.buildDataString(dataMap);
			String sign = SHA256.getSha256(dataString, Config.SECRET_KEY);
			dataMap.put("sign", sign);

			RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, JSONObject.toJSONString(dataMap));

			Request request = new Request.Builder().url(Config.URL + "api/queryWithdrawRecord").post(body).build();
			Response response = OkHttpClientUtils.okHttps().newCall(request).execute();
			String res = response.body().string();
			response.close();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 开通一个币
	 * @param coinType Coin Type
	 * @param rsaPubKey 热钱包转账公钥。初始化过的，传空字符串即可
	 * @param chainCode 冷钱包chain Code。不需要初始化冷钱包是，传空字符串即可
	 * @param coldPubKey 冷钱包公钥，不需要初始化冷钱包，传空字符串即可
	 * @param account EOS币时用到，冷钱包EOS收币地址，不是EOS公链，传空字符串即可
	 * @return
	 */
	public static String openCoinType(String coinType, String rsaPubKey, String chainCode, String coldPubKey, String account) {
		try {
			Map<String, String> dataMap = new HashMap<>();

			dataMap.put("appKey", Config.APP_KEY);
			dataMap.put("coinType", coinType);
			dataMap.put("rsaPubKey", rsaPubKey);
			dataMap.put("chainCode", chainCode);
			dataMap.put("coldPubKey", coldPubKey);
			dataMap.put("account", account);

			String dataString = DataUtils.buildDataString(dataMap);
			String sign = SHA256.getSha256(dataString, Config.SECRET_KEY);
			dataMap.put("sign", sign);

			RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, JSONObject.toJSONString(dataMap));

			Request request = new Request.Builder().url(Config.URL + "api/openCoinType").post(body).build();
			Response response = OkHttpClientUtils.okHttps().newCall(request).execute();
			String res = response.body().string();
			response.close();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 回调接口
	 * @param params 通知过来的post body数据
	 * @return
	 */
	public boolean callback(String params) {
		if (params == null) {
			return false;
		}
		JSONObject jo = JSONObject.parseObject(params);
		Map<String, String> dataMap = new HashMap<>();
		for(String key : jo.keySet()) {
			dataMap.put(key, jo.getString(key));
		}
		
		String dataString = DataUtils.buildDataString(dataMap);
		String sign = dataMap.get("sign");
		String calculateSign = SHA256.getSha256(dataString, Config.SECRET_KEY);
		if(sign.equalsIgnoreCase(calculateSign)) {
			//验签不通过
			return false;
		}
		
		NoticeObj noticeObj = com.alibaba.fastjson.JSON.parseObject(params, NoticeObj.class);
		

		//判定是什么币
		if (noticeObj.getCoinType().equals("BTC")) {
			//比特币
		} else if (noticeObj.getCoinType().equals("USDT")) {
			//OMNI USDT
		}else if (noticeObj.getCoinType().startsWith("ETH")) {
			//ETH或者ERC 20代币
		}
		
		// 冷钱包回调操作
		if (noticeObj.getType().equals("cold") && noticeObj.getDirection().equals("in")) {
			//TODO:这里写冷钱包入账逻辑
		} else if (noticeObj.getType().equals("withdraw")) {
			//TODO:这里写提现成功后逻辑
		}
		return false;
	}
}

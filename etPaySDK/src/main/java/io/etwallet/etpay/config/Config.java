package io.etwallet.etpay.config;

import java.math.BigDecimal;

public class Config {
	/**
	 * 接口请求地址
	 */
	 public final static String URL = "http://api.morningpay.io/api/"; //正式环境
//	public final static String URL = "http://192.168.1.188:8888/api/"; // 测试
	/**
	 * 通信app key， 平台分配
	 */
	public final static String APP_KEY = "xxxxxxxxxxxxxxxxxxx";
	/**
	 * 通信secret key， 平台分配，注意保密
	 */
	public final static String SECRET_KEY = "xxxxxxxxxxxxxxxxxxxxxx";
	
//	/**
//	 * 通信app key， 平台分配
//	 */
//	public final static String APP_KEY = "b93c3006-fc8a-4148-948e-617e48f8f10c";
//	/**
//	 * 通信secret key， 平台分配，注意保密
//	 */
//	public final static String SECRET_KEY = "d3cc3a02-4c97-4f12-80b6-f03163d8d967";
	/**
	 * 热钱包转账时用到的通信私钥，商户使用工具类生成，并自行做好保存，注意保密
	 */
	public final static String HOT_WALLET_PRIVATE_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
	/**
	 * 热钱包转账时用到，商户使用工具类生成。并提供给平台配置好
	 */
	public final static String HOT_WALLET_PUBIC_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

	/**
	 * 比特币矿工费, 根据自身情况设置
	 */
	public final static BigDecimal btcFee = BigDecimal.valueOf(0.00002000);
	/**
	 * Omni USDT矿工费
	 */
	public final static BigDecimal usdtFee = BigDecimal.valueOf(0.00002000);
	/**
	 * ERC 20系列代币，gas价格，可查最近区块知道价格
	 */
	public final static BigDecimal ethGasprice = BigDecimal.valueOf(0.00000004);
	/**
	 * ERC 20系列代币，最高可消耗gas量
	 */
	public final static BigDecimal ethGaslimit = BigDecimal.valueOf(66000.00000000);
	
}

package com.morningpay.config;

import java.math.BigDecimal;

public class Config {
	/**
	 * 接口请求地址
	 */
	// public final static String URL = "http://api.morningpay.io/api/"; //正式环境
	public final static String URL = "http://192.168.1.188:8888/api/"; // 测试
	/**
	 * 通信app key， 平台分配
	 */
	public final static String APP_KEY = "9c6a9e04-a344-407f-ac69-7e3e687ef7ef";
	/**
	 * 通信secret key， 平台分配，注意保密
	 */
	public final static String SECRET_KEY = "337588b3-f663-4455-81b8-6618e501b3b6";
	
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
	public final static String HOT_WALLET_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALjEDnGhoRq+TonkQQqHggMdmfr6nAgqkE+WbTghp8MSBQh2NBVvPGm3/62aiZDADPQlK0yr5mAv4CwWLpTjX7lgj9A3ZLJBCUgfrmMeo+7xLK0fGhrNdhzEHVKhy1x+gUUcKzre+cUrjt2IrUTmFDqAWAWarTTB1LH1IUgDsxq5AgMBAAECgYAHbFEMPaskUOsE3TfDvYf6qhDKpZDpPxSHLgr7S1bbHnk3StBy4AI0WSEbj27hcuXSVT/1F57s1F+URdvfPXdWAKtrKWZRB0WG3F6rtFYg9La//AC3j+fIWa7kCSH+bsxUNj/4TpEBvw1Z57sGjqN8JaHROqcRb0txx0IR612PlQJBAPgnWfvVRCby74TKT3Sn/6n4uAPL+OPNMyMZxXF2aUJLHNMN8IHSeTGcnbD2W1QBUAB6lIe1NOr+7ZgOvihYP+8CQQC+m56ybEoSD4b+G8fsTzrvuhBB1hlL03kwTcNwf8XAwXu/bC9hNODTb0nAe94Ifi3E/0pU9RL1LodVZ/v1qCfXAkEAqi7KnlPPDmsS6l2YiidgxZ+GC0yKXyfuBwd4ieysGUPL+84Wf+HpLaX7203Iql7QD9QBIuSZ6wNzzBFoReDnzwJBAIgr3UBSpMgqV6KKblWgdNZ28s5WClRljBq6M3nXjouarrGetGqZwFTNGL9uvr0Kh+BJEe2H+Dc5Om4Fj7d8npkCQDUsdLjn30Crjb2J9EGNNSG+jg/qsEAx5Fd5zVsLukCYcgVQ14iqL/hY85YJc53kW0T1Ay5i1daJM+X52lVJVfU=";
	/**
	 * 热钱包转账时用到，商户使用工具类生成。并提供给平台配置好
	 */
	public final static String HOT_WALLET_PUBIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4xA5xoaEavk6J5EEKh4IDHZn6+pwIKpBPlm04IafDEgUIdjQVbzxpt/+tmomQwAz0JStMq+ZgL+AsFi6U41+5YI/QN2SyQQlIH65jHqPu8SytHxoazXYcxB1SoctcfoFFHCs63vnFK47diK1E5hQ6gFgFmq00wdSx9SFIA7MauQIDAQAB";

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

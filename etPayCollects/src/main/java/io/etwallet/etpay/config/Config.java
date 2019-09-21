package io.etwallet.etpay.config;

import java.math.BigDecimal;

public class Config {
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
	public final static BigDecimal ethGasprice = BigDecimal.valueOf(0.00000008);
	/**
	 * ERC 20系列代币，最高可消耗gas量
	 */
	public final static BigDecimal ethGaslimit = BigDecimal.valueOf(66000);
	
}

package io.etwallet.etpay.enumc;

import com.google.common.collect.ImmutableMap;


import java.math.BigDecimal;

import org.bitcoinj.core.ECKey;

/**
 * 将货币的额度进行内部与外部之前转换。
 * 内部使用long进行存储，外部使用double类型。
 * <p>
 * Created by liqinqin on 2017/7/10.
 */
public class CurrencyWrapper {
	
	ECKey te;
	
    // TODO(ALL): 如果新增币种，加入配置。不放在配置文件中，是因为如果配置被篡改会影响数据的正确性。
    // 保证数字货币钱包中的数量单位与internalValue单位一致。
    private static final ImmutableMap<Currency, Integer> DECIMALS = ImmutableMap.of(
            Currency.BTC, 8,
            Currency.ETH, 8,
            Currency.CNY, 2);
    private BigDecimal externalValue;
    private long internalValue;

    public CurrencyWrapper(Currency currency, double external) {
        externalValue = BigDecimal.valueOf(external).setScale(DECIMALS.get(currency), BigDecimal.ROUND_HALF_UP);
        internalValue = externalValue.scaleByPowerOfTen(DECIMALS.get(currency))
                .setScale(0, BigDecimal.ROUND_HALF_UP)
                .longValue();
    }

    public static CurrencyWrapper valueOf(Currency currency, double external) {
        return new CurrencyWrapper(currency, external);
    }

    public CurrencyWrapper(Currency currency, long internal) {
        internalValue = internal;
        externalValue = BigDecimal.valueOf(internal, DECIMALS.get(currency));
    }

    public static CurrencyWrapper valueOf(Currency currency, long internal) {
        return new CurrencyWrapper(currency, internal);
    }

    public static int getDecimal(Currency currency) {
        return DECIMALS.get(currency);
    }

//    public static boolean isCrypto(Currency currency) {
//        return currency.getNumber() >= Currency.BTC_VALUE;
//    }

    public long longValue() {
        return internalValue;
    }

    public double doubleValue() {
        return externalValue.doubleValue();
    }

    public BigDecimal bigDecimal() {
        return externalValue;
    }

    /**
     * 在下单的时候进行数量转换
     * 如数量为12345，最小单位为100，转换后结果为12300。
     *
     * @param unit 交易数量最小单位
     * @return
     */
    public long longValue(long unit) {
        return internalValue - (internalValue % unit);
    }
}

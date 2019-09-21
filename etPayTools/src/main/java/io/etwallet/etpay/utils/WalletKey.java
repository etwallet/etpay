package io.etwallet.etpay.utils;

import lombok.Data;

/*
 钱包秘钥配置 项目方需要保存好不可以泄露
 */
@Data
public class WalletKey {
    String rsaPubKey;
    String rsaPrivateKey;
    String mnemonicCode;
    String createTime;
    String userName;
    KeyObj btcHotKeyObj;
    KeyObj btcColdKeyObj;
    KeyObj usdtHotKeyObj;
    KeyObj usdtColdKeyObj;
    KeyObj ethHotKeyObj;
    KeyObj ethColdKeyObj;
    KeyObj eosHotKeyObj;
    KeyObj eosColdKeyObj;
}

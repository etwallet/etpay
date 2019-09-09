package com.moringpay.utils;

import lombok.Data;

@Data
public class WalletConfig {

    public WalletConfig(WalletKey wkey) {
        userName = wkey.getUserName();
        usdtHotConfigObj = wkey.getUsdtHotKeyObj().GetKeyConfigObj();
        usdtColdConfigObj =  wkey.getUsdtColdKeyObj().GetKeyConfigObj();
        btcHotConfigObj = wkey.getBtcHotKeyObj().GetKeyConfigObj();
        btcColdConfigObj = wkey.getBtcColdKeyObj().GetKeyConfigObj();
        ethHotConfigObj = wkey.getEthHotKeyObj().GetKeyConfigObj();
        ethColdConfigObj = wkey.getEthColdKeyObj().GetKeyConfigObj();
        eosHotConfigObj = wkey.getEosHotKeyObj().GetKeyConfigObj();
        eosColdConfigObj = wkey.getEosColdKeyObj().GetKeyConfigObj();
        createTime = wkey.getCreateTime();
        rsaPubKey = wkey.getRsaPubKey();
    }

    String createTime;
    String userName;
    KeyConfigObj usdtHotConfigObj;
    KeyConfigObj usdtColdConfigObj;
    KeyConfigObj btcHotConfigObj;
    KeyConfigObj btcColdConfigObj;
    KeyConfigObj ethHotConfigObj;
    KeyConfigObj ethColdConfigObj;
    KeyConfigObj eosHotConfigObj;
    KeyConfigObj eosColdConfigObj;
    String rsaPubKey;

}

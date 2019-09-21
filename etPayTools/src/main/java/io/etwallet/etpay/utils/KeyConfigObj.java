package io.etwallet.etpay.utils;

import lombok.Data;

/*
 给sasa 配置用 没有秘钥可以公开
 */
@Data
public class KeyConfigObj{
    public KeyConfigObj(String pubKey, String chainCode, String address0,String path) {
        this.pubKey = pubKey;
        this.chainCode = chainCode;
        this.address0 = address0;
        this.path = path;
    }

    String pubKey;
    String chainCode;
    String addressSelf;
    String path;
    String address0;
}
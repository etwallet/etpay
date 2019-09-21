/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.etwallet.etpay.service;

import java.util.HashMap;
import java.util.Map;

import io.etwallet.etpay.config.Config;
import io.etwallet.etpay.pojo.CoinType;
import io.etwallet.etpay.utils.EthTokenUtils;
import io.etwallet.etpay.utils.EthUtils;

import java.math.BigDecimal;

/**
 *
 * @author abill
 */
public class CollectsAssetService {

    public static Map<String, String> needProcessAddress = new HashMap<>();

    public void collects(String mnemonicCode, int addressIndex, CoinType coinType, String toAddress, BigDecimal minQty) throws Exception {
        if (coinType.getName().startsWith("ETH_")) {
            String address0 = EthUtils.getAddress(mnemonicCode, 0, 0);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!地址0: " + address0);
            EthTokenUtils.collectsToken(mnemonicCode, addressIndex, coinType.getContract(), coinType.getDecimal(), toAddress, minQty, Config.ethGasprice, Config.ethGaslimit);
        } else if(coinType.getName().equalsIgnoreCase("ETH")){
            EthUtils.collects(mnemonicCode, addressIndex, toAddress, minQty, Config.ethGasprice, Config.ethGaslimit);
        } else {
            throw new Exception(coinType.getName() + "暂不支持归集");
        }
    }

    public void transferFee(String mnemonicCode, int addressIndex, CoinType coinType, String toAddress, BigDecimal minQty) throws Exception {
        if (coinType.getName().startsWith("ETH_")) {
            String address0 = EthUtils.getAddress(mnemonicCode, 0, 0);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!地址0: " + address0);
            EthTokenUtils.transferFee(mnemonicCode, addressIndex, coinType.getContract(), coinType.getDecimal(), toAddress, minQty, Config.ethGasprice, Config.ethGaslimit);
        } else {
            throw new Exception(coinType.getName() + "暂不支持归集");
        }
    }
}

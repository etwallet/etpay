package com.moringpay.utils;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;

public class Parameter {
	private boolean  isTest = false;
	enum Currency{
		BTC,
		ETH,
		LTC,
	}
    public NetworkParameters getParameters(String  coinType) {
        switch (coinType) {
            case "BTC":
                return !isTest ? MainNetParams.get() : RegTestParams.get();

            default:
                return null;
        }
    }
}

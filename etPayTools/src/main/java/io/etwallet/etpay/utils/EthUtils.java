package io.etwallet.etpay.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;

public class EthUtils {
	public static String getPrivateKeyByMnemonicCode(String mnemonicCode, int childNumber) {
		List<String> codeList = new ArrayList<>();
		String[] words = StringUtils.split(mnemonicCode, " ");
		codeList = Arrays.asList(words);
		DeterministicKey deterministicKey = HDKeyDerivation.createMasterPrivateKey(MnemonicCode.toSeed(codeList, ""));
		DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(60).derive(0),
				ChildNumber.ZERO);
		String privateKey = Utils.HEX.encode(HDKeyDerivation.deriveChildKey(addressKey, childNumber).getSecretBytes());
		return privateKey;
	}
//
//	public static void main(String[] args) {
//		String privateKey = EthUtils.getPrivateKeyByMnemonicCode(
//				"illegal purity topple train mixed boost machine bachelor pear deny live solid", 0);
//		byte[] secretBytes = Utils.HEX.decode(privateKey);
//		System.out.println(privateKey);
//
//	}
}

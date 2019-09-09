package com.moringpay.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.ethereum.crypto.ECKey;
import org.spongycastle.math.ec.ECPoint;

import io.eblock.eos4j.utils.Base58;
import io.eblock.eos4j.utils.ByteUtils;
import io.eblock.eos4j.utils.Ripemd160;
import io.eblock.eos4j.utils.Sha;

public class EosUtils {
	public static String getPrivateKeyByMnemonicCode(String mnemonicCode, int childNumber) {
		String[] words = StringUtils.split(mnemonicCode, " ");
		List<String> codeList = Arrays.asList(words);
		
		DeterministicKey deterministicKey = HDKeyDerivation.createMasterPrivateKey(MnemonicCode.toSeed(codeList, ""));
		DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(194).derive(1),
				ChildNumber.ZERO);

		byte[] a = { (byte) 0x80 };
		byte[] b = HDKeyDerivation.deriveChildKey(addressKey, childNumber).getSecretBytes();
		byte[] private_key = ByteUtils.concat(a, b);
		byte[] checksum = Sha.SHA256(private_key);
		checksum = Sha.SHA256(checksum);
		byte[] check = ByteUtils.copy(checksum, 0, 4);
		byte[] pk = ByteUtils.concat(private_key, check);
		return Base58.encode(pk);
	}
	
	public static String getEosPublicKey(String mnemonicCode, int childNumber) {
		String[] words = StringUtils.split(mnemonicCode, " ");
		List<String> codeList = Arrays.asList(words);
		
		DeterministicKey deterministicKey = HDKeyDerivation.createMasterPrivateKey(MnemonicCode.toSeed(codeList, ""));
		DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(194).derive(1),
				ChildNumber.ZERO);
		
		ECKey  ecKey =ECKey.fromPublicOnly(HDKeyDerivation.deriveChildKey(addressKey, childNumber).getPubKey());
   	 	ECPoint te = ecKey.getPubKeyPoint();
        byte[] pub_buf = te.getEncoded();
        byte[] csum = Ripemd160.from(pub_buf).bytes();
        csum = ByteUtils.copy(csum, 0, 4);
        byte[] addy = ByteUtils.concat(pub_buf, csum);
        StringBuffer bf = new StringBuffer("EOS");
        bf.append(Base58.encode(addy));
        
        return bf.toString();
    }

	public static void main(String[] args) {
//		String mnemonicCode = "illegal purity topple train mixed boost machine bachelor pear deny live solid";
//
//
//		String privateKey = EosUtils.getPrivateKeyByMnemonicCode(mnemonicCode, 2);
//		System.out.println("EOS private key: "+ privateKey);
//		
//		String publicKey = EosUtils.getEosPublicKey(mnemonicCode,2);
//		System.out.println("EOS public key: "+ publicKey);
	}
}

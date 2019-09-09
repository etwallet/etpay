package com.moringpay.utils;

import com.alibaba.fastjson.JSONObject;
import io.eblock.eos4j.utils.Base58;
import io.eblock.eos4j.utils.ByteUtils;
import io.eblock.eos4j.utils.Ripemd160;
import io.eblock.eos4j.utils.Sha;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.DeterministicSeed;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.spongycastle.math.ec.ECPoint;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.moringpay.utils.RSATOOL.*;
import static java.time.OffsetDateTime.now;

/**
 * 
 * @author abill
 *
 */
public class HdKeyManager {
    private final DeterministicKey addressKey;

    public HdKeyManager(DeterministicKey addressKey) {
        this.addressKey = addressKey;
    }
    public  DeterministicKey GetSlefAddress()
    {
        return this.addressKey;
    }
    public  org.bitcoinj.core.ECKey  getPrivateBtcECKey()
    {
       return org.bitcoinj.core.ECKey.fromPrivate(addressKey.getPrivKeyBytes());
    }

    public static HdKeyManager createPrivateKey(String chainCode, String privateKey) {
        return new HdKeyManager(HDKeyDerivation.createMasterPrivKeyFromBytes(Utils.HEX.decode(privateKey), Utils.HEX.decode(chainCode)));
    }

    public static HdKeyManager createPubKey(String chainCode, String pubKey) {
        return new HdKeyManager(HDKeyDerivation.createMasterPubKeyFromBytes(Utils.HEX.decode(pubKey), Utils.HEX.decode(chainCode)));
    }

    public String getAddress(int childNumber, NetworkParameters parameters) {
        return HDKeyDerivation.deriveChildKey(addressKey, childNumber).toAddress(parameters).toBase58();
    }

    public String getAddress(int childNumber) {
        return "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(HDKeyDerivation.deriveChildKey(addressKey, childNumber).getPubKey()).getAddress());
    }

    public String getAddress(int childNumber,String coinType) {

        if(coinType.startsWith( "ETH" ))
        {
            return "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(HDKeyDerivation.deriveChildKey(addressKey, childNumber).getPubKey()).getAddress());
        }else if(coinType.equals( "BTC" )||coinType.equals( "USDT" ))
        {
            return getAddress(childNumber,new Parameter().getParameters("BTC"));
        }
        return  null;

    }

    public String getPublicHash(int childNumber) {
        return "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(HDKeyDerivation.deriveChildKey(addressKey, childNumber).getPubKey()).getAddress());
    }

    public String getEosAddress(int childNumber) {
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
    
    public String GetEosPraviteKey(int childNumber)
    {
		byte[] a = { (byte) 0x80 };
		byte[] b = HDKeyDerivation.deriveChildKey(addressKey, childNumber).getSecretBytes();
		byte[] private_key = ByteUtils.concat(a, b);
		byte[] checksum = Sha.SHA256(private_key);
		checksum = Sha.SHA256(checksum);
		byte[] check = ByteUtils.copy(checksum, 0, 4);
		byte[] pk = ByteUtils.concat(private_key, check);
		return Base58.encode(pk);
    }
    
    public static String getEosPraviteKey(DeterministicKey addressKey, int childNumber)
    {
		byte[] a = { (byte) 0x80 };
		byte[] b = HDKeyDerivation.deriveChildKey(addressKey, childNumber).getSecretBytes();
		byte[] private_key = ByteUtils.concat(a, b);
		byte[] checksum = Sha.SHA256(private_key);
		checksum = Sha.SHA256(checksum);
		byte[] check = ByteUtils.copy(checksum, 0, 4);
		byte[] pk = ByteUtils.concat(private_key, check);
		return Base58.encode(pk);
    }

    public byte[] getPrivateKey(int childNumber) {
        return HDKeyDerivation.deriveChildKey(addressKey, childNumber).getSecretBytes();
    }

    public void sign(Transaction transaction, int childNumber) {
        ECKey ethPriKey = ECKey.fromPrivate(getPrivateKey(childNumber));
        transaction.sign(ethPriKey);
    }

    public static String genMnemonicCode() {

        DeterministicSeed seed = new DeterministicSeed(new SecureRandom(), DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS, "", System.currentTimeMillis());
        return StringUtils.join(seed.getMnemonicCode(), " ");
    }

    public static void generateAddressKey(String mnemonicCode,String user_name) {
        List<String> codeList = new ArrayList<>();
        for (String str : StringUtils.split(mnemonicCode, " ")) {
            codeList.add(str);
        }

        WalletKey walletKey = new WalletKey();
        walletKey.setMnemonicCode( mnemonicCode );
        walletKey.setUserName( user_name );

        DeterministicKey deterministicKey = HDKeyDerivation.createMasterPrivateKey(MnemonicCode.toSeed(codeList, ""));


        { // BTC
            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(0).derive(0), ChildNumber.ZERO);
            KeyObj keyObj = new KeyObj(addressKey,
                    HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(MainNetParams.get()).toBase58(),
                    addressKey.toAddress(MainNetParams.get() ).toBase58()
                    );

            if(keyObj.selfCheck()){
                walletKey.setBtcColdKeyObj( keyObj );
            }

        }
        { // BTC
            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(0).derive(1), ChildNumber.ZERO);
            KeyObj keyObj = new KeyObj(
                    addressKey,
                    HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(MainNetParams.get()).toBase58(),
                    addressKey.toAddress(MainNetParams.get() ).toBase58()
                    );
            if(keyObj.selfCheck()){
                walletKey.setBtcHotKeyObj( keyObj );
            }

        }
        { // USDT
            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(0).derive(3), ChildNumber.ZERO);
            KeyObj keyObj = new KeyObj(addressKey,
                    HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(MainNetParams.get()).toBase58(),
                    addressKey.toAddress(MainNetParams.get() ).toBase58());
            if(keyObj.selfCheck()){
                walletKey.setUsdtColdKeyObj(  keyObj );
            }
        }
        { // USDT
            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(0).derive(4), ChildNumber.ZERO);
            KeyObj keyObj = new KeyObj(addressKey,
                    HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(MainNetParams.get()).toBase58(),
                    addressKey.toAddress(MainNetParams.get() ).toBase58());


            if(keyObj.selfCheck()){
                walletKey.setUsdtHotKeyObj( keyObj );
            }
        }

        {//ETH
            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(60).derive(0), ChildNumber.ZERO);
            KeyObj keyObj = new KeyObj(addressKey,
                    HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(MainNetParams.get()).toBase58(),
                    addressKey.toAddress(MainNetParams.get() ).toBase58());

            if(keyObj.selfCheck()){
                walletKey.setEthColdKeyObj( keyObj );
            }

        }
        {//ETH
            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(60).derive(1), ChildNumber.ZERO);


            KeyObj keyObj = new KeyObj(addressKey,
                    HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(MainNetParams.get()).toBase58(),
                    addressKey.toAddress(MainNetParams.get() ).toBase58());

            if(keyObj.selfCheck()){
                walletKey.setEthHotKeyObj( keyObj );
            }
        }
        
        {//EOS
            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(194).derive(0), ChildNumber.ZERO);
            KeyObj keyObj = new KeyObj(addressKey,
                    HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(MainNetParams.get()).toBase58(),
                    addressKey.toAddress(MainNetParams.get() ).toBase58());

            if(keyObj.selfCheck()){
                walletKey.setEosColdKeyObj( keyObj );
            }

        }
        {//EOS
            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(194).derive(1), ChildNumber.ZERO);


            KeyObj keyObj = new KeyObj(addressKey,
                    HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(MainNetParams.get()).toBase58(),
                    addressKey.toAddress(MainNetParams.get() ).toBase58());

            if(keyObj.selfCheck()){
                walletKey.setEosHotKeyObj( keyObj );
            }
            
           String eosPraviteKey =  getEosPraviteKey(addressKey, 2);
           System.out.println("eosPraviteKey: "+eosPraviteKey);
        }

        Map<String, String> stringStringMap = CreateRsaKey();

        walletKey.setRsaPrivateKey( stringStringMap.get( "priKey" ) );
        walletKey.setRsaPubKey( stringStringMap.get( "pubKey" ) );

        WalletConfig walletConfig = new WalletConfig( walletKey );

        System.out.println("这个是需要项目方保存好  , walletKey: \r\n "+JSONObject.toJSON( walletKey ).toString());
        System.out.println("这个没有私钥 需要用来初始化配置, walletconfig: \r\n"+JSONObject.toJSON( walletConfig ).toString()  );
        
        
        

    }


    private static  String getDatafromFile(String Path) {

           BufferedReader reader = null;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }
}

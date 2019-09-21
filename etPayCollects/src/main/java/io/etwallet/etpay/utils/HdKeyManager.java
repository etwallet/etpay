package io.etwallet.etpay.utils;

import com.alibaba.fastjson.JSONObject;
import io.eblock.eos4j.utils.Base58;
import io.eblock.eos4j.utils.ByteUtils;
import io.eblock.eos4j.utils.Ripemd160;
import io.eblock.eos4j.utils.Sha;
import lombok.Data;
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

import static java.time.OffsetDateTime.now;

public class HdKeyManager {
    private static final String MNEMONIC_CODE = "misery crime model tape okay finish abstract icon outdoor master multiply churn"; // for test
//    BIP44_ETH_ACCOUNT_ZERO_PATH
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

    public static void generateAddressKey(String mnemonicCode) {
        List<String> codeList = new ArrayList<>();
        for (String str : StringUtils.split(mnemonicCode, " ")) {
            codeList.add(str);
        }
        JSONObject jsonObject = new JSONObject(  );

        jsonObject.put( "mnemonicCode",mnemonicCode);
        jsonObject.put( "create_time",now());

        DeterministicKey deterministicKey = HDKeyDerivation.createMasterPrivateKey(MnemonicCode.toSeed(codeList, ""));

//        DeterministicKey rootKey = HDKeyDerivation.createMasterPrivateKey(MnemonicCode.toSeed(codeList, ""));
//        DeterministicHierarchy hierarchy = new DeterministicHierarchy(rootKey);

        { // BTC
            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(0).derive(0), ChildNumber.ZERO);
//            DeterministicKey deterministicKey1 = hierarchy.deriveChild( HDUtils.parsePath( "M/44H/0H/0H" ), false, true, new ChildNumber( 0, false ) );
//
//            System.out.println( deterministicKey1.getPubKey() );
//            System.out.println(Utils.HEX.encode(deterministicKey1.getSecretBytes()));
//            System.out.println(HDUtils.formatPath(deterministicKey1.getPath()));
//            System.out.println(Utils.HEX.encode(deterministicKey1.getChainCode()));

            KeyHex keyHex = new KeyHex(addressKey,
                    HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(MainNetParams.get()).toBase58(),
                    addressKey.toAddress(MainNetParams.get() ).toBase58()
                    );
            HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
            HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);
            if (!pubKey.getAddress(0, MainNetParams.get()).equals(keyHex.address0)
                    || !privateKey.getAddress(0, MainNetParams.get()).equals(keyHex.address0)) {
                System.out.println("Error-----------------------------------------------------.");
                return;
            }
            jsonObject.put("btc-cold-address-key",JSONObject.toJSON( keyHex ));
        }
        { // BTC
            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(0).derive(1), ChildNumber.ZERO);
            KeyHex keyHex = new KeyHex(
                    addressKey,
                    HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(MainNetParams.get()).toBase58(),
                    addressKey.toAddress(MainNetParams.get() ).toBase58()
                    );
            HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
            HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);

            if (!pubKey.getAddress(0, MainNetParams.get()).equals(keyHex.address0)
                    || !privateKey.getAddress(0, MainNetParams.get()).equals(keyHex.address0)) {
                System.out.println("Error-----------------------------------------------------.");
                return;
            }
            jsonObject.put("btc-hot-address-key",JSONObject.toJSON( keyHex ));
        }
        { // USDT
            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(0).derive(3), ChildNumber.ZERO);
            KeyHex keyHex = new KeyHex(addressKey,
                    HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(MainNetParams.get()).toBase58(),
                    addressKey.toAddress(MainNetParams.get() ).toBase58());
            HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
            HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);//
            // 生成子地址
            System.out.println( pubKey.getAddress(0, MainNetParams.get()) );
            System.out.println( pubKey.getAddress(1, MainNetParams.get()) );
            System.out.println( pubKey.getAddress(2, MainNetParams.get()) );
            System.out.println( pubKey.getAddress(3, MainNetParams.get()) );
            System.out.println( pubKey.getAddress(4, MainNetParams.get()) );
            System.out.println( pubKey.getAddress(5, MainNetParams.get()) );
            //生成子私钥
            System.out.println( Utils.HEX.encode(privateKey.getPrivateKey( 0 )));
            System.out.println( Utils.HEX.encode(privateKey.getPrivateKey( 1 )));
            System.out.println( Utils.HEX.encode(privateKey.getPrivateKey( 2 )));
            System.out.println( Utils.HEX.encode(privateKey.getPrivateKey( 3 )));
            System.out.println( Utils.HEX.encode(privateKey.getPrivateKey( 4 )));
            System.out.println( Utils.HEX.encode(privateKey.getPrivateKey( 5 )));

            if (!pubKey.getAddress(0, MainNetParams.get()).equals(keyHex.address0)
                    || !privateKey.getAddress(0, MainNetParams.get()).equals(keyHex.address0)) {
                System.out.println("Error-----------------------------------------------------.");
                return;
            }
            jsonObject.put("usdt-cold-address-key",JSONObject.toJSON( keyHex ));
        }
        { // USDT
            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(0).derive(4), ChildNumber.ZERO);
            KeyHex keyHex = new KeyHex(addressKey,
                    HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(MainNetParams.get()).toBase58(),
                    addressKey.toAddress(MainNetParams.get() ).toBase58());
            HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);

            HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);
          

            if (!pubKey.getAddress(0, MainNetParams.get()).equals(keyHex.address0)
                    || !privateKey.getAddress(0, MainNetParams.get()).equals(keyHex.address0)) {
                System.out.println("Error-----------------------------------------------------.");
                return;
            }

//            DeterministicKey deterministicKey1 = HDKeyDerivation.deriveChildKey( privateKey.GetSlefAddress(), 7 );
            jsonObject.put("usdt-hot-address-key",JSONObject.toJSON( keyHex ));
        }

        { // ETH
            {
                DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(60).derive(0), ChildNumber.ZERO);
                String address0 = "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(HDKeyDerivation.deriveChildKey(addressKey, 0).getPubKey()).getAddress());
                String slefAddress = "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(addressKey.getPubKey()).getAddress());
                KeyHex keyHex = new KeyHex(addressKey, address0,slefAddress);
                HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
                HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);
                if (!pubKey.getAddress(0).equals(keyHex.address0)
                        || !privateKey.getAddress(0).equals(keyHex.address0)) {
                    System.out.println("Error-----------------------------------------------------.");
                    return;
                }
                jsonObject.put("eth-cold-address-key",JSONObject.toJSON( keyHex ));
            }
            {
                DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(60).derive(1), ChildNumber.ZERO);
                String address0 = "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(HDKeyDerivation.deriveChildKey(addressKey, 0).getPubKey()).getAddress());
                String slefAddress = "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(addressKey.getPubKey()).getAddress());
                KeyHex keyHex = new KeyHex(addressKey, address0,slefAddress);
                HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
                HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);
                if (!pubKey.getAddress(0).equals(keyHex.address0)
                        || !privateKey.getAddress(0).equals(keyHex.address0)) {
                    System.out.println("Error-----------------------------------------------------.");
                    return;
                }
                jsonObject.put("eth-hot-address-key",JSONObject.toJSON( keyHex ));
            }
        }
        System.out.println(jsonObject.toJSONString());

    }

//    public static void generateTestAddressKey(String mnemonicCode) {
//        List<String> codeList = new ArrayList<>();
//        for (String str : StringUtils.split(mnemonicCode, " ")) {
//            codeList.add(str);
//        }
//        DeterministicKey deterministicKey = HDKeyDerivation.createMasterPrivateKey(MnemonicCode.toSeed(codeList, ""));
//        { // BTC DeterministicKey{pub=03e2a944cfddcebed94fdb3e0a7e52b3f74c7bf692b580d863a9af29709ab1bd4e,
//        	//chainCode=664762128e81ef375e097d950e4ff4b7b2108f66ed199501504b0a3a4ed95ce7,
//        	//path=M, creationTimeSeconds=1544531201, isEncrypted=false, isPubKeyOnly=false}
//            System.out.println("btc-cold-address-key:");
//            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(0).derive(0), ChildNumber.ZERO);
//            KeyHex keyHex = new KeyHex(addressKey, HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(RegTestParams.get()).toBase58());
//            HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
//            HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);
//            if (!pubKey.getAddress(0, RegTestParams.get()).equals(keyHex.address0)
//                    || !privateKey.getAddress(0, RegTestParams.get()).equals(keyHex.address0)) {
//                System.out.println("Error-----------------------------------------------------.");
//                return;
//            }
//            System.out.println(keyHex);
//        }
//        { // BTC DeterministicKey{pub=03e2a944cfddcebed94fdb3e0a7e52b3f74c7bf692b580d863a9af29709ab1bd4e,
//            //chainCode=664762128e81ef375e097d950e4ff4b7b2108f66ed199501504b0a3a4ed95ce7,
//            //path=M, creationTimeSeconds=1544531201, isEncrypted=false, isPubKeyOnly=false}
//            System.out.println("btc-hot-address-key:");
//            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(0).derive(1), ChildNumber.ZERO);
//            KeyHex keyHex = new KeyHex(addressKey, HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(RegTestParams.get()).toBase58());
//            HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
//            HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);
//            if (!pubKey.getAddress(0, RegTestParams.get()).equals(keyHex.address0)
//                    || !privateKey.getAddress(0, RegTestParams.get()).equals(keyHex.address0)) {
//                System.out.println("Error-----------------------------------------------------.");
//                return;
//            }
//            System.out.println(keyHex);
//        }
//        { // LTC
//            System.out.println("\nltc-cold-address-key:");
//            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(2).derive(0), ChildNumber.ZERO);
//            KeyHex keyHex = new KeyHex(addressKey, HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(RegTestParams.get()).toBase58());
//            HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
//            HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);
//            if (!pubKey.getAddress(0, RegTestParams.get()).equals(keyHex.address0)
//                    || !privateKey.getAddress(0, RegTestParams.get()).equals(keyHex.address0)) {
//                System.out.println("Error-----------------------------------------------------.");
//                return;
//            }
//            System.out.println(keyHex);
//        }
//        { // ETH
//            {
//                System.out.println("\neth-cold-address-key:");
//                DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(60).derive(0), ChildNumber.ZERO);
//                String address0 = "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(HDKeyDerivation.deriveChildKey(addressKey, 0).getPubKey()).getAddress());
//                KeyHex keyHex = new KeyHex(addressKey, address0);
//                HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
//                HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);
//                if (!pubKey.getAddress(0).equals(keyHex.address0)
//                        || !privateKey.getAddress(0).equals(keyHex.address0)) {
//                    System.out.println("Error-----------------------------------------------------.");
//                    return;
//                }
//                System.out.println(keyHex);
//            }
//            {
//                System.out.println("\neth-hot-address-key:");
//                DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(60).derive(1), ChildNumber.ZERO);
//                String address0 = "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(HDKeyDerivation.deriveChildKey(addressKey, 0).getPubKey()).getAddress());
//                KeyHex keyHex = new KeyHex(addressKey, address0);
//                HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
//                HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);
//                if (!pubKey.getAddress(0).equals(keyHex.address0)
//                        || !privateKey.getAddress(0).equals(keyHex.address0)) {
//                    System.out.println("Error-----------------------------------------------------.");
//                    return;
//                }
//                System.out.println(keyHex);
//                System.out.println("address0-prikey: " + Utils.HEX.encode(privateKey.getPrivateKey(0)));
//            }
//        }
//        { // OSC
//            {
//                System.out.println("\nosc-cold-address-key:");
//                DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(999).derive(0), ChildNumber.ZERO);
//                String address0 = "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(HDKeyDerivation.deriveChildKey(addressKey, 0).getPubKey()).getAddress());
//                KeyHex keyHex = new KeyHex(addressKey, address0);
//                HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
//                HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);
//                if (!pubKey.getAddress(0).equals(keyHex.address0)
//                        || !privateKey.getAddress(0).equals(keyHex.address0)) {
//                    System.out.println("Error-----------------------------------------------------.");
//                    return;
//                }
//                System.out.println(keyHex);
//            }
//            {
//                System.out.println("\nosc-hot-address-key:");
//                DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(999).derive(1), ChildNumber.ZERO);
//                String address0 = "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(HDKeyDerivation.deriveChildKey(addressKey, 0).getPubKey()).getAddress());
//                KeyHex keyHex = new KeyHex(addressKey, address0);
//                HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
//                HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);
//                if (!pubKey.getAddress(0).equals(keyHex.address0)
//                        || !privateKey.getAddress(0).equals(keyHex.address0)) {
//                    System.out.println("Error-----------------------------------------------------.");
//                    return;
//                }
//                System.out.println(keyHex);
//                System.out.println("address0-prikey: " + Utils.HEX.encode(privateKey.getPrivateKey(0)));
//            }
//        }
//
//        { // EOS
//            {
//                System.out.println("\neos-cold-address-key:");
//                DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(194).derive(0), ChildNumber.ZERO);
//                String address0 = "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(HDKeyDerivation.deriveChildKey(addressKey, 0).getPubKey()).getAddress());
//                KeyHex keyHex = new KeyHex(addressKey, address0);
//                HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
//                HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);
//                if (!pubKey.getAddress(0).equals(keyHex.address0)
//                        || !privateKey.getAddress(0).equals(keyHex.address0)) {
//                    System.out.println("Error-----------------------------------------------------.");
//                    return;
//                }
//                System.out.println(keyHex);
//            }
//            {
//                System.out.println("\neos-hot-address-key:");
//                DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(194).derive(1), ChildNumber.ZERO);
//                String address0 = "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(HDKeyDerivation.deriveChildKey(addressKey, 0).getPubKey()).getAddress());
//                KeyHex keyHex = new KeyHex(addressKey, address0);
//                HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
//                HdKeyManager pubKey = HdKeyManager.createPubKey(keyHex.chainCode, keyHex.pubKey);
//                if (!pubKey.getAddress(0).equals(keyHex.address0)
//                        || !privateKey.getAddress(0).equals(keyHex.address0)) {
//                    System.out.println("Error-----------------------------------------------------.");
//                    return;
//                }
//                System.out.println(keyHex);
//                System.out.println("address0-prikey: " + Utils.HEX.encode(privateKey.getPrivateKey(0)));
//            }
//        }
//    }
    @Data
    public static class KeyHex {
        public String pubKey;
        public String privateKey;
        public String chainCode;
        public String addressSelf;
        public String address0;
        public String privateKey0;
        public String path;


        public KeyHex(DeterministicKey addressKey, String address0,String addressself) {
            this.addressSelf = addressself;
            this.pubKey = Utils.HEX.encode(addressKey.getPubKey());
            this.privateKey = Utils.HEX.encode(addressKey.getSecretBytes());
            this.path = HDUtils.formatPath(addressKey.getPath());
            this.chainCode = Utils.HEX.encode(addressKey.getChainCode());
            this.address0 = address0;
            this.privateKey0 = Utils.HEX.encode(HDKeyDerivation.deriveChildKey(addressKey, 0).getSecretBytes());
        }

        @Override
        public String toString() {
            return JSONObject.toJSON( this ).toString();
        }
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

//    public static void main(String arg[]) throws Exception {
//
////        String s = getDatafromFile("D:\\work\\saas\\wallet_backend\\key.json");
////        JSONObject parse = (JSONObject)JSONObject.parse( s );
////        System.out.print(parse);
////        JSONObject usdtObj = parse.getJSONObject( "usdt-cold-address-key" );
////
////        HdKeyManager privateKey = HdKeyManager.createPrivateKey(
////                usdtObj.getString("chainCode" ),
////                usdtObj.getString("privateKey"));
////       String addr0 =  privateKey.getAddress(0,MainNetParams.get());
//
////        addressKey.getSecretBytes()
////        Utils.HEX.encode(privateKey.getSecretBytes())
////        System.out.println(addr0);
//
//
//
//        String mnemonicCode = genMnemonicCode();
//
//        System.out.println("mnemonicCode: " + mnemonicCode);
//        generateAddressKey(mnemonicCode);
////        generateAddressKey("picture beyond order arrow awake raccoon head sponsor endorse convince cereal orchard");
//
//
//
////        HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey)
////        HdKeyManager privateKey = HdKeyManager.createPrivateKey(keyHex.chainCode, keyHex.privateKey);
//////        generateAddressKey(arg[0]);
////
////
//////    	String chainCode = "25791ae37d5ba3affdb1d0c68c1f63c1b24e60a2d4b37e51962f61c7a1a72d51";
//////    	String pubKey = "027e80a9d1e5d355f008bdf6ddc40fd1f7d016de4c5fc94cdd63952e1d7fabd79c";
//////
//////    	HdKeyManager hdKeyManager = HdKeyManager.createPubKey(chainCode, pubKey);
//////    	byte[] s = hdKeyManager.getPrivateKey(10);
//////    	 System.out.println("s-----------------------------------------------------."+s);
//////        {pubKey:02a07f28dddbf3615065996416d702f2cc6a744e92962aac8a475616af62216570
//////        	privateKey:3a7f251b6f404bef1d7126bcb80d593b033df37d6f885fe1ccf65c73e1350d01
//////        	chainCode:9f9043f7363e99695d085274d795dea301c516024b4bc8c03a3d1def3da56a84
//////        	address0:0xf6dbdf7064a03b699e3bca85ffc9b75d42a22e8d}
////    	String chainCode = "02a07f28dddbf3615065996416d702f2cc6a744e92962aac8a475616af62216570";
//////     	                    9f9043f7363e99695d085274d795dea301c516024b4bc8c03a3d1def3da56a84
////    	String priKey = "02a07f28dddbf3615065996416d702f2cc6a744e92962aac8a475616af62216570";
////    	String pubKey = "02a07f28dddbf3615065996416d702f2cc6a744e92962aac8a475616af62216570";
//////    	                   02a07f28dddbf3615065996416d702f2cc6a744e92962aac8a475616af62216570
////    	HdKeyManager hdKeyManager = HdKeyManager.createPrivateKey(chainCode, priKey);
//////    	 hdKeyManager = HdKeyManager.createPubKey(chainCode, pubKey);
////    	String s = hdKeyManager.GetEosPraviteKey(1);
////    	String puk = hdKeyManager.getEosAddress(1);
////    //EOS7vCwCuYmZWEnJfHeBYE164p2thb58boXj4TWcwn71mpqdUUe72
////    	String pu = Ecc.privateToPublic(s);
////
////
////
////
////
////    	 System.out.println("s-----------------------------------------------------."+puk);
//    }
}

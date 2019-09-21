package io.etwallet.etpay.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.params.MainNetParams;

@Data
public class KeyObj {

     String pubKey;
     String privateKey;
     String chainCode;
     String addressSelf;
     String address0;
     String privateKey0;
     String path;
     String name;

    public KeyConfigObj GetKeyConfigObj(){
        return new KeyConfigObj(this.pubKey,this.chainCode,address0,path);
    }


    public KeyObj(DeterministicKey addressKey, String address0, String addressself) {
        this.addressSelf = addressself;
        this.pubKey = Utils.HEX.encode(addressKey.getPubKey());
        this.privateKey = Utils.HEX.encode(addressKey.getSecretBytes());
        this.path = HDUtils.formatPath(addressKey.getPath());
        this.chainCode = Utils.HEX.encode(addressKey.getChainCode());
        this.address0 = address0;
        this.privateKey0 = Utils.HEX.encode( HDKeyDerivation.deriveChildKey(addressKey, 0).getSecretBytes());
    }


    boolean selfCheck()
    {
        HdKeyManager privateKey = HdKeyManager.createPrivateKey( chainCode, this.privateKey);
        HdKeyManager pubKey = HdKeyManager.createPubKey( chainCode, this.pubKey);
        if (!pubKey.getAddress(0, MainNetParams.get()).equals( address0)
                || !privateKey.getAddress(0, MainNetParams.get()).equals( address0)) {
            System.out.println("Error-----------------------------------------------------.");
            return false;
        }
        return  true;
    }

    @Override
    public String toString() {
        return JSONObject.toJSON( this ).toString();
    }
}

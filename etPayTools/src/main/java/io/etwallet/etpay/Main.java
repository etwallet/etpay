package io.etwallet.etpay;

import io.etwallet.etpay.utils.HdKeyManager;

public class Main {

	//产生 秘钥
    public static void main(String arg[]) throws Exception {
        String mnemonicCode = HdKeyManager.genMnemonicCode();
        String user_name = "yourname";
        if(arg.length>0) {
        	user_name = arg[0];
        	System.out.println(arg[0]);
        }
        
        HdKeyManager.generateAddressKey(mnemonicCode,user_name);
    }

}

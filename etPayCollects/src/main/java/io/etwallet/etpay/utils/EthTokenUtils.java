package io.etwallet.etpay.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.bitcoinj.core.Utils;
import org.ethereum.core.Transaction;
import org.ethereum.jsonrpc.TypeConverter;


public class EthTokenUtils extends EthUtils {

    public static Transaction createEthTokenTx(String contract, int decimal, String fromAddress, String toAddress, BigDecimal qty, BigDecimal gasPrice, BigDecimal gasLimit) {
        //获取小数位数
        Transaction transaction = null;
        BigInteger value = qty.movePointRight(decimal).toBigInteger();
        try {
            EthRpc rpc = new EthRpc(RPC_URL, "", "");

            return rpc.getNonce(fromAddress).map(nonce -> {
                System.out.print("nonce: " + nonce.toString());
                try {
                    String data = "0xa9059cbb" + AddPreZero(filterHexHead(toAddress)) + AddPreZero(value.toString(16));

                    return new Transaction(TypeConverter.StringHexToByteArray(TypeConverter.toJsonHex(nonce)),
                            TypeConverter.StringNumberAsBytes(gasPrice.movePointRight(SCALE).toString()),
                            TypeConverter.StringNumberAsBytes(gasLimit.toString()),
                            TypeConverter.StringHexToByteArray(contract),
                            new byte[0],
                            TypeConverter.StringHexToByteArray(data), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).orElse(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print("transaction: " + transaction.toString());
        return transaction;
    }

    /**
     * 归集
     *
     * @param mnemonicCode
     * @param childNumber
     * @param contract
     * @param decimal
     * @param toAddress
     * @param gasPrice
     * @param gasLimit
     * @throws Exception
     */
    public static void collectsToken(String mnemonicCode, int childNumber, String contract, int decimal, String toAddress, BigDecimal minQty, BigDecimal gasPrice, BigDecimal gasLimit) throws Exception {
        EthRpc rpc = new EthRpc(RPC_URL, "", "");
        String fromAddress = getAddress(mnemonicCode, 0, childNumber);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!地址" + childNumber + ": " + fromAddress);
        BigDecimal ethBalance = rpc.getBalance(fromAddress);
        BigDecimal gasEth = gasPrice.multiply(gasLimit);
        BigInteger symbolBalance = rpc.getSymbolBalance(contract, fromAddress);
        BigDecimal symbolBalanceD = new BigDecimal(symbolBalance);
        symbolBalanceD = symbolBalanceD.movePointLeft(decimal);
        if (symbolBalanceD.compareTo(minQty) <= 0) {
            throw new Exception("地址：" + fromAddress + "余额("+symbolBalanceD+")不足，跳过");
        }
        if (ethBalance.compareTo(gasEth) < 0) {
            throw new Exception(fromAddress + "矿工费不足，跳过");
        }

        Transaction transaction = createEthTokenTx(contract, decimal, fromAddress, toAddress, symbolBalanceD, gasPrice, gasLimit);

        String privateKey = getPrivateKeyByMnemonicCode(mnemonicCode, 0, childNumber);
        transaction.sign(Utils.HEX.decode(privateKey));
        String jsHex = TypeConverter.toJsonHex(transaction.getEncoded());
        String txid = TypeConverter.toJsonHex(transaction.getHash());

        String txid2 = rpc.sendRawTransaction(jsHex).filter(id -> id.length() > 5).orElse(null);
        if (null == txid2 || !txid.equalsIgnoreCase(txid2)) {
            throw new Exception(fromAddress + "地址归集失败");
        } else {
            System.out.println(fromAddress + "地址归集成功");
        }
    }

    //前补0,补齐64位
    private static String AddPreZero(String s) {
        if (null != s && s.length() < 64) {
            String res = "0000000000000000000000000000000000000000000000000000000000000000";
            return res.substring(s.length()) + s;
        }
        return s;
    }

    private static String filterHexHead(String s) {
        if (null != s && s.startsWith("0x")) {
            return s.substring(2);
        }
        return s;
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

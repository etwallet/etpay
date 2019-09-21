package io.etwallet.etpay.utils;

import static io.etwallet.etpay.utils.EthTokenUtils.createEthTokenTx;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.params.MainNetParams;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.jsonrpc.TypeConverter;

public class EthUtils {

    protected static final String RPC_URL = "http://47.52.104.69:8888";
    protected static final int SCALE = 18;

    public static String getPrivateKeyByMnemonicCode(String mnemonicCode, int isHot, int childNumber) {
        String[] words = StringUtils.split(mnemonicCode, " ");
        List<String> codeList = Arrays.asList(words);
        DeterministicKey deterministicKey = HDKeyDerivation.createMasterPrivateKey(MnemonicCode.toSeed(codeList, ""));
        DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(60).derive(isHot),
                ChildNumber.ZERO);
        String privateKey = Utils.HEX.encode(HDKeyDerivation.deriveChildKey(addressKey, childNumber).getSecretBytes());
        return privateKey;
    }

    public static String getAddress(String mnemonicCode, int isHot, int childNumber) {
        String[] words = StringUtils.split(mnemonicCode, " ");
        List<String> codeList = Arrays.asList(words);
        DeterministicKey deterministicKey = HDKeyDerivation.createMasterPrivateKey(MnemonicCode.toSeed(codeList, ""));
        DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(deterministicKey.derive(44).derive(60).derive(isHot), ChildNumber.ZERO);

        HDKeyDerivation.deriveChildKey(addressKey, 0).toAddress(MainNetParams.get()).toBase58();

        return "0x" + Utils.HEX.encode(ECKey.fromPublicOnly(HDKeyDerivation.deriveChildKey(addressKey, childNumber).getPubKey()).getAddress());
    }

    public static Transaction createEthTx(String fromAddress, String toAddress, BigDecimal qty, BigDecimal gasPrice, BigDecimal gasLimit) {
        Transaction transaction = null;
        BigInteger value = qty.movePointRight(SCALE).toBigInteger();
        try {
            EthRpc rpc = new EthRpc(RPC_URL, "", "");
            return rpc.getNonce(fromAddress).map(nonce -> {
                try {
                    return new Transaction(TypeConverter.StringHexToByteArray(TypeConverter.toJsonHex(nonce)),
                            TypeConverter.StringNumberAsBytes(gasPrice.movePointRight(SCALE).toString()),
                            TypeConverter.StringNumberAsBytes(gasLimit.toString()),
                            TypeConverter.StringHexToByteArray(toAddress),
                            value.toByteArray(), null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).orElse(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * @param minQty
     * @param gasPrice
     * @param gasLimit
     * @throws Exception
     */
    public static void collects(String mnemonicCode, int childNumber, String toAddress, BigDecimal minQty, BigDecimal gasPrice, BigDecimal gasLimit) throws Exception {
        EthRpc rpc = new EthRpc(RPC_URL, "", "");
        String fromAddress = getAddress(mnemonicCode, 0, childNumber);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!地址" + childNumber + ": " + fromAddress);
        BigDecimal ethBalance = rpc.getBalance(fromAddress);
        BigDecimal gasEth = gasPrice.multiply(gasLimit);
        if (ethBalance.compareTo(gasEth.add(minQty)) < 0) {
            throw new Exception(fromAddress + "矿工费不足，跳过");
        }
        BigDecimal qty = ethBalance.subtract(gasEth);
        Transaction transaction = createEthTx(fromAddress, toAddress, qty, gasPrice, gasLimit);

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
    public static void transferFee(String mnemonicCode, int childNumber, String contract, int decimal, String toAddress, BigDecimal minQty, BigDecimal gasPrice, BigDecimal gasLimit) throws Exception {
        EthRpc rpc = new EthRpc(RPC_URL, "", "");
        String fromAddress = getAddress(mnemonicCode, 0, childNumber);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!地址" + childNumber + ": " + fromAddress);
        BigDecimal ethBalance = rpc.getBalance(fromAddress);
        BigDecimal gasEth = gasPrice.multiply(gasLimit);
        BigInteger symbolBalance = rpc.getSymbolBalance(contract, fromAddress);
        BigDecimal symbolBalanceD = new BigDecimal(symbolBalance);
        symbolBalanceD = symbolBalanceD.movePointLeft(decimal);
        if (symbolBalanceD.compareTo(minQty) < 0) {
            System.out.println("地址：" + fromAddress + "，余额("+symbolBalanceD+")小于" + minQty + "，跳过");
            throw new Exception("地址：" + fromAddress + "，余额("+symbolBalanceD+")小于" + minQty + "，跳过");
        }
        if (ethBalance.compareTo(gasEth) < 0) {
            String address0 = EthUtils.getAddress(mnemonicCode, 0, 0);
            BigDecimal address0EthBalance = rpc.getBalance(address0);
            if (address0EthBalance.compareTo(gasEth) <= 0) {
                System.out.println("地址0：" + address0 + "，ETH余额(" + address0EthBalance + ")不足，跳过");
                throw new Exception("地址0：" + address0 + "，ETH余额(" + address0EthBalance + ")不足，跳过");
            }

            Transaction transaction4Gas = createEthTx(address0, fromAddress, gasEth, gasPrice, gasLimit);
            String privateKey0 = getPrivateKeyByMnemonicCode(mnemonicCode, 0, 0);
            transaction4Gas.sign(Utils.HEX.decode(privateKey0));
            String jsHex4Gas = TypeConverter.toJsonHex(transaction4Gas.getEncoded());
            String txid4Gas = TypeConverter.toJsonHex(transaction4Gas.getHash());
            String txid24Gas = rpc.sendRawTransaction(jsHex4Gas).filter(id -> id.length() > 5).orElse(null);
            if (null == txid24Gas || !txid4Gas.equalsIgnoreCase(txid24Gas)) {
                throw new Exception(fromAddress + "地址转矿工费失败");
            } else {
                System.out.println(fromAddress + "地址转矿工费成功");
            }
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

//	public static void main(String[] args) {
////            String m = "illegal purity topple train mixed boost machine bachelor pear deny live solid";
////            
////            String address = EthUtils.getAddress(m, 0, 1);
//////		String privateKey = EthUtils.getPrivateKeyByMnemonicCode(
//////				"illegal purity topple train mixed boost machine bachelor pear deny live solid", 0);
//////		byte[] secretBytes = Utils.HEX.decode(privateKey);
////		System.out.println(address);
////
//	}
}

package io.etwallet.etpay.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.script.Script;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BtcColdToHotManager {
    private final HdKeyManager hdKeyManager;

    public BtcColdToHotManager(HdKeyManager hdKeyManager) {
        this.hdKeyManager = hdKeyManager;
    }




    public String createRawTransaction(TransactionObject transactionObject, NetworkParameters parameters) throws Exception {
        List<Unspent> unspentList = transactionObject.getUnspentList();
        List<Output> outputList = transactionObject.getOutputList();
        Transaction transaction = new Transaction(parameters);
        for (Output output : outputList) {
            transaction.addOutput(Coin.valueOf(output.getAmount()), Address.fromBase58(transaction.getParams(), output.getTo()));
        }

        for (Unspent unspent : unspentList) {
            ECKey ecKey = ECKey.fromPrivate(hdKeyManager.getPrivateKey(unspent.childNumber));
            Script scriptPubKey = new Script(Utils.HEX.decode(unspent.getScriptPubKey()));
            transaction.addInput(Sha256Hash.wrap(unspent.getTxid()), unspent.getVout(), new Script(Utils.HEX.decode(unspent.getScriptPubKey())))
                    .setScriptSig(scriptPubKey.createEmptyInputScript(ecKey, scriptPubKey));
        }
        int n = unspentList.size();
        for (int i = 0; i < n; i++) {
            ECKey ecKey = ECKey.fromPrivate(hdKeyManager.getPrivateKey(unspentList.get(i).childNumber));
            if (!hdKeyManager.getAddress(unspentList.get(i).childNumber, parameters).equals(unspentList.get(i).getAddress())) {
                System.out.println("error address is not equal");
                throw new Exception("error address is not equal");
            }
            Script inputScript = transaction.getInput(i).getScriptSig();
            Script script = new Script(Utils.HEX.decode(unspentList.get(i).getScriptPubKey()));
            TransactionSignature signature = transaction.calculateSignature(i, ecKey, script, Transaction.SigHash.ALL, false);
            inputScript = script.getScriptSigWithSignature(inputScript, signature.encodeToBitcoin(), 0);
            transaction.getInput(i).setScriptSig(inputScript);
        }

        return Utils.HEX.encode(transaction.bitcoinSerialize());
    }

    public Transaction omni_createRawTransaction(TransactionObject transactionObject, Script scriptOutput,NetworkParameters parameters) throws Exception {
        List<Unspent> unspentList = transactionObject.getUnspentList();
        List<Output> outputList = transactionObject.getOutputList();
        Transaction transaction = new Transaction(parameters);
        for (Output output : outputList) {
            transaction.addOutput(Coin.valueOf(output.getAmount()), Address.fromBase58(transaction.getParams(), output.getTo()));
        }

        transaction.addOutput(Coin.valueOf(0), scriptOutput);

        for (Unspent unspent : unspentList) {
            ECKey ecKey = ECKey.fromPrivate(hdKeyManager.getPrivateKey(unspent.childNumber));

            Script scriptPubKey = new Script(Utils.HEX.decode(unspent.getScriptPubKey()));
            transaction.addInput(Sha256Hash.wrap(unspent.getTxid()), unspent.getVout(), new Script(Utils.HEX.decode(unspent.getScriptPubKey())))
                    .setScriptSig(scriptPubKey.createEmptyInputScript(ecKey, scriptPubKey));
        }
        int n = unspentList.size();
        for (int i = 0; i < n; i++) {
            ECKey ecKey = ECKey.fromPrivate(hdKeyManager.getPrivateKey(unspentList.get(i).childNumber));
            if (!hdKeyManager.getAddress(unspentList.get(i).childNumber, parameters).equals(unspentList.get(i).getAddress())) {
                System.out.println("error address is not equal");
                throw new Exception("error address is not equal");
            }
            Script inputScript = transaction.getInput(i).getScriptSig();
            Script script = new Script(Utils.HEX.decode(unspentList.get(i).getScriptPubKey()));
            TransactionSignature signature = transaction.calculateSignature(i, ecKey, script, Transaction.SigHash.ALL, false);
            inputScript = script.getScriptSigWithSignature(inputScript, signature.encodeToBitcoin(), 0);
            transaction.getInput(i).setScriptSig(inputScript);
        }
       return transaction;

    }

    public static class Unspent {
        private String txid;
        private int vout;
        private String scriptPubKey;
        private int childNumber;
        private long amount; // 与Coin中的value单位一致
        private String address;

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public int getVout() {
            return vout;
        }

        public void setVout(int vout) {
            this.vout = vout;
        }

        public String getScriptPubKey() {
            return scriptPubKey;
        }

        public void setScriptPubKey(String scriptPubKey) {
            this.scriptPubKey = scriptPubKey;
        }

        public int getChildNumber() {
            return childNumber;
        }

        public void setChildNumber(int childNumber) {
            this.childNumber = childNumber;
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static class Output {
        private long amount; // 与Coin中的value单位一致
        private String to;

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }
    }

    public static class TransactionObject {
        private List<Unspent> unspentList = new ArrayList<>();
        private List<Output> outputList = new ArrayList<>();

        public List<Unspent> getUnspentList() {
            return unspentList;
        }

        public void setUnspentList(List<Unspent> unspentList) {
            this.unspentList = unspentList;
        }

        public List<Output> getOutputList() {
            return outputList;
        }

        public void setOutputList(List<Output> outputList) {
            this.outputList = outputList;
        }
    }

    public static void writeLog(String logPath, String log) {
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(logPath), true);
            outputStream.write(log.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void main(String argv[]) throws Exception {
//        Config config = ConfigFactory.load("cold-to-hot.conf");
//        String currency = argv[0];
//        if (argv.length < 3 || (!currency.equals("btc") && !currency.equals("ltc"))) {
//            System.out.println(" [argv[0] must be btc or ltc] [true[test] of false[live]] [file]");
//            return;
//        }
//        ObjectMapper mapper = new ObjectMapper();
//        boolean isTest = Boolean.valueOf(argv[1]);
//        TransactionObject transactionObject = mapper.readValue(new File(argv[2]), TransactionObject.class);
//        HdKeyManager hdKeyManager = HdKeyManager.createPrivateKey(config.getString(currency + ".chain-code"),
//                config.getString(currency + ".private-key"));
//        BtcColdToHotManager btcColdToHotManager = new BtcColdToHotManager(hdKeyManager);
//        String logPath = config.getString(currency + ".log-path");
//        if (currency.equals("btc")) {
//            String log = "\nbtc new Tx: " + btcColdToHotManager.createRawTransaction(transactionObject, !isTest ? MainNetParams.get() : RegTestParams.get()) + "\n";
//            System.out.println(log);
//            writeLog(logPath, mapper.writeValueAsString(transactionObject) + log);
//        } else {
//            String log = "\nltc new Tx: " + btcColdToHotManager.createRawTransaction(transactionObject, !isTest ? LtcMainNetParams.get() : RegTestParams.get()) + "\n";
//            System.out.println(log);
//            writeLog(logPath, mapper.writeValueAsString(transactionObject) + log);
//        }
//
//        /*TransactionObject transactionObject = new TransactionObject();
//        List<Unspent> unspentList = new ArrayList<>();
//        List<Output> outputList = new ArrayList<>();
//        transactionObject.setUnspentList(unspentList);
//        transactionObject.setOutputList(outputList);
//        {
//            Unspent unspent = new Unspent();
//            unspent.setAmount(50000000000L);
//            unspent.setChildNumber(9);
//            unspent.setScriptPubKey("76a91416fe238376b0af2fc3b2ad6c69f04261fb50aa8088ac");
//            unspent.setTxid("0e996f054dffdca62fa4d0420d3b5dab78324f4b7b00eb813d505bab7010de2b");
//            unspent.setVout(0);
//            unspent.setAddress("mhcXc9DSs7XQ9bPV6UgMeY19T5eUghdkuG");
//            unspentList.add(unspent);
//        }
//        {
//            Unspent unspent = new Unspent();
//            unspent.setAmount(1000000000L);
//            unspent.setChildNumber(13);
//            unspent.setScriptPubKey("76a9146abb6cc4b1ccacbe3fcb6038e34dd45f4d2b1d6488ac");
//            unspent.setTxid("af643e075f09f55e4dd6ea42d7c77c647fb84c01e9d1627b2eae00907214c63a");
//            unspent.setVout(1);
//            unspent.setAddress("mqFJStxvmWDhvUJkhAVyz6BffcAErAbUJH");
//            unspentList.add(unspent);
//        }
//        {
//            Output output = new Output();
//            output.setTo("ms3KfUvxK4ENN6MkYgM8UUW63tfpZjPrDz");
//            output.setAmount(50000000000L);
//            outputList.add(output);
//        }
//        {
//            Output output = new Output();
//            output.setTo("mn14v8T3UymKRboGsVRuszezMNbo4uBuQK");
//            output.setAmount(999900000L);
//            outputList.add(output);
//        }
//        ObjectMapper mapper = new ObjectMapper();
//        String json = mapper.writeValueAsString(transactionObject);
//        System.out.println(json);
//
//        privateKey:758ab8247a54198a726c798ab122af54914ac538e883c0b8b9ef9f3a3b828ddc
//chainCode:29a9d765f501a413512c77778ecee8c7d534523bf06f7b26a888f7f28beda5eb
//
//HdKeyManager hdKeyManager = HdKeyManager.createPrivateKey("29a9d765f501a413512c77778ecee8c7d534523bf06f7b26a888f7f28beda5eb",
//                "758ab8247a54198a726c798ab122af54914ac538e883c0b8b9ef9f3a3b828ddc");
//        BtcColdToHotManager btcColdToHotManager = new BtcColdToHotManager(hdKeyManager);
//        System.out.println("new Tx: " + btcColdToHotManager.createRawTransaction(transactionObject, RegTestParams.get()));
//         */
//        // System.out.println("new Tx: " + btcColdToHotManager.createRawTransaction(mapper.readValue(json, TransactionObject.class), RegTestParams.get()));
//    }

}

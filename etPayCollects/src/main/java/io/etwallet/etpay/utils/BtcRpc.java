package io.etwallet.etpay.utils;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.etwallet.etpay.enumc.Currency;
import io.etwallet.etpay.enumc.CurrencyWrapper;
import io.etwallet.etpay.pojo.AddressItem;



public class BtcRpc implements  BaseRpc{
    private static Logger logger = LoggerFactory.getLogger(BtcRpc.class);
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    public static final int CONNECT_TIMEOUT = 10;
    public static final int READ_TIMEOUT = 50;
    public static final int WRITE_TIMEOUT = 50;
    private URL url;
    private String authorization;
    private static OkHttpClient instance;



    public BtcRpc(String urlName, String user, String password) throws MalformedURLException {
        url = new URL(urlName);
        String strRPCUserColonPass = user + ":" + password;
        authorization = "Basic " + Base64.getEncoder().encodeToString(strRPCUserColonPass.getBytes());
    }

    private static synchronized OkHttpClient client() {
        if (instance == null) {
            instance = new OkHttpClient.Builder()
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .build();
        }
        return instance;
    }
    public boolean callBack(String json, String appkey, String signValue) throws Exception {
        try{
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Host", url.getHost())
                    .addHeader("Connection", "close")
                    .addHeader("x-appkey", appkey)
                    .addHeader("x-sign", signValue)
                    .post(body)
                    .build();
            Response response = client().newCall(request).execute();
            String res = response.body().string();
            response.close();
            JSONObject jsonObject = JSONObject.parseObject(res);
            String code = jsonObject.getString("code");

            if (response.code() == 200 && null != code && (code.equals( "200" ) || code.equals( "0" ))) {
                return true;
            } else {
                logger.info( "response err :" + response.toString() );
            }
            
        }catch (Exception e){
            logger.info( "callBack______________" + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    public String post(String json) throws Exception {
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Host", url.getHost())
                .addHeader("Connection", "close")
                .addHeader("Authorization", authorization)

                .post(body)
                .build();
        Response response = client().newCall(request).execute();
        String res = response.body().string();
        response.close();
        if (response.code() != 200) {
            throw new Exception(res);
        }
        return res;
    }
    private String get(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client().newCall(request).execute();
        String res = response.body().string();
        response.close();
        if (response.code() != 200) {
            throw new Exception(res);
        }
        return res;
    }

    public String getDataByBtcCom() {
        try {
            Thread.sleep(1000);//https://btc.com 每分钟支持120次访问
            String	 path = url + "";
            String res = get(path.toString());
            return res;
        } catch (Exception e) {
            logger.error("getDataByBtcCom exception: {}", e.getMessage());
            return "";
        }
    }

    public Optional<JSONArray> getAddressUtxos(String address) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "getaddressutxos")
                .put("id", Integer.valueOf(1));

        ObjectNode array1 = mapper.createObjectNode();
        array1.putArray("addresses").add( address );
        objectNode.putArray("params")
                .add(array1);
        try {
            String res = post(objectNode.toString());
            JSONObject jsonObject = JSONObject.parseObject(res);
            JSONArray result = jsonObject.getJSONArray( "result" );
            return Optional.of(result);
        } catch (Exception e) {
//            logger.error("getBestBlockHash exception: {}", e.getMessage());
            return Optional.empty();
        }
    }


    public Optional<String> getAddressBanlance(String address) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "getaddressbalance")
                .put("id", Integer.valueOf(1));

        ObjectNode array1 = mapper.createObjectNode();
        array1.putArray("addresses").add( address );
        objectNode.putArray("params")
                .add(array1);
        try {
            String res = post(objectNode.toString());
            String balance =  ((JSONObject)JSONObject.parse( res )).getJSONObject("result").getString( "balance" );
            return Optional.of(new BigDecimal( balance ).divide( new BigDecimal(100000000)).toString());
        } catch (Exception e) {
//            logger.error("getBestBlockHash exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> getBestBlockHash() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "getbestblockhash")
                .put("id", Integer.valueOf(1));
        try {
            String res = post(objectNode.toString());
            return Optional.of(mapper.readTree(res)
                    .get("result")
                    .asText());
        } catch (Exception e) {
//            logger.error("getBestBlockHash exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public boolean importAddress(String address) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "importaddress")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(address)
                .add("")
                .add(false);
        try {
            String res = post(objectNode.toString());
            return true;
        } catch (Exception e) {
            logger.error("importAddress exception: {}", e.getMessage());
            return false;
        }
    }

    public Optional<SinceBlock> listSinceBlock(String blockHash, int targetConfirms) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "listsinceblock")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(blockHash)
                .add(targetConfirms)
                .add(true);
        try {
            String res = post(objectNode.toString());
            return Optional.of(mapper.readValue(mapper.readTree(res)
                    .get("result")
                    .toString(), SinceBlock.class));
        } catch (Exception e) {
            logger.error("listSinceBlock exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * @param sendTo 转出地址、金额映射表。
     * @return 交易id
     */
    public Optional<String> sendMany(Map<String, Double> sendTo) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "sendmany")
                .put("id", Integer.valueOf(1));
        ObjectNode sendToNode = objectNode.putArray("params")
                .add("")
                .addObject();
        sendTo.forEach((address, amount) -> {
            sendToNode.put(address, amount);
        });
        try {
            String res = post(objectNode.toString());
            return Optional.of(mapper.readTree(res)
                    .get("result")
                    .asText());
        } catch (Exception e) {
            logger.error("sendMany exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<List<TxObject>> listTransactions(int limit, int offset) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "listtransactions")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add("*")
                .add(limit)
                .add(offset);
        try {
            String res = post(objectNode.toString());
            return Optional.of(mapper.readValue(mapper.readTree(res)
                    .get("result")
                    .toString(), mapper.getTypeFactory().constructParametricType(ArrayList.class, TxObject.class)));
        } catch (Exception e) {
            logger.error("listTransactions exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<List<Unspent>> listUnspent() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "listunspent")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(1)
                .add(99999999);
        try {
            String res = post(objectNode.toString());
            return Optional.of(mapper.readValue(mapper.readTree(res)
                    .get("result")
                    .toString(), mapper.getTypeFactory().constructParametricType(ArrayList.class, Unspent.class)));
        } catch (Exception e) {
            logger.error("ObjectMapper exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<WalletInfo> getWalletInfo() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "getwalletinfo")
                .put("id", Integer.valueOf(1));
        try {
            String res = post(objectNode.toString());
            return Optional.of(mapper.readValue(mapper.readTree(res)
                    .get("result")
                    .toString(), WalletInfo.class));
        } catch (Exception e) {
            logger.error("getWalletInfo exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public String getblockchaininfo() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "getblockchaininfo")
                .put("id", Integer.valueOf(1));
        try {
            String res = post(objectNode.toString());
//            return Optional.empty();
            return res;
        } catch (Exception e) {
            logger.error("getblockchaininfo exception: {}", e.getMessage());
            return "";
        }
    }

    public String getinfo() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "getinfo")
                .put("id", Integer.valueOf(1));
        try {
            String res = post(objectNode.toString());
//            return Optional.empty();
            return res;
        } catch (Exception e) {
            logger.error("getinfo exception: {}", e.getMessage());
            return "";
        }
    }

    public String getblockhash(Integer Height) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "getblockhash")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(Height);
        try {
            String res = post(objectNode.toString());
//            logger.info("getblockhash="+res);
            return res;
        } catch (Exception e) {
//            logger.error("getblockhash exception: {}", e.getMessage());
            return "";
        }
    }

    public String getblock(String blockHash) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "getblock")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
        .add(blockHash);
        try {
            String res = post(objectNode.toString());
//            logger.info("getblock="+res);
            return res;
//            return Optional.empty();
     
        } catch (Exception e) {
//            logger.error("getblock exception: {}", e.getMessage());
            return "";
        }
    }
    public String gettransaction(String txid) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "gettransaction")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
        .add(txid);
        try {
            String res = post(objectNode.toString());
            return res;
//            System.out.println(res);
//            return Optional.empty();
     
        } catch (Exception e) {
            logger.error("gettransaction exception: {}", e.getMessage());
            return "";
        }
    }

    public String getrawtransaction(String txid) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "getrawtransaction")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(txid)
                .add(1);

        try {
            String res = post(objectNode.toString());
            return res;
//            System.out.println(res);
//            return Optional.empty();

        } catch (Exception e) {
            logger.error("getrawtransaction exception: {}", e.getMessage());
            return "";
        }
    }
//    {"result":{"amount":-100.00000000,"fee":-0.00083600,"confirmations":133,"blockhash":"1bc10f94a6d3514768f73f993f7182dcf5a0fb3dd44e5e26e45dfcef0de4ace1","blockindex":3,"blocktime":1545991226,"txid":"707fa1639786d4b4a4d5096bbbdb48777583b786c28640a56a6a064ebccf9bbb","walletconflicts":[],"time":1545991079,"timereceived":1545991079,"bip125-replaceable":"no","details":[{"account":"","address":"mjobudJXcQahVDP5PkVsQgwW1uf6hwyUiX","category":"send","amount":-100.00000000,"vout":1,"fee":-0.00083600,"abandoned":false}],"hex":"0100000003fd9595ce9cae2b50705b585992e85922a644bf92792ae18f14cf12772d258217000000004847304402200df8adae8cd7d799d7ebbdece48f5f71e032e498df85204bbd38f1d53a8660c702207c4fa0e0549680ac8ed1aefdb1ca6f353b774d6cfebf91699f0b89fc9908cbda01feffffff0e7891dd0a26b7cdc0956505c5ec8beeab30944844200c3a2596180593695dde000000004948304502210083d061b7848f9b4542021a91abb071a6213301baab173d94a0a03752008ba26b02200de4aa997e207f87dc4864c329866522d34f827c2f379fee50d783567323da3901feffffff6e6b1f9bd2789c562467b958d5e523d9117b9aafe588fa9a8bc010c23d72f5610000000048473044022063fb55e7552ef8f8b91de28216883e200fca4552b88a0b841753ce97d8bb1d4a02201d0db4d62d15660601658fb76ef9d2814ca87bba3bc6d6576fa80701abe1862301feffffff0270ab042a010000001976a914104f72e501af337c4589a53ca73012ead7a641cd88ac00e40b54020000001976a9142f068e79ab7ab8668ac74ee3e4dc5df1544d602988acc8000000"},"error":null,"id":1}

//    {"result":{"amount":-100.00000000,"fee":-0.00084000,"confirmations":133,"blockhash":"1bc10f94a6d3514768f73f993f7182dcf5a0fb3dd44e5e26e45dfcef0de4ace1","blockindex":2,"blocktime":1545991226,"txid":"de02e618294579005f743393112eb2b4d5b3163d0d96593451ba70fb31f5ade2","walletconflicts":[],"time":1545812197,"timereceived":1545812197,"bip125-replaceable":"no","details":[{"account":"","address":"mpF9tkZNktRbi1c7wMnbyU5Tc64VJVJgLz","category":"send","amount":-100.00000000,"vout":1,"fee":-0.00084000,"abandoned":false}],"hex":"0100000003137d81a608beb807793154cb60a8f7016547b72d68261e89807aa1b4e3c666af0000000049483045022100feae48e89a0dd156fae9559c7b09fe3502fd6501d019f88d770a2ae1ed0542340220157e5c291451272d76f2a800acfcc457d1bb48afebd88bb03e97164fba2d955d01feffffff35e8d93cb9c861fe78793a44c9b637faeeca92bf5ddd20deee1a451d30736148000000004847304402204fab6312c57494e6f0efc693a8ebbef3f63d0b2bcc09e8aac7f4be911b535e010220787755f95058417d8473bb548656dfc1e592d9ae32a38b86f9230a1c60526fbd01feffffff46116e9f41a12e26a4f3e38fb5d5bc4f9749347320229f6d3771370372cbce7a000000004847304402206e6692499879ab885c2fa995c2af1a87709310e4d594fbdbca9495a22e50c66a022009f450ad7fe80efcb11d8fc61a543c10bcc0267f054973ca129dc99b1e06490b01feffffff02e0a9042a010000001976a9143442eb5b1a3556fb5358fd162d2dfa977eec367d88ac00e40b54020000001976a9145fbc288c09c05a0b75cba6ce775008485941a59188acc8000000"},"error":null,"id":1}
 
//    ["ce0c86b3731b5027c36e6b15361991bdf4d312121ba7d4fa85852141b7e0b782",
//     "5ecc23b05400419b1f227e13b16c52c8eb608a142ebff469cfa8f9d733fe9129",
//     "de02e618294579005f743393112eb2b4d5b3163d0d96593451ba70fb31f5ade2",
//     "707fa1639786d4b4a4d5096bbbdb48777583b786c28640a56a6a064ebccf9bbb"]
//0100000001d97aa98afd4d9196818ef37a5f061e5762ae79611ce2b8f0ccbef4466188d3b8000000006a473044022037b42356a9d62465fa7657e07a0823129a9af2bd1798b3986b8499a5d25ade6a02203edfd85e2e7b46e05a6477d7eec4cf8f21dabe4e3208031f12e4ad4d86b3c153012102fe79336524c10514ad8635060f3ded99ecf4f33db22069c7feb2789e2ab1a232ffffffff0318110200000000001976a9142e0c0094d2aac3f856560798606a9f5d97b660d688ac22020000000000001976a9148a0b924f324d3ed3c2acb2224206ea7af98c7cb888ac0000000000000000166a146f6d6e69000000000000001f0000000005f767a000000000
    public String sendrawtransaction(String tx){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "sendrawtransaction")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(tx);

        try {
            String res = post(objectNode.toString());
            return res;
        } catch (Exception e) {
            logger.error("sendrawtransaction exception: {}", e.getMessage());
            return "";
        }
    }

    public String postrawtransaction(String tx) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("rawhex", tx);
        try {
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, objectNode.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Host", url.getHost())
                    .addHeader("Connection", "close")
                    .post(body)
                    .build();
            Response response = client().newCall(request).execute();
            String res = response.body().string();
            response.close();
            if (response.code() != 200) {
                throw new Exception(res);
            }
            return res;
        } catch (Exception e) {
            logger.error("postrawtransaction exception: {}", e.getMessage());
            return "";
        }
    }

    public String btc_getaddressutxos(String address){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "getaddressutxos")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(address);

        try {
            String res = post(objectNode.toString());
            return res;

        } catch (Exception e) {
            logger.error("btc_getaddressutxos exception: {}", e.getMessage());
            return "";
        }
    }

    public String btc_getaddresstxids(String address){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "getaddresstxids")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(address);

        try {
            String res = post(objectNode.toString());
            return res;

        } catch (Exception e) {
            logger.error("btc_getaddresstxids exception: {}", e.getMessage());
            return "";
        }
    }

    public String btc_getaddressbalance(String address){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "getaddressbalance")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(address);

        try {
            String res = post(objectNode.toString());
            return res;

        } catch (Exception e) {
            logger.error("btc_getaddressbalance exception: {}", e.getMessage());
            return "";
        }
    }


    public String omni_listblocktransactions(Integer Height) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "omni_listblocktransactions")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(Height);

            String res = post(objectNode.toString());
//            logger.info("omni_listblocktransactions="+res);
            return res;

    }

    public String omni_gettransaction(String txid) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "omni_gettransaction")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(txid);

            String res = post(objectNode.toString());
            return res;

    }

    public String omni_getbalance(String address, int propertyid) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "omni_getbalance")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(address)
                .add(propertyid);
        try {
            String res = post(objectNode.toString());

            JSONObject jsonObject = JSONObject.parseObject(res);
            jsonObject = jsonObject.getJSONObject("result");
            return jsonObject.getBigDecimal("balance").toPlainString();
        } catch (Exception e) {
//            logger.error("getblockhash exception: {}", e.getMessage());
            return "";
        }
    }

    public String omni_getaddresstxidsbyexpolrer(String address){
        try {
            Thread.sleep(1000);//https://btc.com 每分钟支持120次访问
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("addr", address);
            RequestBody formBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Host", url.getHost())
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Connection", "close")
                    .post(formBody)
                    .build();
            Response response = client().newCall(request).execute();
            String res = response.body().string();
            response.close();
            if (response.code() != 200) {
                throw new Exception(res);
            }
            return res;
        } catch (Exception e) {
//            logger.error("omni_getaddresstxidsbyexpolrer exception: {}", e.getMessage());
            return "";
        }
    }
    public String omni_getaddressbalancebyexpolrer(String address){
        try {
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("addr", address);
            RequestBody formBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Host", url.getHost())
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Connection", "close")
                    .post(formBody)
                    .build();
            Response response = client().newCall(request).execute();
            String res = response.body().string();
            response.close();
            if (response.code() != 200) {
                throw new Exception(res);
            }
            return res;
        } catch (Exception e) {
            logger.error("omni_getaddressbalancebyexpolrer exception: {}", e.getMessage());
            return "";
        }
    }

    public String omni_postrawtransaction(String tx) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("signedTransaction", tx);
            RequestBody formBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Host", url.getHost())
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Connection", "close")
                    .post(formBody)
                    .build();
            Response response = client().newCall(request).execute();
            String res = response.body().string();
            response.close();
            if (response.code() != 200) {
                throw new Exception(res);
            }
            return res;
        } catch (Exception e) {
            logger.error("omni_postrawtransaction exception: {}", e.getMessage());
            return "";
        }
    }
//    public static void main(String arg[]) throws Exception {
//
//
//
//    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TxObject {
        private String address;
        private String txid;
        private int vout;
        private String category; // send or receive
        private double amount;
        private double fee;
        private int time;
        private boolean trusted;
        private int confirmations;
        private String blockhash;
        private int blocktime;
        private boolean abandoned;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

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

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getFee() {
            return fee;
        }

        public void setFee(double fee) {
            this.fee = fee;
        }

        public long getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public boolean isTrusted() {
            return trusted;
        }

        public void setTrusted(boolean trusted) {
            this.trusted = trusted;
        }

        public int getConfirmations() {
            return confirmations;
        }

        public void setConfirmations(int confirmations) {
            this.confirmations = confirmations;
        }

        public String getBlockhash() {
            return blockhash;
        }

        public void setBlockhash(String blockhash) {
            this.blockhash = blockhash;
        }

        public long getBlocktime() {
            return blocktime;
        }

        public void setBlocktime(int blocktime) {
            this.blocktime = blocktime;
        }

        public boolean isAbandoned() {
            return abandoned;
        }

        public void setAbandoned(boolean abandoned) {
            this.abandoned = abandoned;
        }

        @Override
        public String toString() {
            return "{address: " + address +
                    ",txid: " + txid +
                    ",category: " + category +
                    ",amount: " + amount +
                    ",time: " + time +
                    ",confirmations: " + confirmations +
                    ",blockhash: " + blockhash +
                    ",blocktime: " + blocktime +
                    ",abandoned: " + abandoned +
                    "}";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SinceBlock {
        private List<TxObject> transactions;
        private String lastblock;

        public List<TxObject> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<TxObject> transactions) {
            this.transactions = transactions;
        }

        public String getLastblock() {
            return lastblock;
        }

        public void setLastblock(String lastblock) {
            this.lastblock = lastblock;
        }

        @Override
        public String toString() {
            return "{transactions: " + transactions +
                    ",lastblock: " + lastblock +
                    "}";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalletInfo {
        private int walletversion = 0;
        private double balance = 0;
        @JsonProperty("unconfirmed_balance")
        private double unconfirmedBalance = 0;
        @JsonProperty("immature_balance")
        private double immatureBalance = 0;
        private int txcount = 0;
        private double paytxfee = 0;

        public int getWalletversion() {
            return walletversion;
        }

        public void setWalletversion(int walletversion) {
            this.walletversion = walletversion;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public double getUnconfirmedBalance() {
            return unconfirmedBalance;
        }

        public void setUnconfirmedBalance(double unconfirmedBalance) {
            this.unconfirmedBalance = unconfirmedBalance;
        }

        public double getImmatureBalance() {
            return immatureBalance;
        }

        public void setImmatureBalance(double immatureBalance) {
            this.immatureBalance = immatureBalance;
        }

        public int getTxcount() {
            return txcount;
        }

        public void setTxcount(int txcount) {
            this.txcount = txcount;
        }

        public double getPaytxfee() {
            return paytxfee;
        }

        public void setPaytxfee(double paytxfee) {
            this.paytxfee = paytxfee;
        }

        @Override
        public String toString() {
            return "{walletversion: " + walletversion +
                    ",balance: " + balance +
                    ",unconfirmedBalance: " + unconfirmedBalance +
                    ",immatureBalance: " + immatureBalance +
                    ",txcount: " + txcount +
                    ",paytxfee: " + paytxfee +
                    "}";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Unspent {
        private String address;
        private String txid;
        private int vout;
        private double amount;
        private String scriptPubKey;

        @Override
        public String toString() {
            return "{address: " + address +
                    ",txid: " + txid +
                    ",amount: " + amount +
                    ",vout: " + vout +
                    ",scriptPubKey: " + scriptPubKey +
                    "}";
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

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

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getScriptPubKey() {
            return scriptPubKey;
        }

        public void setScriptPubKey(String scriptPubKey) {
            this.scriptPubKey = scriptPubKey;
        }

        public BtcColdToHotManager.Unspent toUnspent(Currency currency, AddressItem addressItem) {
            BtcColdToHotManager.Unspent unspent = new BtcColdToHotManager.Unspent();
            unspent.setAddress(address);
            unspent.setVout(vout);
            unspent.setScriptPubKey(scriptPubKey);
            unspent.setTxid(txid);
            unspent.setChildNumber(addressItem.getChildNumber());
            unspent.setAmount(CurrencyWrapper.valueOf(currency, amount).longValue());
            return unspent;
        }
    }

}

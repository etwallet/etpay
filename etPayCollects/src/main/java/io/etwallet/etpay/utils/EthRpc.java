package io.etwallet.etpay.utils;

import cn.hutool.core.codec.BCD;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.ethereum.jsonrpc.TypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class EthRpc implements BaseRpc {

    private static final int SCALE = 18;
    private static Logger logger = LoggerFactory.getLogger(EthRpc.class);
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    public static final int CONNECT_TIMEOUT = 50;
    public static final int READ_TIMEOUT = 50;
    public static final int WRITE_TIMEOUT = 50;
    private URL url;
    private String authorization;
    private static OkHttpClient instance;

    public EthRpc(String urlName) throws MalformedURLException {
        url = new URL(urlName);
    }

    public EthRpc(String urlName, String user, String password) throws MalformedURLException {
        url = new URL(urlName);
        // String strRPCUserColonPass = user + ":" + password;
        // authorization = "Basic " + Base64.getEncoder().encodeToString(strRPCUserColonPass.getBytes());
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
        try {
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
            if (response.code() == 200 && null != code && (code.equals("200") || code.equals("0"))) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;

    }

//    private static String getUserAgent() {
//        String userAgent = "";
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            try {
//                userAgent = WebSettings.getDefaultUserAgent(context);
//            } catch (Exception e) {
//                userAgent = System.getProperty("http.agent");
//            }
//        } else {
//            userAgent = System.getProperty("http.agent");
//        }
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0, length = userAgent.length(); i < length; i++) {
//            char c = userAgent.charAt(i);
//            if (c <= '\u001f' || c >= '\u007f') {
//                sb.append(String.format("\\u%04x", (int) c));
//            } else {
//                sb.append(c);
//            }
//        }
//        return sb.toString();
//    }
    private String get(String url) throws Exception {
//        String s = "https://chain.api.btc.com/v3/address/17Zg9BAsUzUjfn6xySgEgFxjfphXvvJo73/tx?page=1&pagesize=50";
        Request request = new Request.Builder()
                //                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                //                .addHeader("Accept-Encoding", "gzip, deflate, br")
                //                .addHeader("Accept-Language", "zh,en-US;q=0.7,en;q=0.3")
                //                .addHeader("Cache-Control", "max-age=0")
                //                .addHeader("Connection", "keep-alive")
                //                .addHeader("Cookie", "Hm_lvt_9d96a423dd505d9f575a6b445e48b709=1555316883,1555327294,1555555683,1555912663; Hm_lpvt_9d96a423dd505d9f575a6b445e48b709=1556263717")
                //                .addHeader("Host", "api.yitaifang.com")
                //                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
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

    public String post(String json) throws Exception {
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Host", url.getHost())
                .addHeader("Connection", "close")
                // .addHeader("Authorization", authorization)
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

    public Optional<Integer> blockNumber() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "eth_blockNumber")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params");
        try {
            String res = post(objectNode.toString());
            return Optional.ofNullable(new BigInteger(mapper.readTree(res)
                    .get("result").asText().substring(2), 16).intValue());
        } catch (Exception e) {
            logger.warn("eth_blockNumber exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Block> getBlockByNumber(int height, boolean full) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "eth_getBlockByNumber")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(String.format("0x%x", height))
                .add(full);
        try {
            String res = post(objectNode.toString());
            return Optional.ofNullable(mapper.readValue(mapper.readTree(res)
                    .get("result")
                    .toString(), Block.class));
        } catch (Exception e) {
            logger.warn("eth_getBlockByNumber exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public String getDataByExpolrer() {
        try {
            Thread.sleep(1000);//https://btc.com 每分钟支持120次访问
            String path = url + "";
            String res = get(path.toString());
            return res;
        } catch (Exception e) {
            logger.error("getDataByExpolrer:" + url + ", exception: {}", e.getMessage());
            return "";
        }
    }

    public BigDecimal getBalance(String address) {
        if (!address.startsWith("0x")) {
            address = "0x" + address;
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "eth_getBalance")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(address)
                .add("latest");
        try {
            String res = post(objectNode.toString());
            logger.info("eth_getBalance: {}", res);
            return new BigDecimal(new BigInteger(mapper.readTree(res)
                    .get("result").asText().substring(2), 16))
                    .movePointLeft(SCALE);
        } catch (Exception e) {
            logger.warn("eth_getBalance exception: {}", e.getMessage());
            return null;
        }
    }

    public Optional<String> sendRawTransaction(String txHex) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "eth_sendRawTransaction")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(txHex);
        try {
            String res = post(objectNode.toString());
            JsonNode jsonNode = mapper.readTree(res);
            if (jsonNode.get("error") != null) {
                String errorMessage = jsonNode.get("error").get("message").asText();
                String sentFlag = "known transaction: ";
                if (errorMessage.indexOf(sentFlag) >= 0) {
                    return Optional.of(errorMessage.replace(sentFlag, "0x"));
                }
                logger.warn("sendRawTransaction {} error: {}", txHex, errorMessage);
//                return Optional.of("manual handling:"+errorMessage);
                return Optional.empty();
            } else {
                return Optional.ofNullable(jsonNode
                        .get("result")
                        .asText());
            }
        } catch (Exception e) {
            logger.warn("sendRawTransaction exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<TxObject> getTransactionByHash(String hash) {
        if (!hash.startsWith("0x")) {
            hash = "0x" + hash;
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "eth_getTransactionByHash")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(hash);
        try {
            String res = post(objectNode.toString());
            return Optional.ofNullable(mapper.readValue(mapper.readTree(res)
                    .get("result")
                    .toString(), TxObject.class));
        } catch (Exception e) {
            logger.warn("getTransactionByHash exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<TxReceiptObject> getTransactionReceipt(String hash) {
        if (!hash.startsWith("0x")) {
            hash = "0x" + hash;
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "eth_getTransactionReceipt")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(hash);
        try {
            String res = post(objectNode.toString());
            return Optional.ofNullable(mapper.readValue(mapper.readTree(res)
                    .get("result")
                    .toString(), TxReceiptObject.class));
        } catch (Exception e) {
            logger.warn("getTransactionReceipt exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Long> getNonce(String address) {
        if (!address.startsWith("0x")) {
            address = "0x" + address;
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "eth_getTransactionCount")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(address)
                .add("pending");
        try {
            String res = post(objectNode.toString());
            return Optional.of(new BigInteger(mapper.readTree(res)
                    .get("result").asText().substring(2), 16).longValue());
        } catch (Exception e) {
            logger.warn("eth_getTransactionCount exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> ethCall(String contractaddress, String data) {
        if (!contractaddress.startsWith("0x")) {
            contractaddress = "0x" + contractaddress;
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode()
                .put("method", "eth_call")
                .put("id", Integer.valueOf(1));
        objectNode.putArray("params")
                .add(mapper.createObjectNode()
                        .put("to", contractaddress)
                        .put("data", data))
                .add("latest");

        try {
            String res = post(objectNode.toString());
            return Optional.of(new String(mapper.readTree(res)
                    .get("result")
                    .asText().substring(2)));
        } catch (Exception e) {
            logger.warn("eth_call exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    //前补0,补齐64位
    String AddPreZero(String s) {
        if (null != s && s.length() < 64) {
            String res = "0000000000000000000000000000000000000000000000000000000000000000";
            return res.substring(s.length()) + s;
        }
        return s;
    }

    public BigInteger getSymbolBalance(String contractaddress, String address) throws Exception {
        try {
            if (null == contractaddress || null == address) {
                return null;
            }
            if (contractaddress.startsWith("0x")) {
                contractaddress = contractaddress.substring(2);
            }
            if (address.startsWith("0x")) {
                address = address.substring(2);
            }

            String data;
//            data = TypeConverter.toJsonHex(HashUtil.sha3("balanceOf(address)".getBytes())).substring(0,10);//0x70a08231
            data = "0x70a08231";//写死,不去计算哈希值,因为每次都是一样的
            data += AddPreZero(address);

            Optional<String> res = ethCall(contractaddress, data);
            if (res.isPresent()) {
                BigInteger bigInteger = TypeConverter.StringHexToBigInteger(res.get());

                return bigInteger;
            }
            return null;
        } catch (Exception e) {
            throw new Exception("获取余额失败："+e.getMessage());
//            return null;
        }
    }

    public String getSymbol(String contractaddress) {
        try {
            String data;
//            data = TypeConverter.toJsonHex(HashUtil.sha3("symbol()".getBytes())).substring(0,10);
            data = "0x95d89b41";//写死,不去计算哈希值,因为每次都是一样的
            if (null != contractaddress && contractaddress.startsWith("0x")) {
                contractaddress = contractaddress.substring(2);
            }
            Optional<String> res = ethCall(contractaddress, data);
            if (res.isPresent()) {
                int symbol_length = TypeConverter.StringHexToBigInteger(res.get().substring(64, 128)).intValue();
                if (symbol_length > 32) {
                    symbol_length = 32;
                }
                return new String(BCD.strToBcd(res.get().substring(128, 128 + symbol_length * 2)));
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

//    public static void main(String arg[]) throws Exception {
//
////  String et = "0xa9059cbb0000000000000000000000007982b15abd351226d312b544fa56fd5297aebd730000000000000000000000000000000000000000000000000000000381a55d00".substring(74);
////        System.out.println(et);
////        String urlName = "http://192.168.1.58:8545";
////        EthRpc ethRpc = new EthRpc(urlName);
////        System.out.println(ethRpc.getNonce("0x4b1617dae6a2d968dad80de4a4b3e98fd43ba5ea"));
////        System.out.println(TypeConverter.StringHexToBigInteger("0x46997a8cfa000"));
////        System.out.println(TypeConverter.toJsonHex(new BigInteger("21000")));
//        //  System.out.println(ethRpc.getBalance("0x7e469b6a00aaee191f2294de055ea9a0c4c5f969"));
//        //  System.out.println(ethRpc.getTransactionByHash("0x51992794275c29010b79b9d113c1afc6a59cf2a41fe83917063e6708097736e1"));
//        //  System.out.println(ethRpc.sendRawTransaction("0xf86d01850430e2340083015f90947e469b6a00aaee191f2294de055ea9a0c4c5f9698803311fc80a570000801ca0fb5dfb7e6e7e1036dc4524970e6ae9d2092a8e935a2d521e19234b9b271df1a3a07014bace2939293bc212e6ca835b1c083854e2d8a6cac277530615102e840cb0"));
//    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TxObject {

        private String from;
        private String to;
        private String hash;
        private int blockNumber;
        private BigDecimal value;
        private BigDecimal gas;
        private BigDecimal gasPrice;
        private long nonce;
        private String blockHash;
        private String input;

        public boolean IsErc20() {
            if (input != null && input.length() > 20) {
                return input.startsWith("0xa9059cbb000000000000000000000000");
            }
            return false;
        }

        public String GetErc20TargetAddress() {
            return "0x" + input.substring(34, 74);
        }

        public BigDecimal GetErc20Qty(Long decimal) {
            return new BigDecimal(new BigInteger(input.substring(74).replaceAll("^(0+)", ""), 16)).movePointLeft(decimal.intValue());
        }

        public String getBlockHash() {
            return blockHash;
        }

        public void setBlockHash(String blockHash) {
            this.blockHash = blockHash;
        }

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        @Override
        public String toString() {
            return "{from: " + from
                    + ",to: " + to
                    + ",hash: " + hash
                    + ",blockNumber: " + blockNumber
                    + ",value: " + value
                    + ",gas: " + gas
                    + ",gasPrice: " + gasPrice
                    + ",nonce: " + nonce
                    + "}";
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public int getBlockNumber() {
            return blockNumber;
        }

        public void setBlockNumber(String blockNumber) {
            if (blockNumber != null) {
                this.blockNumber = Integer.parseInt(blockNumber.substring(2), 16);
            }
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(String value) {
            BigInteger bigInteger = new BigInteger(value.substring(2), 16);
            this.value = new BigDecimal(bigInteger).movePointLeft(SCALE);
        }

        public BigDecimal getGas() {
            return gas;
        }

        public void setGas(String gas) {
            BigInteger bigInteger = new BigInteger(gas.substring(2), 16);
            this.gas = new BigDecimal(bigInteger);
        }

        public BigDecimal getGasPrice() {
            return gasPrice;
        }

        public void setGasPrice(String gasPrice) {
            BigInteger bigInteger = new BigInteger(gasPrice.substring(2), 16);
            this.gasPrice = new BigDecimal(bigInteger).movePointLeft(SCALE);
        }

        public long getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = Long.parseLong(nonce.substring(2), 16);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LogsObject {

        private String address;
        private List<String> topics;
        private String data;
        private int logIndex;

        @Override
        public String toString() {
            return "{address: " + address
                    + ",topics: " + topics
                    + ",data: " + data
                    + ",logIndex: " + logIndex
                    + "}";
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public List<String> getTopics() {
            return topics;
        }

        public void setTopics(List<String> topics) {
            String s;
            for (int i = 0; i < topics.size(); i++) {
                s = topics.get(i);
                if (null != s && s.startsWith("0x")) {
                    topics.set(i, s.substring(2));
                }
            }
            this.topics = topics;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            if (null != data && data.startsWith("0x")) {
                data = data.substring(2);
            }
            this.data = data;
        }

        public int getLogIndex() {
            return logIndex;
        }

        public void setLogIndex(String logIndex) {
            this.logIndex = Integer.parseInt(logIndex.substring(2), 16);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TxReceiptObject {

        private String from;
        private String to;
        private String transactionHash;
        private int blockNumber;
        private BigDecimal cumulativeGasUsed;
        private BigDecimal gasUsed;
        private List<LogsObject> logs;
        private String status;
        private String blockHash;

        @Override
        public String toString() {
            return "{from: " + from
                    + ",to: " + to
                    + ",transactionHash: " + transactionHash
                    + ",blockNumber: " + blockNumber
                    + ",cumulativeGasUsed: " + cumulativeGasUsed
                    + ",gasUsed: " + gasUsed
                    + ",logs: " + logs
                    + ",status: " + status
                    + ",blockHash: " + blockHash
                    + "}";
        }

        public String getBlockHash() {
            return blockHash;
        }

        public void setBlockHash(String blockHash) {
            this.blockHash = blockHash;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getTransactionHash() {
            return transactionHash;
        }

        public void setTransactionHash(String transactionHash) {
            this.transactionHash = transactionHash;
        }

        public int getBlockNumber() {
            return blockNumber;
        }

        public void setBlockNumber(String blockNumber) {
            if (blockNumber != null) {
                this.blockNumber = Integer.parseInt(blockNumber.substring(2), 16);
            }
        }

        public BigDecimal getCumulativeGasUsed() {
            return cumulativeGasUsed;
        }

        public void setCumulativeGasUsed(String cumulativeGasUsed) {
            BigInteger bigInteger = new BigInteger(cumulativeGasUsed.substring(2), 16);
            this.cumulativeGasUsed = new BigDecimal(bigInteger);
        }

        public BigDecimal getGasUsed() {
            return gasUsed;
        }

        public void setGasUsed(String gasUsed) {
            BigInteger bigInteger = new BigInteger(gasUsed.substring(2), 16);
            this.gasUsed = new BigDecimal(bigInteger);
        }

        public List<LogsObject> getLogs() {
            return logs;
        }

        public void setLogs(List<LogsObject> logs) {
            this.logs = logs;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            if (null != status && status.startsWith("0x")) {
                status = status.substring(2);
            }
            this.status = status;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Block {

        private List<TxObject> transactions;
        private String parentHash;
        private String hash;
        private int number;

        public List<TxObject> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<TxObject> transactions) {
            this.transactions = transactions;
        }

        public String getParentHash() {
            return parentHash;
        }

        public void setParentHash(String parentHash) {
            this.parentHash = parentHash;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = Integer.parseInt(number.substring(2), 16);
        }

        @Override
        public String toString() {
            return "{transactions: " + transactions
                    + ",parentHash: " + parentHash
                    + ",hash: " + hash
                    + ",number: " + number
                    + "}";
        }
    }

}

ETpay是全球领先的数字资产支付平台，旨在为第三方平台提供快速接入区块链支付服务，实现数字货币资产最快速、最安全的流通，让商户释放精力更加专注于自身的业务开发，而不再需要费时费力的去实现各种公链的支付。 

ETpay适用于各种规模的交易所、钱包、区块链游戏等相关应用。

**目录说明：**
### 1、doc
详细开发文档。[下载](https://github.com/eostoken/etpay/raw/master/doc/MorningPay%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3.docx)

### 2、etPayTools
etPayTools源代码Project。用于生成私钥和相关公钥配置信息，用于初始化商户MorningPay配置。

可以自行打包jar或直接运行io.etwallet.etpay.Main类生成基本配置，也可以直接[下载](https://github.com/eostoken/etpay/raw/master/bin/etPayTools.jar)已经打包的jar包。
jar包执行：
```
java -jar etPayTools.jar>data.txt 
```

在当前目录找到data.txt，里面内容生成结果。

### 3、etPaySDK
java版sdk及Demo。

需要修改io.etwallet.etpay.config.Config类中相关配置。

### 4、etPayCollects
   etPayCollects源代码Project。用于归集冷钱包资产，目前支持所有ERC-20 代币（ETH除外）。
   
   可以自行打包jar或直接运行io.etwallet.etpay.Main类生成基本配置，也可以直接[下载](https://github.com/eostoken/etpay/raw/master/bin/etPayCollects.jar)已经打包的jar包。
   
   jar包执行--安装jre后，直接双击打开。或执行下面命令行打开操作界面：
   
```
java -jar etPayCollects.jar
```
   
   请确保操作系统防火墙没有阻止访问外面相关端口。

### 4、bin
1. 存放etPayTools打包为jar的文件etPayTools.jar。初始化生成助记词、私钥和公钥工具。
2. 存放etPayCollects打包为jar的文件etPayCollects.jar。归集资产工具。

### 其它
java运行环境安装，可以自行下载jre。jre下载地址：

[https://www.java.com](https://www.java.com)

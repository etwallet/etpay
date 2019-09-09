package io.eblock.eos4j.utils.ecc;

import sun.security.ec.ECPrivateKeyImpl;
import sun.security.ec.ECPublicKeyImpl;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.HashMap;
import java.util.Map;
 
public class EcDsaCode {
    private static final String KEY_ALGORITHM="EC";
    private static final int KEY_SIZE = 256;
    private static final String SIGNATURE_ALGORITHM = "SHA512withECDSA";
 
    //产生密钥对,获取密钥参数
    public static  Map<String,Object> initKey() throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥对生成器
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //得到公钥和私钥
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        //获取私钥D
        BigInteger D = privateKey.getS();
        //得到公钥的横纵坐标
        BigInteger publicKeyX= publicKey.getW().getAffineX();
        BigInteger publicKeyY= publicKey.getW().getAffineY();
        //得到生成椭圆曲线的参数a,b
        java.security.spec.ECParameterSpec ecParams = privateKey.getParams();
        BigInteger curveA = ecParams.getCurve().getA();
        BigInteger curveB = ecParams.getCurve().getB();
        //获取此椭圆有限字段的素数 qq
        ECFieldFp fieldFp = (ECFieldFp) ecParams.getCurve().getField();
        BigInteger q = fieldFp.getP();
        //获取椭圆的基点的x,y值
        BigInteger coordinatesX = ecParams.getGenerator().getAffineX();
        BigInteger coordinatesY =  ecParams.getGenerator().getAffineY();
        //基点的阶
        BigInteger coordinatesG = ecParams.getOrder();
        //获取余因子
        int h = ecParams.getCofactor();
 
        Map<String, Object> initKeyMap = new HashMap<String,Object>();
        //椭圆曲线参数A,B
        initKeyMap.put("A",curveA);
        initKeyMap.put("B",curveB);
        //素数Q
        initKeyMap.put("Q",q);
        //G点的坐标
        initKeyMap.put("X",coordinatesX);
        initKeyMap.put("Y",coordinatesY);
        //N为G点的阶
        initKeyMap.put("N",coordinatesG);
        //H为余因子
        initKeyMap.put("H",h);
        //获取私钥
        initKeyMap.put("D",D);
        //获取公钥点的坐标
        initKeyMap.put("PUBKEY_X",publicKeyX);
        initKeyMap.put("PUBKEY_Y",publicKeyY);
        return initKeyMap;
    }
 
    //DATA是数据，Q是大素数q，A，B为椭圆曲线参数a,b，G为基点，N为点G的阶，H是余因子,X,Y是基点的坐标，PUBKEY_X,PUBKEY_Y是公钥(DG)的坐标，D是随机数私钥
 
    public static KeyPair generateKey(BigInteger Q, BigInteger A, BigInteger B, BigInteger N, int H, BigInteger X, BigInteger Y, BigInteger PUBKEY_X, BigInteger PUBKEY_Y, BigInteger D) throws Exception{
        //创建基于指定值的椭圆曲线域参数
        ECParameterSpec ecParameterSpec = new ECParameterSpec(new EllipticCurve(new ECFieldFp(Q),A,B),new ECPoint(X,Y),N,H);
        ECPublicKey publicKey = new ECPublicKeyImpl(new ECPoint(PUBKEY_X,PUBKEY_Y),ecParameterSpec);
        ECPrivateKey privateKey = new ECPrivateKeyImpl(D,ecParameterSpec);
        return new KeyPair(publicKey,privateKey);
    }
    public static   byte[] sign(byte[] data,PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }
 
 
    public static boolean verify(byte[] data,PublicKey publicKey, byte[] sign) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }
 
 
 
}

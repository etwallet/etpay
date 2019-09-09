/**
 * 
 */
package com.morningpay.demo;

import java.math.BigDecimal;
import java.util.Date;

import com.morningpay.form.WithdrawForm;
import com.morningpay.utils.WalletUtil;

import okhttp3.MediaType;

/**
 * @author abill
 * Demo仅做参考，具体请根据自己业务实现
 *
 */
public class Demo {
	public static final MediaType JSON = MediaType.parse( "application/json; charset=utf-8" );

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String result = "";
		
		//////////////////////初始化商户配置///////////////////////////
//		String walletconfigJson = "{\"ethHotConfigObj\":{\"path\":\"M/44H/60H/1H/0\",\"chainCode\":\"1ee21014d21cb8d4eabc16a83bb7e0ea8f0803ecae301f91d8adb4fec6f4a6db\",\"address0\":\"16jYubE7f92V6Tp9o9J5WptKEUUBt3UevJ\",\"pubKey\":\"02adae3a2a70de9827a63918b20f3ba4ba2e84fd9b1d03fff13fab5ddec838cc30\"},\"btcColdConfigObj\":{\"path\":\"M/44H/0H/0H/0\",\"chainCode\":\"50631ec00f6c888bccc9b1e41fda37fa2a0a96ddd6f1474d673be55a57dbe177\",\"address0\":\"1NKzAHR11npqG46ZCpGJYNDFfjr6L5Sr2n\",\"pubKey\":\"03381ed18fb5bc7d09176643e7ca46e5f404f7988205464023d6c3e3ebb72f7659\"},\"eosColdConfigObj\":{\"path\":\"M/44H/194H/0H/0\",\"chainCode\":\"95ad27a178937d3f874cd8ad84493f3025e483e74edde97bf1ee7112a8cb88a1\",\"address0\":\"1BzxrxjF9vFBQ14FULjaonBEvqDAKkPFKT\",\"pubKey\":\"02354477f2d6fb50f125c632c04637638989a4b299a57f8b92e6858478377b91df\"},\"usdtHotConfigObj\":{\"path\":\"M/44H/0H/4H/0\",\"chainCode\":\"dde15dd3f858fd65b091fb59187f633b3184c6620aebc7022226e6fcb8e4ad9d\",\"address0\":\"19Jd1GCznfB8e3BwiFzCSBMDffPjCPyLmi\",\"pubKey\":\"02f68c11be6dfd46708ceeb5fa59995aa98be45899bba752d50b9f7e1958f97748\"},\"btcHotConfigObj\":{\"path\":\"M/44H/0H/1H/0\",\"chainCode\":\"7ba0c85c9231f07d0556d1ac10f3eba52f51c638b8d0bb6d4d870886eabb1541\",\"address0\":\"1DiwJxhziB7iWe9XtwH2zeRN6nfThBuFxm\",\"pubKey\":\"0337d213e895ed605fe97f4a7189482d9faad47dcee3383cceb38632358153a680\"},\"ethColdConfigObj\":{\"path\":\"M/44H/60H/0H/0\",\"chainCode\":\"062f9a63f998a80232dc37ed700fa9993b3d222adb2a84b2081336df35a29280\",\"address0\":\"1BrjburvGABrcu1ME12E4ipRS85BqzCpid\",\"pubKey\":\"033211199bd8dfd4c31d587e12fe588cb592f77a83f606421b27fdabc81c227b21\"},\"usdtColdConfigObj\":{\"path\":\"M/44H/0H/3H/0\",\"chainCode\":\"bd26de89b381ee3c17b0bd8fb8366623d57ddc3e98a7d7be934960a142725942\",\"address0\":\"1PKtCtoCwGd6SNYt9Wk8PM256Di7EeKxo7\",\"pubKey\":\"02a18bed738ab4332bbff303861679bd69f6bd29e64d15769913c7f7ed9350e53e\"},\"eosHotConfigObj\":{\"path\":\"M/44H/194H/1H/0\",\"chainCode\":\"3343519cbb60472224652e3bfabee491e38644a7733ebff183fca3b8a1d39390\",\"address0\":\"1FMHMbViRgRDHeieweuseWHJnu9RYP8PTk\",\"pubKey\":\"032f7708a6e0d20c8aeba68dc99e09c7ad0df76563cc47917817aca0c4d21f5f1e\"},\"userName\":\"yourname\",\"rsaPubKey\":\"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbnvEHzoqdwalmvYn+L3Afmz+d+ZqN+waB11rOPZmhZuKCB1sOKniM1Lc8hQOET3eJAuKHJtQ++N+dJmUMemOy2mnqA1Jc0rzsHzBSNbkCnwdfa9Todpx6wUqw5YHiI88oWeaXjZPqQ1j0u3WKcDc3ZAa99l4vVhpQhMcW1AzQ2wIDAQAB\"}";
//		result = WalletUtil.initConfig(walletconfigJson);
//		System.out.println("初始化配置结果："+result);
		//////////////////////////////////////////////////////////////
		
		//////////////////////开通一个币///////////////////////////
		result = WalletUtil.openCoinType("ETH_USDT_0xdac17f958d2ee523a2206206994597c13d831ec7", "", "", "", ""); //开通ERC-20 USDT
		System.out.println("开通结果："+result);
		//////////////////////////////////////////////////////////////
		
		//////////////////////添加自定义冷钱包地址///////////////////////////
//		String coinType="EOS";
//		String address = "testaddressx";
//		result = WalletUtil.addSelfDefineAddress(coinType, address);
//		System.out.println("添加地址结果："+result);
		//////////////////////////////////////////////////////////////
		
		//////////////////////获取冷钱包子地址//////////////////////////////
//		result = WalletUtil.getAddress(WalletUtil.BTC);
//		System.out.println("获取冷钱包子地址结果："+result);
		//////////////////////////////////////////////////////////////
		
		//////////////////////获取冷钱包收币记录///////////////////////////////
//		result = WalletUtil.queryColdWalletRecord(WalletUtil.EOS, "1");
//		System.out.println("获取冷钱包记录结果："+result);
		//////////////////////////////////////////////////////////////
		
		//////////////////////获取提现记录/////////////////////////////////
//		result = WalletUtil.queryWithdrawRecord(WalletUtil.USDT, "1");
//		System.out.println("获取提现记录结果："+result);
		//////////////////////////////////////////////////////////////
		
		 //////////////////////提现////////////////////////////////////
//		 WithdrawForm withdrawFrom = new WithdrawForm();
//		 withdrawFrom.setAddress( "1JHQjzsMkjDCuoKJYq6DJrvkSpmcxLBEuz" );
//		 withdrawFrom.setAmount( new BigDecimal( "0.01" ) );
//		 withdrawFrom.setCoinName( WalletUtil.USDT );
//		 withdrawFrom.setMemo( "xxx" );
//		 withdrawFrom.setSerial_number( String.valueOf(new Date().getTime()));
//		 result = WalletUtil.withdraw(withdrawFrom);
//		 System.out.println("提现结果："+result);
		 //////////////////////////////////////////////////////////////

	}

}

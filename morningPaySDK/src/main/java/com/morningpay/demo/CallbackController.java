package com.morningpay.demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.morningpay.config.Config;
import com.morningpay.pojo.NoticeObj;
import com.morningpay.utils.DataUtils;
import com.morningpay.utils.R;
import com.morningpay.utils.SHA256;

/**
 * 
 * @author abill 回调类 Demo仅供参考
 */
@RestController
@RequestMapping("/callback")
public class CallbackController {

	/**
	 * 添加自定义冷钱包地址
	 */
	@RequestMapping("notify")
	public R notify(@RequestBody String params) {
		if (params == null) {
			R.error();
		}
		JSONObject jo = JSONObject.parseObject(params);
		Map<String, String> dataMap = new HashMap<>();
		for (String key : jo.keySet()) {
			dataMap.put(key, jo.getString(key));
		}

		String dataString = DataUtils.buildDataString(dataMap);
		String sign = dataMap.get("sign");
		String calculateSign = SHA256.getSha256(dataString, Config.SECRET_KEY);
		if (sign.equalsIgnoreCase(calculateSign)) {
			return R.error("验签不通过");
		}

		NoticeObj noticeObj = JSON.parseObject(params, NoticeObj.class);

		// //判定是什么币，
		// if (noticeObj.getCoinType().equals("BTC")) {
		// //比特币
		// } else if (noticeObj.getCoinType().equals("USDT")) {
		// //OMNI USDT
		// }else if (noticeObj.getCoinType().startsWith("ETH")) {
		// //ETH或者ERC 20代币
		// }

		// 冷钱包回调操作
		if (noticeObj.getType().equals("cold") && noticeObj.getDirection().equals("in")) {
			// TODO:这里写冷钱包入账逻辑
		} else if (noticeObj.getType().equals("withdraw")) {
			// TODO:这里写提现成功后逻辑
		}
		return R.ok(); // 必须返回code为0的json串，否则会一直通知
	}
}

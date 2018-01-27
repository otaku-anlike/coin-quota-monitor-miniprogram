package com.coin.otaku.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import wang.raye.springboot.ApiApplication;
import wang.raye.springboot.bean.WxSendBean;
import wang.raye.springboot.utils.WxSendUtils;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ApiApplication.class)
@WebAppConfiguration
public class WxSendTests {

	@Autowired
	private WxSendUtils wxSendUtils;

	@Test
	public void sendCross() {
		WxSendBean sendBean = new WxSendBean();
		sendBean.setSckey("SCU20744Tcae3e700d2845416dcf4b7d48fefd4fc5a68472d8b5e0");
		sendBean.setExchange("binance");
		sendBean.setPrice("0.00679");
		sendBean.setSymbol("BNBBTC");
		sendBean.setStatus("2");
		sendBean.setType("MACD");
		sendBean.setTime(new Date().toString());
		sendBean.setPeriod("1h");
		wxSendUtils.sendCross(sendBean);
	}

}

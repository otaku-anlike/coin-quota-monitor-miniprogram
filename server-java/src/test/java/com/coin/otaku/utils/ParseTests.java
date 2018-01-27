package com.coin.otaku.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import wang.raye.springboot.ApiApplication;
import wang.raye.springboot.bean.WxSendBean;
import wang.raye.springboot.utils.ParseUtils;
import wang.raye.springboot.utils.WxSendUtils;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ApiApplication.class)
@WebAppConfiguration
public class ParseTests {


	@Test
	public void parseWxPrice() {
		double price = 0.0000123456;
		String result = ParseUtils.parsePrice(price);
		System.out.println(result);
	}

}

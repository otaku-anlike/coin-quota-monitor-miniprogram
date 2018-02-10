package com.coin.otaku.utils;

import de.elbatya.cryptocoins.bittrexclient.BittrexClient;
import de.elbatya.cryptocoins.bittrexclient.api.model.common.ApiResult;
import de.elbatya.cryptocoins.bittrexclient.api.model.publicapi.Market;
import de.elbatya.cryptocoins.bittrexclient.api.model.publicapi.MarketSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import wang.raye.springboot.ApiApplication;
import wang.raye.springboot.bean.WxSendBean;
import wang.raye.springboot.model.Symbols;
import wang.raye.springboot.model.SymbolsCriteria;
import wang.raye.springboot.model.mapper.SymbolsMapper;
import wang.raye.springboot.utils.ParseUtils;
import wang.raye.springboot.utils.WxSendUtils;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ApiApplication.class)
@WebAppConfiguration
public class ParseTests {

	@Autowired
	private SymbolsMapper symbolsMapper;


	@Test
	public void parseWxPrice() {
		double price = 0.0000123456;
		String result = ParseUtils.parsePrice(price);
		System.out.println(result);

//		// Create a BittrexClient
//		BittrexClient bittrexClient = new BittrexClient();
//
//		// Perform a getMarkets request on the public api
//		ApiResult<List<MarketSummary>> apiResult = bittrexClient.getPublicApi().getMarketSummaries();
//
//		// Unwrap the results
//		List<MarketSummary> markets = apiResult.unwrap();
//		for (MarketSummary market: markets) {
//			System.out.println(market.getMarketName() + ":" + market.getLast());
//		}
		SymbolsCriteria cond = new SymbolsCriteria();
		SymbolsCriteria.Criteria criteria = cond.createCriteria();
//		criteria.andExchangeEqualTo(exchange);
		List<Symbols> symbolsList = symbolsMapper.selectByExample(cond);
		for (Symbols symbol:symbolsList) {
			boolean isBtc = false;
			if(symbol.getSymbol().contains("BTC")) {
				isBtc = true;
			}
			System.out.println(symbol.getSymbol() + ":" + isBtc);
		}

	}

}

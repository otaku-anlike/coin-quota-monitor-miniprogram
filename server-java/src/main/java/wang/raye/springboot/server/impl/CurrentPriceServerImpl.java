package wang.raye.springboot.server.impl;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.TickerPrice;
import de.elbatya.cryptocoins.bittrexclient.BittrexClient;
import de.elbatya.cryptocoins.bittrexclient.api.model.common.ApiResult;
import de.elbatya.cryptocoins.bittrexclient.api.model.publicapi.MarketSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import wang.raye.springboot.bean.PositionBean;
import wang.raye.springboot.bean.WxSendBean;
import wang.raye.springboot.bean.WxSendQuotaBean;
import wang.raye.springboot.bean.WxSendQuotaPeriodBean;
import wang.raye.springboot.model.*;
import wang.raye.springboot.model.mapper.AlertMapper;
import wang.raye.springboot.model.mapper.AlertUserMapper;
import wang.raye.springboot.model.mapper.SymbolsMapper;
import wang.raye.springboot.model.mapper.UserMapper;
import wang.raye.springboot.server.CurrentPriceServer;
import wang.raye.springboot.server.UserServer;
import wang.raye.springboot.utils.DateUtils;
import wang.raye.springboot.utils.ParseUtils;
import wang.raye.springboot.utils.PostionUtils;
import wang.raye.springboot.utils.WxSendUtils;

import java.util.*;

/**
 * 用户相关数据库操作实现类
 * @author Raye
 * @since 2016年10月11日19:29:02
 */
@Repository
public class CurrentPriceServerImpl implements CurrentPriceServer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SymbolsMapper symbolsMapper;
	@Autowired
	private AlertMapper alertMapper;

	@Autowired
	private RestOperations restOperations;

	@Autowired
	private WxSendUtils wxSendUtils;
	@Autowired
	private AlertUserMapper alertUserMapper;

	@Value("${self.type.new}")
	private String NEW;
	@Value("${self.sckey}")
	private String SCKEY;

	public void wxSend(String exchange,String symbol, String price){
		String url = "https://sc.ftqq.com/SCU20744Tcae3e700d2845416dcf4b7d48fefd4fc5a68472d8b5e0.send?text=={text}&desp={desp}";

		Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("text", exchange+"上新币["+symbol+"]");
		uriVariables.put("desp", "新币["+symbol+"]目前价格:"+price);
        try {
            String result = restOperations.getForObject(url, String.class, uriVariables);
        } catch (RestClientException e){
            log.error("发新币微信时异常:"+e.getStackTrace());
        }

	}

	@Override
	public boolean binancePrice(String exchange) {
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");
		BinanceApiRestClient client = factory.newRestClient();
		List<TickerPrice> allPrices = client.getAllPrices();

		SymbolsCriteria cond = new SymbolsCriteria();
		SymbolsCriteria.Criteria criteria = cond.createCriteria();
		criteria.andExchangeEqualTo(exchange);
		List<Symbols> symbolsList = symbolsMapper.selectByExample(cond);
//		if (allPrices.size() != symbolsList.size()) {
			for (TickerPrice tickerPrice:allPrices) {
				boolean isNew = true;
				Symbols updateSymbol = new Symbols();
				for (Symbols symbol:symbolsList) {
					if(symbol.getSymbol().equals(tickerPrice.getSymbol())) {
						isNew = false;
						updateSymbol = symbol;
						break;
					}
				}
				if (isNew) {
					Alert alert = new Alert();
					alert.setExchange(exchange);
					alert.setSymbol(tickerPrice.getSymbol());
					alert.setType(NEW);
					alert.setStatus("0");
					alert.setPrice(Double.valueOf(tickerPrice.getPrice()));
					alert.setPeriod("0m");
					alert.setTime(new Date());
					alert.setCount(1);
					alertMapper.insert(alert);

					Symbols record = new Symbols();
					record.setExchange(exchange);
					record.setSymbol(tickerPrice.getSymbol());
					record.setPrice(Double.valueOf(tickerPrice.getPrice()));
					record.setTime(new Date());
					symbolsMapper.insert(record);

					this.sendNew(exchange,tickerPrice.getSymbol(),tickerPrice.getPrice());
				} else {
					updateSymbol.setPrice(Double.valueOf(tickerPrice.getPrice()));
                    updateSymbol.setTime(new Date());
					symbolsMapper.updateByPrimaryKey(updateSymbol);
				}
			}
//		}

		return false;
	}

	@Override
	public boolean bittrexPrice(String exchange) {
		// Create a BittrexClient
		BittrexClient bittrexClient = new BittrexClient();

		// Perform a getMarkets request on the public api
		ApiResult<List<MarketSummary>> apiResult = bittrexClient.getPublicApi().getMarketSummaries();

		// Unwrap the results
		List<MarketSummary> markets = apiResult.unwrap();

		SymbolsCriteria cond = new SymbolsCriteria();
		SymbolsCriteria.Criteria criteria = cond.createCriteria();
		criteria.andExchangeEqualTo(exchange);
		List<Symbols> symbolsList = symbolsMapper.selectByExample(cond);
//		if (allPrices.size() != symbolsList.size()) {
		for (MarketSummary tickerPrice:markets) {
			boolean isNew = true;
			Symbols updateSymbol = new Symbols();
			for (Symbols symbol:symbolsList) {
				if(symbol.getSymbol().equals(tickerPrice.getMarketName())) {
					isNew = false;
					updateSymbol = symbol;
					break;
				}
			}
			if (isNew) {
				Alert alert = new Alert();
				alert.setExchange(exchange);
				alert.setSymbol(tickerPrice.getMarketName());
				alert.setType(NEW);
				alert.setStatus("0");
				alert.setPrice(tickerPrice.getLast().doubleValue());
				alert.setPeriod("0m");
				alert.setTime(new Date());
				alert.setCount(1);
				alertMapper.insert(alert);

				Symbols record = new Symbols();
				record.setExchange(exchange);
				record.setSymbol(tickerPrice.getMarketName());
				record.setPrice(tickerPrice.getLast().doubleValue());
				record.setTime(new Date());
				symbolsMapper.insert(record);

				this.sendNew(exchange,tickerPrice.getMarketName(),String.valueOf(tickerPrice.getLast()));
			} else {
				updateSymbol.setPrice(tickerPrice.getLast().doubleValue());
				updateSymbol.setTime(new Date());
				symbolsMapper.updateByPrimaryKey(updateSymbol);
			}
		}
//		}

		return false;
	}

	@Override
	public boolean huobiPrice(String exchange) {
		// Create a BittrexClient
		BittrexClient bittrexClient = new BittrexClient();

		// Perform a getMarkets request on the public api
		ApiResult<List<MarketSummary>> apiResult = bittrexClient.getPublicApi().getMarketSummaries();

		// Unwrap the results
		List<MarketSummary> markets = apiResult.unwrap();

		SymbolsCriteria cond = new SymbolsCriteria();
		SymbolsCriteria.Criteria criteria = cond.createCriteria();
		criteria.andExchangeEqualTo(exchange);
		List<Symbols> symbolsList = symbolsMapper.selectByExample(cond);
//		if (allPrices.size() != symbolsList.size()) {
		for (MarketSummary tickerPrice:markets) {
			boolean isNew = true;
			Symbols updateSymbol = new Symbols();
			for (Symbols symbol:symbolsList) {
				if(symbol.getSymbol().equals(tickerPrice.getMarketName())) {
					isNew = false;
					updateSymbol = symbol;
					break;
				}
			}
			if (isNew) {
				Alert alert = new Alert();
				alert.setExchange(exchange);
				alert.setSymbol(tickerPrice.getMarketName());
				alert.setType(NEW);
				alert.setStatus("0");
				alert.setPrice(tickerPrice.getLast().doubleValue());
				alert.setPeriod("0m");
				alert.setTime(new Date());
				alert.setCount(1);
				alertMapper.insert(alert);

				Symbols record = new Symbols();
				record.setExchange(exchange);
				record.setSymbol(tickerPrice.getMarketName());
				record.setPrice(tickerPrice.getLast().doubleValue());
				record.setTime(new Date());
				symbolsMapper.insert(record);

				this.sendNew(exchange,tickerPrice.getMarketName(),String.valueOf(tickerPrice.getLast()));
			} else {
				updateSymbol.setPrice(tickerPrice.getLast().doubleValue());
				updateSymbol.setTime(new Date());
				symbolsMapper.updateByPrimaryKey(updateSymbol);
			}
		}
//		}

		return false;
	}

	private void sendNew(String exchange, String symbol, String price){
		List<AlertUser> alertUsers= this.getAlertUsers();
		//发微信
		WxSendBean sendBean = new WxSendBean();

		sendBean.setExchange(exchange);
		sendBean.setSymbol(symbol);
		sendBean.setPrice(price);

		for(AlertUser alertUser : alertUsers) {
			sendBean.setSckey(alertUser.getSckey());
			wxSendUtils.sendNew(sendBean);
		}
		log.info(exchange+"上新币["+symbol+"]");
		log.info("新币["+symbol+"]目前价格:"+price);
	}

	private List<AlertUser> getAlertUsers() {
		AlertUserCriteria cond = new AlertUserCriteria();
		cond.createCriteria()
				.andOpenflgEqualTo("1");
		return alertUserMapper.selectByExample(cond);
	}
}

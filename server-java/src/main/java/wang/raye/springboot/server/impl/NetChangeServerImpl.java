package wang.raye.springboot.server.impl;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import de.elbatya.cryptocoins.bittrexclient.BittrexClient2;
import de.elbatya.cryptocoins.bittrexclient.api.model.publicapi.BittrexInterval;
import de.elbatya.cryptocoins.bittrexclient.api.model.publicapi.Candle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wang.raye.springboot.bean.*;
import wang.raye.springboot.model.*;
import wang.raye.springboot.model.mapper.*;
import wang.raye.springboot.server.MacdCrossServer;
import wang.raye.springboot.server.NetChangeServer;
import wang.raye.springboot.utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 涨跌幅相关数据库操作实现类
 * @author Raye
 * @since 2016年10月11日19:29:02
 */
@Repository
public class NetChangeServerImpl implements NetChangeServer{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SymbolsMapper symbolsMapper;
	@Autowired
	private MacdCrossMapper macdCrossMapper;
	@Autowired
	private MacdCrossHistoryMapper macdCrossHistoryMapper;
	@Autowired
	private AlertMapper alertMapper;
	@Autowired
	private AlertSettingMapper alertSettingMapper;
	@Autowired
	private AlertUserMapper alertUserMapper;

	@Autowired
	private WxSendUtils wxSendUtils;

	@Autowired
	private QuotaUtils quotaUtils;
	@Autowired
	private QuotaCrossUtils quotaCrossUtils;

	@Value("${self.type.volatile}")
	private String VOLATILE;

	@Override
	public boolean binanceNetChange(String exchange, CandlestickInterval periods) {
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");
		BinanceApiRestClient client = factory.newRestClient();

		SymbolsCriteria cond = new SymbolsCriteria();
		SymbolsCriteria.Criteria criteria = cond.createCriteria();
		criteria.andExchangeEqualTo(exchange);
		List<Symbols> symbolsList = symbolsMapper.selectByExample(cond);
		for (Symbols symbols:symbolsList) {
			//币种名
			String symbol = symbols.getSymbol();
			// k线周期
			String period = periods.getIntervalId();
			//调api获取k线
			List<Candlestick> candlestickList = client.getCandlestickBars(symbols.getSymbol(), periods);

			this.getCandlestickList(candlestickList,exchange, symbol, period);
		}

		return true;
	}

	@Override
	public boolean bittrexNetChange(String exchange, BittrexInterval periods) {
		// Create a BittrexClient
		BittrexClient2 bittrexClient2 = new BittrexClient2();

		SymbolsCriteria cond = new SymbolsCriteria();
		SymbolsCriteria.Criteria criteria = cond.createCriteria();
		criteria.andExchangeEqualTo(exchange);
		List<Symbols> symbolsList = symbolsMapper.selectByExample(cond);
		for (Symbols symbols:symbolsList) {
			//币种名
			String symbol = symbols.getSymbol();
			// k线周期
			String period = periods.getIntervalId();
			//调api获取k线
			List<Candle> candleList = bittrexClient2.getPublicApi2().getCandles(symbols.getSymbol(),period).unwrap();
			//
			List<Candlestick> candlestickList = ParseUtils.parseCandlestick(candleList);

			this.getCandlestickList(candlestickList,exchange, symbol, period);

		}

		return true;
	}

	private void getCandlestickList(List<Candlestick> candlestickList,String exchange, String symbol, String period) {

		Candlestick lastCandle = candlestickList.get(candlestickList.size()-1);
		Candlestick secondCandle = candlestickList.get(candlestickList.size()-2);

		double lastHigh = Double.valueOf(lastCandle.getHigh());
		double lastLow = Double.valueOf(lastCandle.getLow());
		double secondClose = Double.valueOf(secondCandle.getClose());

		//判断是否爆涨还是暴跌
		String status = quotaCrossUtils.getVolatile(lastHigh,lastLow,secondClose);

		List<Double> priceHighList = new ArrayList<>();
		List<Double> priceLowList = new ArrayList<>();

		for (Candlestick candlestick: candlestickList) {
			priceHighList.add(Double.valueOf(candlestick.getHigh()));
			priceLowList.add(Double.valueOf(candlestick.getLow()));
		}
		double maxPrice = Collections.max(priceHighList);
		double minPrice =Collections.min(priceLowList);

		// 保存提醒数据
		this.saveAlert(exchange, symbol,period, VOLATILE, status, lastCandle, maxPrice, minPrice);

		try {
			Thread.sleep(2000);
		}
		catch (InterruptedException e) {
			log.error("线程异常终止...");
		}
	}

	/**
	 *  保存提醒数据
	 * @param exchange
	 * @param symbol
	 * @param period
	 * @param type
	 * @param status
	 * @param lastCandle
	 */
	private void saveAlert (String exchange, String symbol, String period, String type, String status,
							Candlestick lastCandle, double maxPrice, double minPrice) {
		List<AlertSetting> alertSettingExistList = this.getAlertSetting(exchange, status, period, type);
		if (null != alertSettingExistList && alertSettingExistList.size() > 0) {
			String openflg = alertSettingExistList.get(0).getOpenflg();
			// 提醒开启关闭flg(默认:1开启,0关闭)
			if ("1".equals(openflg)) {
				List<Alert> alertExistList = this.getAlert(exchange, symbol, period, type);
				boolean isNeedSend = false;
				if (alertExistList.size() > 0) {
					Alert alert = alertExistList.get(0);
					if (!status.equals(alert.getStatus())) {
						this.modAlert(alertExistList, status, Double.valueOf(lastCandle.getClose()));
						isNeedSend = true;
					}
				} else {
					this.addAlert(exchange, symbol, period, type, status, Double.valueOf(lastCandle.getClose()));
					isNeedSend = true;
				}
				if (isNeedSend) {
					// 只发生BTC交易对的交叉提醒
					if (symbol.contains("BTC")) {
						List<AlertUser> alertUsers= this.getAlertUsers();
						// 算出裴波那契回调数据
						List<PositionBean> fibonacciList = PostionUtils.getFibonacciRetrace(maxPrice, minPrice);
						for(AlertUser alertUser : alertUsers) {
							this.sendCross(alertUser.getSckey(),exchange, symbol, period, type, status,lastCandle,fibonacciList);
						}
					}
				}
			}
		}
	}

	private List<Alert> getAlert(String exchange, String symbol, String period, String type) {
		AlertCriteria condAlert = new AlertCriteria();
		AlertCriteria.Criteria criteriaAlert = condAlert.createCriteria();
		criteriaAlert.andExchangeEqualTo(exchange);
		criteriaAlert.andSymbolEqualTo(symbol);
		criteriaAlert.andPeriodEqualTo(period);
		criteriaAlert.andTypeEqualTo(type);
		return alertMapper.selectByExample(condAlert);
	}

	private List<AlertSetting> getAlertSetting(String exchange, String status, String period, String type) {
		AlertSettingCriteria condAlert = new AlertSettingCriteria();
		AlertSettingCriteria.Criteria criteriaAlert = condAlert.createCriteria();
		criteriaAlert.andExchangeEqualTo(exchange);
		criteriaAlert.andPeriodEqualTo(period);
		criteriaAlert.andTypeEqualTo(type);
		criteriaAlert.andStatusEqualTo(status);
		return alertSettingMapper.selectByExample(condAlert);
	}

	private void modAlert(List<Alert> list, String status, double close) {
		Alert alert = list.get(0);
		alert.setPrice(close);
		alert.setTime(new Date());
		alert.setStatus(status);
		alert.setCount(alert.getCount() + 1);
		alertMapper.updateByPrimaryKey(alert);
	}

	private void addAlert(String exchange, String symbol, String period, String type, String status, double close) {
		Alert alert = new Alert();
		alert.setExchange(exchange);
		alert.setSymbol(symbol);
		alert.setPeriod(period);
		alert.setType(type);
		alert.setStatus(status);
		alert.setPrice(close);
		alert.setTime(new Date());
		alert.setCount(1);
		alertMapper.insert(alert);
	}

	private void sendCross(String sckey,String exchange, String symbol, String period, String type, String status, Candlestick lastCandle, List<PositionBean> fibonacciList){
		WxSendBean sendBean = new WxSendBean();
		sendBean.setSckey(sckey);
		sendBean.setExchange(exchange);
		sendBean.setPrice(ParseUtils.parsePrice(lastCandle.getClose()));
		sendBean.setSymbol(symbol);
		sendBean.setStatus(status);
		sendBean.setType(type);
		sendBean.setTime(DateUtils.getTodayShort());
		sendBean.setPeriod(period);
		sendBean.setFibonacciList(fibonacciList);
		sendBean.setVolume(lastCandle.getVolume());

		List<WxSendQuotaBean> quotaList = new ArrayList<>();
		List<MacdCross> typeList = this.getSendMacdType(exchange,symbol);
		if (null != typeList && typeList.size() > 0) {
			for (MacdCross macdType : typeList) {

				WxSendQuotaBean quotaBean = new WxSendQuotaBean();
				quotaBean.setType(macdType.getType());

				List<MacdCross> macdList = this.getSendMacdCross(exchange, symbol, macdType.getType());

				List<WxSendQuotaPeriodBean> periodBeanList = new ArrayList<>();

				for (MacdCross macd : macdList) {
					WxSendQuotaPeriodBean periodBean = new WxSendQuotaPeriodBean();
					periodBean.setPeriod(macd.getPeriod());
					periodBean.setTime(DateUtils.formatShortTime(macd.getTime()));
					periodBean.setStatus(macd.getStatus());
					periodBeanList.add(periodBean);
				}
				quotaBean.setPeriodList(periodBeanList);
				quotaList.add(quotaBean);
			}
		}
		sendBean.setQuotaList(quotaList);

		wxSendUtils.sendCross(sendBean);
	}

	/**
	 *
	 * @param exchange
	 * @param symbol
	 * @return
	 */
	private List<MacdCross> getSendMacdType(String exchange, String symbol) {
		MacdCrossCriteria condMacdCross = new MacdCrossCriteria();
		condMacdCross.createCriteria()
				.andExchangeEqualTo(exchange)
				.andSymbolEqualTo(symbol);
		condMacdCross.setDistinct(true);
		condMacdCross.setGroupByClause("type");
		return macdCrossMapper.selectByExample(condMacdCross);
	}

	/**
	 *
	 * @param exchange
	 * @param symbol
	 * @return
	 */
	private List<MacdCross> getSendMacdCross(String exchange, String symbol, String type) {
		MacdCrossCriteria condMacdCross = new MacdCrossCriteria();
		condMacdCross.createCriteria()
				.andExchangeEqualTo(exchange)
				.andSymbolEqualTo(symbol)
				.andTypeEqualTo(type);
		condMacdCross.setOrderByClause("period asc");
		return macdCrossMapper.selectByExample(condMacdCross);
	}


	private List<AlertUser> getAlertUsers() {
		AlertUserCriteria cond = new AlertUserCriteria();
		cond.createCriteria()
				.andOpenflgEqualTo("1");
		return alertUserMapper.selectByExample(cond);
	}
}

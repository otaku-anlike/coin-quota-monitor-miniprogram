package wang.raye.springboot.server.impl;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.TickerPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import wang.raye.springboot.bean.*;
import wang.raye.springboot.model.*;
import wang.raye.springboot.model.mapper.*;
import wang.raye.springboot.server.CurrentPriceServer;
import wang.raye.springboot.server.MacdCrossServer;
import wang.raye.springboot.utils.*;

import java.util.*;

/**
 * 用户相关数据库操作实现类
 * @author Raye
 * @since 2016年10月11日19:29:02
 */
@Repository
public class MacdCrossServerImpl implements MacdCrossServer{

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
	private WxSendUtils wxSendUtils;

	@Autowired
	private QuotaUtils quotaUtils;
	@Autowired
	private QuotaCrossUtils quotaCrossUtils;

	@Value("${self.exchange.binance}")
	private String BINANCE;
	@Value("${self.type.macd}")
	private String MACD;
	@Value("${self.type.kdj}")
	private String KDJ;
	@Value("${self.sckey}")
	private String SCKEY;

	@Override
	public boolean binanceMacdCross(CandlestickInterval periods) {
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");
		BinanceApiRestClient client = factory.newRestClient();

		SymbolsCriteria cond = new SymbolsCriteria();
		SymbolsCriteria.Criteria criteria = cond.createCriteria();
		criteria.andExchangeEqualTo(BINANCE);
		List<Symbols> symbolsList = symbolsMapper.selectByExample(cond);
		for (Symbols symbols:symbolsList) {
			//币种名
			String symbol = symbols.getSymbol();
			// k线周期
			String period = periods.getIntervalId();
			//调api获取k线
			List<Candlestick> candlestickList = client.getCandlestickBars(symbols.getSymbol(), periods);
			// 收盘价
			double close = Double.valueOf(candlestickList.get(candlestickList.size()-1).getClose());

			//MACD
			List<MacdBean> macdBeanList =  quotaUtils.getMACD(12,26,9,candlestickList);
			String statusMacd = quotaCrossUtils.getMACDCross(macdBeanList, candlestickList);
			MacdBean lastMacdBean = macdBeanList.get(macdBeanList.size() - 1);
			// 保存MACD数据
			this.saveCross(BINANCE, symbol, period, MACD, statusMacd, close,lastMacdBean.getDif(),lastMacdBean.getDea(),lastMacdBean.getBar());

			//KDJ
			List<KdjBean> kdjBeanList = quotaUtils.getKDJ(9,3,3,candlestickList);
			String statusKdj = quotaCrossUtils.getKdjCross(kdjBeanList, candlestickList);
			KdjBean lastKdjBean = kdjBeanList.get(kdjBeanList.size() - 1);
			// 保存KDJ数据
			this.saveCross(BINANCE, symbol, period, KDJ, statusKdj, close, lastKdjBean.getK(), lastKdjBean.getD(), lastKdjBean.getJ());

			try {
				Thread.sleep(2000);
			}
			catch (InterruptedException e) {
				log.error("线程异常终止...");
			}
		}

		return true;
	}

	/**
	 * 保存交叉指标数据
	 * @param exchange 交易所
	 * @param symbol 币种
	 * @param period k线周期
	 * @param type 指标类型
	 * @param status 指标状态
	 * @param close 收盘价
	 */
	private void saveCross (String exchange, String symbol, String period, String type, String status,
							double close, double quota1, double quota2, double quota3) {
		List<MacdCross> existList = this.getMacdCross(exchange, symbol, period, type);

		if (existList.size() > 0) {
			this.modMacdCross(existList,status, close, quota1, quota2, quota3);
		} else {
			this.addMacdCross(exchange, symbol, period, type, status, close, quota1, quota2, quota3);
		}

		// 金叉或者死叉时
		if ("2".equals(status) || "4".equals(status)) {
			List<MacdCrossHistory> macdCrossHistoryExistList = this.getMacdCrossHistory(exchange, symbol, period, type, status);
			if (macdCrossHistoryExistList.size() > 0) {
				this.modMacdCrossHistory(macdCrossHistoryExistList,close, quota1, quota2, quota3);
			} else {
				this.addMacdCrossHistory(exchange, symbol, period, type, status, close, quota1, quota2, quota3);
			}

			// 保存提醒数据
			this.saveAlert( exchange, symbol,period, type, status, close);
		}
	}

	/**
	 *  保存提醒数据
	 * @param exchange
	 * @param symbol
	 * @param period
	 * @param type
	 * @param status
	 * @param close
	 */
	private void saveAlert (String exchange, String symbol, String period, String type, String status, double close) {
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
						this.modAlert(alertExistList, status, close);
						isNeedSend = true;
					}
				} else {
					this.addAlert(exchange, symbol, period, type, status, close);
					isNeedSend = true;
				}
				if (isNeedSend) {
					// 只发生BTC交易对的交叉提醒
					if (symbol.indexOf("BTC") > 0) {
						this.sendCross(SCKEY,exchange, symbol, period, type, status,close);
					}
				}
			}
		}
	}

	/**
	 *
	 * @param exchange
	 * @param symbol
	 * @param period
	 * @param type
	 * @return
	 */
	private List<MacdCross> getMacdCross(String exchange, String symbol, String period, String type) {
		MacdCrossCriteria condMacdCross = new MacdCrossCriteria();
		MacdCrossCriteria.Criteria criteriaMacdCross = condMacdCross.createCriteria();
		criteriaMacdCross.andExchangeEqualTo(exchange);
		criteriaMacdCross.andSymbolEqualTo(symbol);
		criteriaMacdCross.andPeriodEqualTo(period);
		criteriaMacdCross.andTypeEqualTo(type);
		return macdCrossMapper.selectByExample(condMacdCross);
	}

	/**
	 *
	 * @param list
	 * @param status
	 * @param close
	 */
	private void modMacdCross(List<MacdCross> list, String status,
							  double close, double quota1, double quota2, double quota3) {
		MacdCross macdCross = list.get(0);
		macdCross.setStatus(status);
		macdCross.setPrice(close);
		macdCross.setTime(new Date());
		macdCross.setQuota1(quota1);
		macdCross.setQuota2(quota2);
		macdCross.setQuota3(quota3);
		macdCrossMapper.updateByPrimaryKey(macdCross);
	}

	/**
	 *
	 * @param exchange
	 * @param symbol
	 * @param period
	 * @param type
	 * @param status
	 * @param close
	 */
	private void addMacdCross (String exchange, String symbol, String period, String type, String status,
							   double close, double quota1, double quota2, double quota3) {
		MacdCross macdCross = new MacdCross();
		macdCross.setExchange(exchange);
		macdCross.setSymbol(symbol);
		macdCross.setPeriod(period);
		macdCross.setType(type);
		macdCross.setStatus(status);
		macdCross.setPrice(close);
		macdCross.setTime(new Date());
		macdCross.setQuota1(quota1);
		macdCross.setQuota2(quota2);
		macdCross.setQuota3(quota3);
		macdCrossMapper.insert(macdCross);
	}

	/**
	 *
	 * @param exchange
	 * @param symbol
	 * @param period
	 * @param type
	 * @param status
	 * @return
	 */
	private List<MacdCrossHistory> getMacdCrossHistory (String exchange, String symbol, String period, String type, String status) {
		MacdCrossHistoryCriteria condMacdCrossHistory = new MacdCrossHistoryCriteria();
		MacdCrossHistoryCriteria.Criteria criteriaMacdCrossHistory = condMacdCrossHistory.createCriteria();
		criteriaMacdCrossHistory.andExchangeEqualTo(exchange);
		criteriaMacdCrossHistory.andSymbolEqualTo(symbol);
		criteriaMacdCrossHistory.andPeriodEqualTo(period);
		criteriaMacdCrossHistory.andTypeEqualTo(type);
		// 交叉状态
		criteriaMacdCrossHistory.andStatusEqualTo(status);
		return macdCrossHistoryMapper.selectByExample(condMacdCrossHistory);
	}

	private void modMacdCrossHistory(List<MacdCrossHistory> list,double close, double quota1, double quota2, double quota3){
		MacdCrossHistory macdCrossHistory = list.get(0);
		macdCrossHistory.setPrice(close);
		macdCrossHistory.setTime(new Date());
		macdCrossHistory.setQuota1(quota1);
		macdCrossHistory.setQuota2(quota2);
		macdCrossHistory.setQuota3(quota3);
		macdCrossHistoryMapper.updateByPrimaryKey(macdCrossHistory);
	}

	private void addMacdCrossHistory(String exchange, String symbol, String period, String type, String status,
									 double close, double quota1, double quota2, double quota3){
		MacdCrossHistory macdCrossHistory = new MacdCrossHistory();
		macdCrossHistory.setExchange(exchange);
		macdCrossHistory.setSymbol(symbol);
		macdCrossHistory.setPeriod(period);
		macdCrossHistory.setType(type);
		macdCrossHistory.setStatus(status);
		macdCrossHistory.setPrice(close);
		macdCrossHistory.setTime(new Date());
		macdCrossHistory.setQuota1(quota1);
		macdCrossHistory.setQuota2(quota2);
		macdCrossHistory.setQuota3(quota3);
		macdCrossHistoryMapper.insert(macdCrossHistory);
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

	private void sendCross(String sckey,String exchange, String symbol, String period, String type, String status, double close){
		WxSendBean sendBean = new WxSendBean();
		sendBean.setSckey(sckey);
		sendBean.setExchange(exchange);
		sendBean.setPrice(ParseUtils.parsePrice(close));
		sendBean.setSymbol(symbol);
		sendBean.setStatus(status);
		sendBean.setType(type);
		sendBean.setTime(DateUtils.getTodayShort());
		sendBean.setPeriod(period);

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
}

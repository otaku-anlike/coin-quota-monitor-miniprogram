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
import wang.raye.springboot.bean.MacdBean;
import wang.raye.springboot.model.*;
import wang.raye.springboot.model.mapper.AlertMapper;
import wang.raye.springboot.model.mapper.MacdCrossHistoryMapper;
import wang.raye.springboot.model.mapper.MacdCrossMapper;
import wang.raye.springboot.model.mapper.SymbolsMapper;
import wang.raye.springboot.server.CurrentPriceServer;
import wang.raye.springboot.server.MacdCrossServer;
import wang.raye.springboot.utils.QuotaUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private RestOperations restOperations;

	@Autowired
	private QuotaUtils quotaUtils;

	@Value("${self.exchange.binance}")
	private String BINANCE;
	@Value("${self.type.macd}")
	private String MACD;

	@Override
	public boolean binanceMacdCross(CandlestickInterval period) {
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");
		BinanceApiRestClient client = factory.newRestClient();

		SymbolsCriteria cond = new SymbolsCriteria();
		SymbolsCriteria.Criteria criteria = cond.createCriteria();
		criteria.andExchangeEqualTo(BINANCE);
		List<Symbols> symbolsList = symbolsMapper.selectByExample(cond);
		for (Symbols symbol:symbolsList) {
			List<Candlestick> candlestickList = client.getCandlestickBars(symbol.getSymbol(), period);
			List<MacdBean> macdBeanList =  quotaUtils.getMACD(12,26,9,candlestickList);
			String status = quotaUtils.getMACDCross(macdBeanList, candlestickList);

			MacdCrossCriteria condMacdCross = new MacdCrossCriteria();
			MacdCrossCriteria.Criteria criteriaMacdCross = condMacdCross.createCriteria();
			criteriaMacdCross.andExchangeEqualTo(BINANCE);
			criteriaMacdCross.andSymbolEqualTo(symbol.getSymbol());
			criteriaMacdCross.andPeriodEqualTo(period.getIntervalId());
			criteriaMacdCross.andTypeEqualTo(MACD);
			List<MacdCross> macdCrossExistList = macdCrossMapper.selectByExample(condMacdCross);
			if (macdCrossExistList.size() > 0) {
				MacdCross macdCross = macdCrossExistList.get(0);
				macdCross.setStatus(status);
				macdCross.setPrice(Double.valueOf(candlestickList.get(candlestickList.size()-1).getClose()));
				macdCross.setTime(new Date());
				macdCrossMapper.updateByPrimaryKey(macdCross);
			} else {
				MacdCross macdCross = new MacdCross();
				macdCross.setExchange(BINANCE);
				macdCross.setSymbol(symbol.getSymbol());
				macdCross.setType(MACD);
				macdCross.setPeriod(period.getIntervalId());
				macdCross.setStatus(status);
				macdCross.setPrice(Double.valueOf(candlestickList.get(candlestickList.size()-1).getClose()));
				macdCross.setTime(new Date());
				macdCrossMapper.insert(macdCross);
			}

			if ("2".equals(status) || "4".equals(status)) {
				MacdCrossHistoryCriteria condMacdCrossHistory = new MacdCrossHistoryCriteria();
				MacdCrossHistoryCriteria.Criteria criteriaMacdCrossHistory = condMacdCrossHistory.createCriteria();
				criteriaMacdCrossHistory.andExchangeEqualTo(BINANCE);
				criteriaMacdCrossHistory.andSymbolEqualTo(symbol.getSymbol());
				criteriaMacdCrossHistory.andPeriodEqualTo(period.getIntervalId());
				criteriaMacdCrossHistory.andTypeEqualTo(MACD);
				// 交叉状态 金叉或者死叉时
				criteriaMacdCrossHistory.andStatusEqualTo(status);
				List<MacdCrossHistory> macdCrossHistoryExistList = macdCrossHistoryMapper.selectByExample(condMacdCrossHistory);
				if (macdCrossHistoryExistList.size() > 0) {
					MacdCrossHistory macdCrossHistory = macdCrossHistoryExistList.get(0);
					macdCrossHistory.setPrice(Double.valueOf(candlestickList.get(candlestickList.size()-1).getClose()));
					macdCrossHistory.setTime(new Date());
					macdCrossHistoryMapper.updateByPrimaryKey(macdCrossHistory);
				} else {
					MacdCrossHistory macdCrossHistory = new MacdCrossHistory();
					macdCrossHistory.setExchange(BINANCE);
					macdCrossHistory.setSymbol(symbol.getSymbol());
					macdCrossHistory.setType(MACD);
					macdCrossHistory.setPeriod(period.getIntervalId());
					macdCrossHistory.setStatus(status);
					macdCrossHistory.setPrice(Double.valueOf(candlestickList.get(candlestickList.size()-1).getClose()));
					macdCrossHistory.setTime(new Date());
					macdCrossHistoryMapper.insert(macdCrossHistory);
				}
			}

			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				log.error("线程异常终止...");
			}
		}

		return false;
	}
}

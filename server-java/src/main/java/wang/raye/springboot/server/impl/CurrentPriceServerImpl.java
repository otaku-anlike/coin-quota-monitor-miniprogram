package wang.raye.springboot.server.impl;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import wang.raye.springboot.model.Alert;
import wang.raye.springboot.model.Symbols;
import wang.raye.springboot.model.SymbolsCriteria;
import wang.raye.springboot.model.User;
import wang.raye.springboot.model.mapper.AlertMapper;
import wang.raye.springboot.model.mapper.SymbolsMapper;
import wang.raye.springboot.model.mapper.UserMapper;
import wang.raye.springboot.server.CurrentPriceServer;
import wang.raye.springboot.server.UserServer;

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
public class CurrentPriceServerImpl implements CurrentPriceServer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SymbolsMapper symbolsMapper;
	@Autowired
	private AlertMapper alertMapper;

	@Autowired
	private RestOperations restOperations;

	@Value("${self.exchange.binance}")
	private String BINANCE;
	@Value("${self.type.new}")
	private String NEW;

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
	public boolean binancePrice() {
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");
		BinanceApiRestClient client = factory.newRestClient();
		List<TickerPrice> allPrices = client.getAllPrices();

		SymbolsCriteria cond = new SymbolsCriteria();
		SymbolsCriteria.Criteria criteria = cond.createCriteria();
		criteria.andExchangeEqualTo(BINANCE);
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
					alert.setExchange(BINANCE);
					alert.setType(NEW);
					alert.setSymbol(tickerPrice.getSymbol());
					alert.setTime(new Date());
					alertMapper.insert(alert);

					//发微信
                    this.wxSend(BINANCE,tickerPrice.getSymbol(),tickerPrice.getPrice());
                    log.info(BINANCE+"上新币["+tickerPrice.getSymbol()+"]");
                    log.info("新币["+tickerPrice.getSymbol()+"]目前价格:"+tickerPrice.getPrice());
				}

				//判断币种是否存在
//				SymbolsCriteria condSymbol = new SymbolsCriteria();
//				SymbolsCriteria.Criteria criteriaSymbol = condSymbol.createCriteria();
//				criteriaSymbol.andExchangeEqualTo(BINANCE);
//				criteriaSymbol.andSymbolEqualTo(tickerPrice.getSymbol());
//				List<Symbols> symbolCheckList = symbolsMapper.selectByExample(condSymbol);
//				if (symbolCheckList.size() > 0) {
				if (isNew) {
					Symbols record = new Symbols();
					record.setExchange(BINANCE);
					record.setSymbol(tickerPrice.getSymbol());
					record.setPrice(Double.valueOf(tickerPrice.getPrice()));
					record.setTime(new Date());
					symbolsMapper.insert(record);
				} else {
					updateSymbol.setPrice(Double.valueOf(tickerPrice.getPrice()));
                    updateSymbol.setTime(new Date());
					symbolsMapper.updateByPrimaryKey(updateSymbol);
				}
			}
//		}

		return false;
	}
}

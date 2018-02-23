package wang.raye.springboot.server;

import com.binance.api.client.domain.market.CandlestickInterval;
import wang.raye.springboot.model.Alert;
import wang.raye.springboot.model.MacdCross;
import wang.raye.springboot.model.MacdCrossHistory;

import java.util.List;

/**
 * macd交叉
 * @author Raye
 * @since 2016年9月21日20:57:39
 */
public interface CrossApiServer {

	/**
	 *
	 * @param exchange
	 * @param type
	 * @param period
	 * @param status
	 * @return
	 */
	public List<MacdCross> findMacdCrossList(String exchange, String type, String period, String status);

	/**
	 *
	 * @param id
	 * @return
	 */
	public MacdCross findMacdCrossById(int id);

	/**
	 *
	 * @param exchange
	 * @param type
	 * @param period
	 * @param status
	 * @param symbol
	 * @return
	 */
	public MacdCross findMacdCrossBySymbolLike(String exchange, String type, String period, String status, String symbol);

	/**
	 *
	 * @param exchange
	 * @param type
	 * @param period
	 * @param status
	 * @param limitHour
	 * @return
	 */
	public List<String> findAlertList(String exchange, String type, String period, String status, Integer limitHour);

}

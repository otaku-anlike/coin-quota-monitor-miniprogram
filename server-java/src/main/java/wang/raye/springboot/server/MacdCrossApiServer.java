package wang.raye.springboot.server;

import com.binance.api.client.domain.market.CandlestickInterval;
import wang.raye.springboot.model.MacdCross;

import java.util.List;

/**
 * macd交叉
 * @author Raye
 * @since 2016年9月21日20:57:39
 */
public interface MacdCrossApiServer {

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

}

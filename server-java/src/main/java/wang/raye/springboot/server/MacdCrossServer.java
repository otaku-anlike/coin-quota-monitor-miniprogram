package wang.raye.springboot.server;

import com.binance.api.client.domain.market.CandlestickInterval;
import de.elbatya.cryptocoins.bittrexclient.api.model.publicapi.BittrexInterval;

/**
 * macd交叉
 * @author Raye
 * @since 2016年9月21日20:57:39
 */
public interface MacdCrossServer {
	/**
	 * macd交叉
	 * @param period k线周期
	 * @since 2016年9月21日20:58:17
	 * @return 是否添加成功
	 */
	public boolean binanceMacdCross(String exchange, CandlestickInterval period);

	/**
	 * macd交叉
	 * @param period k线周期
	 * @since 2016年9月21日20:58:17
	 * @return 是否添加成功
	 */
	public boolean bittrexMacdCross(String exchange, BittrexInterval period);

}

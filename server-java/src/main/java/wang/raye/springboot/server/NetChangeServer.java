package wang.raye.springboot.server;

import com.binance.api.client.domain.market.CandlestickInterval;
import de.elbatya.cryptocoins.bittrexclient.api.model.publicapi.BittrexInterval;

/**
 * 涨跌幅监测提醒
 * @author Raye
 * @since 2016年9月21日20:57:39
 */
public interface NetChangeServer {
	/**
	 * 涨跌幅监测提醒
	 * @param exchange 交易所
	 * @param period k线周期
	 * @since 2016年9月21日20:58:17
	 * @return 是否添加成功
	 */
	public boolean binanceNetChange(String exchange, CandlestickInterval period);

	/**
	 * 涨跌幅监测提醒
	 * @param exchange 交易所
	 * @param period k线周期
	 * @since 2016年9月21日20:58:17
	 * @return 是否添加成功
	 */
	public boolean bittrexNetChange(String exchange, BittrexInterval period);


}

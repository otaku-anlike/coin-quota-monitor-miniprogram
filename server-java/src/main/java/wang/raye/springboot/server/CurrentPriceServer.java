package wang.raye.springboot.server;

import com.binance.api.client.domain.market.TickerPrice;
import wang.raye.springboot.model.User;

import java.util.List;

/**
 * 实时价格
 * @author Raye
 * @since 2016年9月21日20:57:39
 */
public interface CurrentPriceServer {
	/**
	 * 更新币安的实时价格
	 * @since 2016年9月21日20:58:17
	 * @return 是否添加成功
	 */
	public boolean binancePrice();

}

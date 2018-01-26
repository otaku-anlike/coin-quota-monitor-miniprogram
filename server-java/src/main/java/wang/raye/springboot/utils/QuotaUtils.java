package wang.raye.springboot.utils;

import com.binance.api.client.domain.market.Candlestick;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import wang.raye.springboot.bean.MacdBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 指标计算工具类
 * @author Raye
 * @since 2016年10月11日19:29:02
 */

@Component
public class QuotaUtils {


	public List<MacdBean> getMACD(int shortValue, int longValue, int mValue, List<Candlestick> list){
		List<MacdBean> resultList = new ArrayList<>();

		double emaShort = 0.0;
		double emaLong = 0.0;
		double macd;
		double dif;
		double dea = 0.0;
		for (int i = 0; i < list.size(); i++) {
			MacdBean bean = new MacdBean();
			double close = Double.valueOf(list.get(i).getClose());
			if (i == 0) {
				emaShort = close;
				emaLong = close;
				dif = 0;
				dea = 0;
				macd = 0;
			} else {
				emaShort = (emaShort * (shortValue - 1) + close * 2) / (shortValue + 1);
				emaLong = (emaLong * (longValue - 1) + close * 2) / (longValue + 1);
				dif = emaShort - emaLong;
				dea = (dea * (mValue - 1) + dif * 2) / (mValue + 1);
				macd = (dif - dea) * 2;
			}
			bean.setDif(dif);
			bean.setDea(dea);
			bean.setMacd(macd);
			resultList.add(bean);
		}
		return resultList;
	}

	public String getMACDCross(List<MacdBean> macdBeanList, List<Candlestick> candlestickList){
		String result = "1";//空仓等待';
		// var macd = TA.MACD(records,12,26,9);//调用指标函数， 参数为MACD 默认的参数。
		// var output = kline.chartMgr._indic._outputs;
//		double dif = macdBean.dif; //dif线
//		var dea = macdBean.dea; //dea线
//		var column = macdBean.column; // MACD柱
		int len = candlestickList.size(); //K线周期长度
		MacdBean last = macdBeanList.get(len - 1);
		MacdBean second  = macdBeanList.get(len - 2);

		// if( (dif[len-1] > 0 && dea[len-1] > 0) && dif[len-1] > dea[len-1] && dif[len-2] < dea[len-2] && column[len-1] > 0.2 ){
		if (last.getDif() > last.getDea() && second.getDif() < second.getDea()) {
			//判断金叉条件：dif 与 dea 此刻均大于0 ， 且dif由下上穿dea ， 且 MACD量柱大于0.2
			//            return 1; //返回1 代表 金叉信号。
			result = "2";//'金叉';
		}
		// if( (dif[len-1] < 0 && dea[len-1] < 0) && dif[len-1] < dea[len-1] && dif[len-2] > dea[len-2] && column[len-1] < -0.2 ){
		else if (last.getDif() < last.getDea() && second.getDif() > second.getDea()) {
			//判断死叉条件：
			//            return 2;//返回2 代表 死叉信号。
			result = "4";//'死叉';
		} else if (last.getDif() > last.getDea() && second.getDif() > second.getDea()) {
			//判断死叉条件：
			//            return 2;//返回2 代表 死叉信号。
			result = "3";//'继续持有';
		}
		return result;
	}


}

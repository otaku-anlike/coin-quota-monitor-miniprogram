package wang.raye.springboot.utils;

import com.binance.api.client.domain.market.Candlestick;
import org.springframework.stereotype.Component;
import wang.raye.springboot.bean.KdjBean;
import wang.raye.springboot.bean.MacdBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 指标计算交叉工具类
 * @author Raye
 * @since 2016年10月11日19:29:02
 */

@Component
public class QuotaCrossUtils {


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

	public String getKdjCross(List<KdjBean> kdjBeanList, List<Candlestick> candlestickList){
		String result = "1";//空仓等待';
		int len = candlestickList.size(); //K线周期长度
		KdjBean last = kdjBeanList.get(len - 1);
		KdjBean second  = kdjBeanList.get(len - 2);

		// CROSS(K,D) AND CROSS(J,D);
		// CROSS(KDJ.K,KDJ.D) AND BETWEEN(KDJ.K,45,50) AND BETWEEN(KDJ.D,45,50);
		if ((last.getK() > last.getD() && second.getK() < second.getD())
				&& (last.getJ() > last.getD() && second.getJ() < second.getD())) {
			result = "2";//'金叉';
		}
		else if ((last.getK() < last.getD() && second.getK() > second.getD())
				&& (last.getJ() < last.getD() && second.getJ() > second.getD())) {
			//判断死叉条件：
			//            return 2;//返回2 代表 死叉信号。
			result = "4";//'死叉';
		} else if ((last.getK() > last.getD() && second.getK() > second.getD())
				&& (last.getJ() > last.getD() && second.getJ() > second.getD())) {
			//判断死叉条件：
			//            return 2;//返回2 代表 死叉信号。
			result = "3";//'继续持有';
		} else if (last.getJ() > 100) {
			//判断死叉条件：
			//            return 2;//返回2 代表 死叉信号。
			result = "5";//'超买';
		} else if (last.getJ() < 0) {
			//判断死叉条件：
			//            return 2;//返回2 代表 死叉信号。
			result = "6";//'超卖';
		}
		return result;
	}


}

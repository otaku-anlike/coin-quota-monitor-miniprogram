package wang.raye.springboot.utils;

import com.binance.api.client.domain.market.Candlestick;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wang.raye.springboot.bean.DojiBean;
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

	@Value("${self.ratio.doji}")
	private double DOJI;
	@Value("${self.ratio.changes}")
	private double CHANGE;
	@Value("${self.ratio.volatile}")
	private double Volatile;


	public String getMACDCross(List<MacdBean> macdBeanList, int len){
//		String result = "1";//空仓等待';
		String result = "4";//空仓等待'状态改为死叉;
		// var macd = TA.MACD(records,12,26,9);//调用指标函数， 参数为MACD 默认的参数。
		// var output = kline.chartMgr._indic._outputs;
//		double dif = macdBean.dif; //dif线
//		var dea = macdBean.dea; //dea线
//		var column = macdBean.column; // MACD柱
//		int len = candlestickList.size(); //K线周期长度
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
//			result = "3";//'继续持有';
			result = "2";//'继续持有'状态改为金叉;
		}
		return result;
	}

	public String getKdjCross(List<KdjBean> kdjBeanList, int len){
//		String result = "1";//空仓等待';
		String result = "4";//空仓等待'状态改为死叉;
//		int len = candlestickList.size(); //K线周期长度
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
//			result = "3";//'继续持有';
			result = "2";//'继续持有'状态改为金叉;
		} else if (last.getJ() > 96) {
			//判断死叉条件：
			//            return 2;//返回2 代表 死叉信号。
			result = "5";//'超买';
		} else if (last.getJ() < 4) {
			//判断死叉条件：
			//            return 2;//返回2 代表 死叉信号。
			result = "6";//'超卖';
		}
		return result;
	}

	/**
	 * 缩量十字星擒牛法
	 * 五线上:=C>=MAX(MA(C,5),MAX(MA(C,10),MAX(MA(C,20),MAX(MA(C,60),MA(C,120)))));
	 *
	 * 缩量:=V<=HHV(V,10)*0.5;
	 *
	 * 十字星:=ABS(C-O)/C<=0.002 AND C/REF(C,1)<1.02;
	 *
	 * 五线上 AND 缩量 AND 十字星;
	 * @param dojiBeanList
	 * @param candlestickList
	 * @return
	 */
	public String getDojiCross(List<DojiBean> dojiBeanList, List<Candlestick> candlestickList){
		String result = "1";//空仓等待';
		int len = candlestickList.size(); //K线周期长度
		DojiBean last = dojiBeanList.get(len - 1);
		DojiBean second  = dojiBeanList.get(len - 2);
		Candlestick lastCandle = candlestickList.get(len - 1);
		Candlestick secondCandle  = candlestickList.get(len - 2);

		// C/REF(C,1)<1.02
		double change = Double.valueOf(lastCandle.getClose())/Double.valueOf(secondCandle.getClose()) - 1;

		// 缩量 AND 十字星
		if (second.getDoji() <= DOJI && Double.valueOf(secondCandle.getVolume()) <= second.getHhv()) {
			if(change >= CHANGE) {
				result = "2";//'金叉';
			} else if(change <= CHANGE * -1) {
				result = "4";//'死叉';
			}
		}
		return result;
	}

	public String getVolatile(double lastHigh, double lastLow, double secondClose) {
		String result = "";
		double difHigh = lastHigh/secondClose - 1;
		double difLow = lastLow/secondClose - 1;
		if (difHigh >= Volatile) {
			result = "7";//暴涨
		} else if (difLow <= Volatile * -1) {
			result = "8";//暴跌
		}
		return result;
	}

	//箱顶:=PEAK(CLOSE,13,1)*0.98 ;
	//箱底:=TROUGH(CLOSE,8,1)*1.02;
	//AA:=CROSS(REF(C,1),箱顶);
	//BB:=CROSS(OPEN,箱顶);
	//DD:=C>O;
	//突破:AA AND BB AND DD;


}

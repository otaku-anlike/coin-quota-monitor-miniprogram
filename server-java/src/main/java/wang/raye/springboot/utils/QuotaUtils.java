package wang.raye.springboot.utils;

import com.binance.api.client.domain.market.Candlestick;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import wang.raye.springboot.bean.KdjBean;
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


	/**
	 * 	 MACD的图表中包含了dif，dea以及macd三个数据，要计算macd，首先要计算ema，ema表示的是n日的移动平均。
	 * 	 例如：12日EMA的计算：EMA（12） = MA（close，12）
	 * 	 以下以12为快速移动平均值，26为慢速平均值，则 差离值（DIF）的计算：
	 * 	 DIF = EMA（12） - EMA（26） 。
	 * 	 根据差离值计算其9日的EMA，即离差平均值，是所求的DEA值。
	 * 	 DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
	 * 	 MACD = （DIF-DEA）*2。
	 * @param shortValue 12为快速移动平均值
	 * @param longValue 26为慢速平均值
	 * @param mValue
	 * @param list k线数据
	 * @return
	 */
	public List<MacdBean> getMACD(int shortValue, int longValue, int mValue, List<Candlestick> list){
		List<MacdBean> resultList = new ArrayList<>();

		double emaShort = 0.0;
		double emaLong = 0.0;
		double bar;
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
				bar = 0;
			} else {
				emaShort = (emaShort * (shortValue - 1) + close * 2) / (shortValue + 1);
				emaLong = (emaLong * (longValue - 1) + close * 2) / (longValue + 1);
				dif = emaShort - emaLong;
				dea = (dea * (mValue - 1) + dif * 2) / (mValue + 1);
				bar = (dif - dea) * 2;
			}
			bean.setDif(dif);
			bean.setDea(dea);
			bean.setBar(bar);
			resultList.add(bean);
		}
		return resultList;
	}

	/**
	 *	  以KDJ（9，3，3）为例，括号内为传入的参数
	 *	 （1）计算周期的RSV值
	 *	 RSV = （C（9）－L（9））/（H（9）－L（9））×100
	 *	 公式中，C（9）为第9日收盘价；L（9）为9日内的最低价；（H（9）为9日内的最高价。
	 *	 （2）计算K值，D值，J值
	 *	 当日K值=2/3×前一日K值+1/3×当日RSV
	 *	 当日D值=2/3×前一日D值+1/3×当日K值
	 *	 J值=3*当日K值-2*当日D值
	 *	 （注：第一天的K值我取的是33.33，D值我取的是11.11，J值我取的是77.78，当然它们要取50也可以）
	 *  （注：KDJ的值均须在0~100内）
	 * @param n 9为K值的时间周期，即9天的强弱指标
	 * @param m1 3为D值的时间周期，即为3日的K值
	 * @param m2 3是J值的时间周期，即为3日的D
	 * @param list k线数据
	 */
	public List<KdjBean> getKDJ (int n, int m1, int m2,List<Candlestick> list) {
		List<KdjBean> resultList = new ArrayList<>();

		if (list==null || list.size() < 1){
			return null;
		}
		double[] mK = new double[list.size()];  //K值
		double[] mD = new double[list.size()];  //D值
		double jValue;                                      //J值
		double highValue = Double.valueOf(list.get(0).getHigh());
		double lowValue = Double.valueOf(list.get(0).getLow());
		int highPosition = 0;           //记录最高价的位置
		int lowPosition = 0;        //记录最低价位置
		double rSV = 0.0;
		for (int i = 0; i < list.size(); i++) {
			KdjBean kdjBean = new KdjBean();
			if (i == 0) {
				mK[0] = 33.33;
				mD[0] = 11.11;
				jValue = 77.78;
			} else {
				//对最高价和最低价赋值
				if (highValue <= Double.valueOf(list.get(i).getHigh())) {
					highValue = Double.valueOf(list.get(i).getHigh());
					highPosition = i;
				}
				if (lowValue >= Double.valueOf(list.get(i).getLow())) {
					lowValue = Double.valueOf(list.get(i).getLow());
					lowPosition = i;
				}
				if (i > (n - 1)) {
					//判断存储的最高价是否高于当前最高价
					if (highValue > Double.valueOf(list.get(i).getHigh())) {
						//判断最高价是不是在最近n天内，若不在最近n天内，则从最近n天找出最高价并赋值
						if (highPosition < (i - (n - 1))) {
							highValue = Double.valueOf(list.get(i - (n - 1)).getHigh());
							for (int j = (i - (n - 2)); j <= i; j++) {
								if (highValue <= Double.valueOf(list.get(j).getHigh())) {
									highValue = Double.valueOf(list.get(j).getHigh());
									highPosition = j;
								}
							}
						}
					}
					if ((lowValue < Double.valueOf(list.get(i).getLow()))) {
						if (lowPosition < i - (n - 1)) {
							lowValue = Double.valueOf(list.get(i - (n - 1)).getLow());
							for (int k = i - (n - 2); k <= i; k++) {
								if (lowValue >= Double.valueOf(list.get(k).getLow())) {
									lowValue = Double.valueOf(list.get(k).getLow());
									lowPosition = k;
								}
							}
						}
					}
				}
				if (highValue != lowValue) {
					rSV = (Double.valueOf(list.get(i).getClose()) - lowValue) / (highValue - lowValue) * 100;
				}
				mK[i] = (mK[i - 1] * (m1 - 1) + rSV) / m1;
				mD[i] = (mD[i - 1] * (m2 - 1) + mK[i]) / m2;
				jValue = 3 * mK[i] - 2 * mD[i];
			}
			kdjBean.setK(this.getLimitKdj(mK[i]));
			kdjBean.setD(this.getLimitKdj(mD[i]));
			kdjBean.setJ(this.getLimitKdj(jValue));
			resultList.add(kdjBean);
//			this.addLimitEntry(i, mK[i], firstData);
//			this.addLimitEntry(i, mD[i], secondData);
//			this.addLimitEntry(i, jValue, thirdData);
		}
		return  resultList;
	}

	private double getLimitKdj(double value) {
//		if (value > 100) {
//			value = 100;
//		} else if (value < 0) {
//			value = 0;
//		}
		return value;
	}


}

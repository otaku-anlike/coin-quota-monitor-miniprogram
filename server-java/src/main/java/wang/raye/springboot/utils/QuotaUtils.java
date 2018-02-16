package wang.raye.springboot.utils;

import com.binance.api.client.domain.market.Candlestick;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import wang.raye.springboot.bean.DojiBean;
import wang.raye.springboot.bean.KdjBean;
import wang.raye.springboot.bean.MacdBean;
import wang.raye.springboot.bean.RsiBean;

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

	public List<DojiBean> getDoji(int n, int m1, int m2, List<Candlestick> list) {
		List<DojiBean> resultList = new ArrayList<>();

		if (list==null || list.size() < 1){
			return null;
		}
		List<Double> maxs = HHV(list, n);
		List<KdjBean> kdjList = this.getKDJ(n, m1, m2,list);
		for (int i = 0; i < list.size(); i++) {
			DojiBean dojiBean = new DojiBean();

			double doji = Math.abs(Double.valueOf(list.get(i).getOpen())/Double.valueOf(list.get(i).getClose()) - 1);
			dojiBean.setDoji(doji);

			double hhv = maxs.get(i) * 0.5;
			dojiBean.setHhv(hhv);

			double j = kdjList.get(i).getJ();
			dojiBean.setJ(j);

			resultList.add(dojiBean);
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

	/**
	 * SMA(C,N,M) = (M*C+(N-M)*Y')/N
	 * LC := REF(CLOSE,1);
	 * RSI$1:SMA(MAX(CLOSE-LC,0),N1,1)/SMA(ABS(CLOSE-LC),N1,1)*100;
	 */
	public List countRSIdatas(List<Candlestick> list, int days) {
		List rsiList = new ArrayList();
		if (list == null)
			return null;
		if (days > list.size())
			return null;

		double smaMax = 0, smaAbs = 0;//默认0
		double lc = 0;//默认0
		double close = 0;
		double rsi = 0;
		for (int i = 1; i < list.size(); i++) {
			Candlestick entity = list.get(i);
			lc = Double.valueOf(list.get(i - 1).getClose());
			close = Double.valueOf(entity.getClose());
			smaMax = this.countSMA(Math.max(close - lc, 0d), days, 1, smaMax);
			smaAbs = this.countSMA(Math.abs(close - lc), days, 1, smaAbs);
			rsi = smaMax / smaAbs * 100;

			rsiList.add(RsiBean.builder().rsi(rsi));
		}
//		Log.v(TAG, "rsiList.size()=" + rsiList.size());

//		int size = list.size() - rsiList.size();
//		for (int i = 0; i < size; i++) {
//			rsiList.add(0, new KCandleObj());
//		}
		return rsiList;
	}

	/**
	 * SMA(C,N,M) = (M*C+(N-M)*Y')/N
	 * C=今天收盘价－昨天收盘价    N＝就是周期比如 6或者12或者24， M＝权重，其实就是1
	 *
	 * @param c   今天收盘价－昨天收盘价
	 * @param n   周期
	 * @param m   1
	 * @param sma 上一个周期的sma
	 * @return
	 */
	private double countSMA(double c, double n, double m, double sma) {
		return (m * c + (n - m) * sma) / n;
	}

	/**
	 * 算出ma的值 需要确保和传入的list size一致
	 * @param list 数据集合
	 * @param days 周期
	 * @return   集合数据  MA=N日内的收盘价之和÷N
	 */
	public List<Double> countMA(List<Candlestick> list, int days) {
		if (days < 1) {
			return null;
		}
		if (list == null || list.size() == 0)
			return null;
		int cycle = days;

		if (cycle > list.size()) {
			//设置的指标参数大于数据集合 不用计算
			return null;
		}

		double sum = 0;

		List<Double> ma5Values = new ArrayList<Double>();

		for (int i = cycle - 1; i < list.size(); i++) {
			if (i == cycle - 1) {
				for (int j = i - days + 1; j <= i; j++) {
					sum += Double.valueOf(list.get(j).getClose());
				}
			} else {
				sum = sum + Double.valueOf(list.get(i).getClose())
						- Double.valueOf(list.get(i - days).getClose());
			}
			ma5Values.add(sum / days);
		}
		return ma5Values;
	}

	/**
	 * LLV  n周期内的最低值 取low字段
	 *
	 * @param list 数据集合
	 * @param n 周期
	 * @return
	 */
	public List<Double> LLV(List<Candlestick> list, int n) {
		if (list == null || list.size() == 0)
			return null;
		List<Double> datas = new ArrayList<>();

		double minValue = 0;
		for (int i = n - 1; i < list.size(); i++) {
			for (int j = i - n + 1; j <= i; j++) {
				if (j == i - n + 1) {
					minValue = Double.valueOf(list.get(j).getLow());
				} else {
					minValue = Math.min(minValue, Double.valueOf(list.get(j).getLow()));
				}
			}
			datas.add(minValue);
		}

		return datas;
	}

	/**
	 * HHV n周期内的最大值 取high字段
	 *
	 * @param list 数据集合
	 * @param n 周期
	 * @return
	 */
	public List<Double> HHV(List<Candlestick> list, int n) {
		if (list == null || list.size() == 0)
			return null;
		List<Double> datas = new ArrayList<>();

		double maxValue = Double.valueOf(list.get(0).getVolume());
		for (int i = 0; i < list.size(); i++) {
			if (i < n - 1) {
				maxValue = Double.valueOf(list.get(i).getVolume());
			} else {
				for (int j = i - n + 1; j <= i; j++) {
					if (j == i - n + 1) {
						maxValue = Double.valueOf(list.get(j).getVolume());
					} else {
						maxValue = Math.max(maxValue, Double.valueOf(list.get(j).getVolume()));
					}
				}
			}
			datas.add(maxValue);
		}
		return datas;
	}


	/**
	 * kdj 9,3,3
	 * N:=9; P1:=3; P2:=3;
	 * RSV:=(CLOSE-LLV(LOW,N))/(HHV(HIGH,N)-LLV(LOW,N))*100;
	 * K:SMA(RSV,P1,1);
	 * D:SMA(K,P2,1);
	 * J:3*K-2*D;
	 * @param list 数据集合
	 * @param n 指标周期 9
	 * @param P1 参数值为3
	 * @param P2 参数值为3
	 * @return
	 */
	public List<KdjBean> getKDJLinesDatas(
			List<Candlestick> list, int n, int P1, int P2) {
		if (list == null || list.size() == 0)
			return null;
		int cycle = n;
		if (n > list.size()) {
			return null;
		}
		List<KdjBean> resultList = new ArrayList<>();

//		List kValue = new ArrayList();
//		List dValue = new ArrayList();
//		List jValue = new ArrayList();

		List<Double> maxs = HHV(list, n);
		List<Double> mins = LLV(list, n);
		if (maxs == null || mins == null)
			return null;
		//确保和 传入的list size一致，
//		int size = list.size() - maxs.size();
//		for (int i = 0; i < size; i++) {
//			maxs.add(0, new KCandleObj());
//			mins.add(0, new KCandleObj());
//		}
		double rsv = 0;
		double lastK = 50;
		double lastD = 50;
		for (int i = cycle - 1; i < list.size(); i++) {
			if (i >= maxs.size())
				break;
			if (i >= mins.size())
				break;
			double div = maxs.get(i) - mins.get(i);
			if (div == 0) {
				//使用上一次的
			} else {
				rsv = ((Double.valueOf(list.get(i).getClose()) - mins.get(i))
						/ (div)) * 100;
			}

			double k = countSMA(rsv, P1, 1, lastK);

//			try {
//				BigDecimal big = new BigDecimal(k);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			double d = countSMA(k, P2, 1, lastD);
			double j = 3 * k - 2 * d;
			lastK = k;
			lastD = d;
			KdjBean kdjBean = new KdjBean();
			kdjBean.setK(k);
			kdjBean.setD(d);
			kdjBean.setJ(j);
			resultList.add(kdjBean);
//			kValue.add(new KCandleObj(k));
//			dValue.add(new KCandleObj(d));
//			jValue.add(new KCandleObj(j));
		}


		//确保和 传入的list size一致，
//		size = list.size() - kValue.size();
//		for (int i = 0; i < size; i++) {
//			kValue.add(0, new KCandleObj());
//			dValue.add(0, new KCandleObj());
//			jValue.add(0, new KCandleObj());
//		}
//
//		List<KLineObj> lineDatas = new ArrayList<KLineObj>();
//
//		KLineObj kLine = new KLineObj();
//		kLine.setLineData(kValue);
//		kLine.setTitle("K");
//		kLine.setLineColor(KParamConfig.COLOR_KDJ_K);
//		lineDatas.add(kLine);
//
//		KLineObj dLine = new KLineObj();
//		dLine.setLineData(dValue);
//		dLine.setTitle("D");
//		dLine.setLineColor(KParamConfig.COLOR_KDJ_D);
//		lineDatas.add(dLine);

//        KLineObj jLine = new KLineObj();
//        jLine.setLineData(jValue);
//        jLine.setTitle("J");
//        jLine.setLineColor(KParamConfig.COLOR_KDJ_J);
//        lineDatas.add(jLine);

//		//加两条固定线 30  70
//		KLineObj line30 = new KLineObj();
//		line30.setLineColor(KParamConfig.COLOR_KDJ_30);
//		line30.setLineValue(KParamConfig.VALUE_KDJ_01);
//		lineDatas.add(line30);
//
//		KLineObj line70 = new KLineObj();
//		line70.setLineColor(KParamConfig.COLOR_KDJ_70);
//		line70.setLineValue(KParamConfig.VALUE_KDJ_02);
//		lineDatas.add(line70);

		return resultList;
	}

	/**
	 * SMA(C,N,M) = (M*C+(N-M)*Y')/N
	 * C=今天收盘价－昨天收盘价    N＝就是周期比如 6或者12或者24， M＝权重，其实就是1
	 *
	 * @param c   今天收盘价－昨天收盘价
	 * @param n   周期
	 * @param m   1
	 * @param sma 上一个周期的sma
	 * @return
	 */
//	public double countSMA(double c, double n, double m, double sma) {
//		return (m * c + (n - m) * sma) / n;
//	}


}

package wang.raye.springboot.utils;

import com.binance.api.client.domain.market.Candlestick;
import de.elbatya.cryptocoins.bittrexclient.api.model.publicapi.Candle;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Component
public final class ParseUtils {
    private static String wxShowPattern = "#0.0###############";

    public static String TwoPattern = "#0.0#";

    public ParseUtils() {
    }

//    public static String getDatePattern() {
//        return defaultDatePattern;
//    }

    public static String parseCrossStatus(String statusCode) {
        String status = "";
        if("1".equals(statusCode)) {
            status = "空仓";
        } else if("2".equals(statusCode)) {
            status = "金叉";
        } else if("3".equals(statusCode)) {
            status = "持有";
        } else if("4".equals(statusCode)) {
            status = "死叉";
        } else if("5".equals(statusCode)) {
            status = "超买";
        } else if("6".equals(statusCode)) {
            status = "超卖";
        } else if("7".equals(statusCode)) {
            status = "暴涨";
        } else if("8".equals(statusCode)) {
            status = "暴跌";
        }
        return status;
    }

    public static String parseCrossType(String typeCode) {
        String type = "";
        if("MACD".equals(typeCode)) {
            type = "MACD";
        } else if("KDJ".equals(typeCode)) {
            type = "KDJ";
        } else if("DOJI".equals(typeCode)) {
            type = "十字星";
        }
        return type;
    }

    /**
     * 去掉douelbe的科学计数法E
     * @param price
     * @return
     */
    public static String parsePrice(String price) {
        BigDecimal result=new BigDecimal(price+"");
        BigDecimal setScale = result.setScale(12,BigDecimal.ROUND_HALF_DOWN);
        DecimalFormat df = new DecimalFormat(wxShowPattern);
        return  df.format(setScale);
    }

    /**
     * 去掉douelbe的科学计数法E
     * @param price
     * @return
     */
    public static String parsePrice(double price) {
        BigDecimal result=new BigDecimal(price+"");
        BigDecimal setScale = result.setScale(12,BigDecimal.ROUND_HALF_DOWN);
        DecimalFormat df = new DecimalFormat(wxShowPattern);
        return  df.format(setScale);
    }

    /**
     *
     * @param value
     * @param pattern
     * @return
     */
    public static String decimalFormat(double value, String pattern) {
        return  new DecimalFormat(pattern).format(new BigDecimal(value+""));
    }

    /**
     *
     * @param list
     * @return
     */
    public static List<Candlestick> parseCandlestick(List<Candle> list) {
        List<Candlestick> candlestickList = new ArrayList<>();
        for (Candle candle : list){
            Candlestick candlestick = new Candlestick();
            candlestick.setOpenTime(candle.getT().toLocalTime().toNanoOfDay());
            candlestick.setOpen(String.valueOf(candle.getO()));
            candlestick.setHigh(String.valueOf(candle.getH()));
            candlestick.setLow(String.valueOf(candle.getL()));
            candlestick.setClose(String.valueOf(candle.getC()));
            candlestick.setVolume(String.valueOf(candle.getV()));
            candlestick.setCloseTime(candle.getT().toLocalTime().toNanoOfDay());
//            private String quoteAssetVolume;
//            private Long numberOfTrades;
            candlestick.setTakerBuyBaseAssetVolume(String.valueOf(candle.getBV()));
//            private String takerBuyQuoteAssetVolume;

            candlestickList.add(candlestick);
        }
        return candlestickList;
    }





}
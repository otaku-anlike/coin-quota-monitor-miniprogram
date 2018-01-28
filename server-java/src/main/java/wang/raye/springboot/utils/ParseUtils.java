package wang.raye.springboot.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        }
        return status;
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





}
package wang.raye.springboot.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public final class ParseUtils {
//    private static String defaultDatePattern = "yyyy-MM-dd HH:mm:ss";

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
        }
        return status;
    }

    public static String parsePrice(double price) {
        BigDecimal result=new BigDecimal(price+"");
        return  result.toString();
    }


}
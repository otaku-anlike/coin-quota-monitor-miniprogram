package wang.raye.springboot.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import wang.raye.springboot.bean.PositionBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public final class PostionUtils {
    private static String defaultDatePattern = "yyyy-MM-dd HH:mm:ss";
    private static String defaultDateShortPattern = "MM-dd HH:mm";

    public PostionUtils() {
    }

    public static String getDatePattern() {
        return defaultDatePattern;
    }

    public static String getDateShortPattern() {
        return defaultDateShortPattern;
    }

    /**
     *  斐波那契回调线
     * @param max 最高点
     * @param min 最低点
     * @return
     */
    public static List<PositionBean> getFibonacciRetrace(double max, double min) {
        List<PositionBean> resultList = new ArrayList<>();
        double dif = max - min;
        PositionBean position1 = new PositionBean();
        position1.setPostion("78.6%");
        position1.setPrice(ParseUtils.parsePrice(dif*0.786+min));
        resultList.add(position1);

        PositionBean position2 = new PositionBean();
        position2.setPostion("61.8%");
        position2.setPrice(ParseUtils.parsePrice(dif*0.618+min));
        resultList.add(position2);

        PositionBean position3 = new PositionBean();
        position3.setPostion("50.0%");
        position3.setPrice(ParseUtils.parsePrice(dif*0.5+min));
        resultList.add(position3);

        PositionBean position4 = new PositionBean();
        position4.setPostion("38.2%");
        position4.setPrice(ParseUtils.parsePrice(dif*0.382+min));
        resultList.add(position4);

        PositionBean position5 = new PositionBean();
        position5.setPostion("23.6%");
        position5.setPrice(ParseUtils.parsePrice(dif*0.236+min));
        resultList.add(position5);

        return resultList;
    }


}
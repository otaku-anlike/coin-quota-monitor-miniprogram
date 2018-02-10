package wang.raye.springboot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import wang.raye.springboot.bean.PositionBean;
import wang.raye.springboot.bean.WxSendBean;
import wang.raye.springboot.bean.WxSendQuotaBean;
import wang.raye.springboot.bean.WxSendQuotaPeriodBean;

import java.util.Date;
import java.util.Map;

@Component
public class WxSendUtils {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HttpRequestUtils httpRequestUtils;

    /**
     * 新币发送提示
     * @param sendBean 微信发送bean
     */
    public void sendNew(WxSendBean sendBean){
//        String url = "https://sc.ftqq.com/"+SCU20744Tcae3e700d2845416dcf4b7d48fefd4fc5a68472d8b5e0+".send";
//        Map<String, Object> uriVariables = new HashMap<String, Object>();
//        uriVariables.put("text", exchange+"上新币["+symbol+"]");
//        uriVariables.put("desp", "新币["+symbol+"]目前价格:"+price);
//        try {
//            String result = restOperations.getForObject(url, String.class, uriVariables);
//        } catch (RestClientException e){
//            log.error("发新币微信时异常:"+e.getStackTrace());
//        }

        String url = "https://sc.ftqq.com/"+sendBean.getSckey()+".send";
        String text=sendBean.getExchange()+"上新币["+sendBean.getSymbol()+"]";
        String desp="新币["+sendBean.getSymbol()+"]目前价格:"+sendBean.getPrice()+ "\n";
        desp=desp+DateUtils.getToday();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("text", text);
        params.add("desp", desp);
//        Object result = httpRequestUtils.request(url,HttpMethod.GET, params);
        String result = httpRequestUtils.get(url, params);
//        log.info("微信提醒[新币]发送结果:"+result);
        log.info("上新币微信发送提醒:"+text);
    }

    /**
     * 指标交叉发送提示
     * @param sendBean 微信发送bean
     */
    public void sendCross(WxSendBean sendBean){
        String url = "https://sc.ftqq.com/"+sendBean.getSckey()+".send";
        String text= sendBean.getExchange()+"["+sendBean.getSymbol()+"]" +" ["+sendBean.getType()+"]";// + "📈";
        text = text+ "["+sendBean.getPeriod()+"]" +" ["+ParseUtils.parseCrossStatus(sendBean.getStatus())+"]";
//        String desp=" ["+sendBean.getSymbol()+"]  ->  "+sendBean.getType()+"  ->  "+sendBean.getPeriod()+"  ->  "+"["+status+"]";
//        desp=desp+"   <br />       <hr/>           ==========                   <br /> ";
//        desp=desp+"> 1. ["+sendBean.getSymbol()+"]目前价格:["+sendBean.getPrice() + "]";
//        desp=desp+"当前时间:["+DateUtils.getToday() + "]";

        //--------------------------
//        String desp="# 这是一级标题\n";
//        desp=desp+"## 这是二级标题\n";
//        desp=desp+"### 这是三级标题\n";
//        desp=desp+"这是高阶标题（效果和一级标题一样 ）\n" +
//                "========";
//        desp=desp+"> 这是一级引用\n";
//        desp=desp+">>这是二级引用\n";
//        desp=desp+">>> 这是三级引用\n";

        String desp = this.getMarkdownDesp(sendBean);
        //---------------------------


//        desp=desp+
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("text", text);
        params.add("desp", desp);
//        Object result = httpRequestUtils.request(url,HttpMethod.GET, params);
        String result = httpRequestUtils.get(url, params);
//        log.info("微信提醒[交叉]发送结果:"+result);

        log.info("交叉指标微信发送提醒:"+text);
    }

    private String getMarkdownDesp (WxSendBean sendBean) {
        double volume = Double.valueOf(sendBean.getPrice())*Double.valueOf(sendBean.getVolume());
        StringBuffer desp = new StringBuffer();
        desp.append(" <table> ").append("\n");
        desp.append("  <tr> ").append("\n");
        desp.append("    <th>"+sendBean.getExchange()+"   </th> ").append("\n");
        desp.append("    <th> "+sendBean.getSymbol()+"["+sendBean.getPrice()+"][量:"+ParseUtils.decimalFormat(volume,ParseUtils.TwoPattern)+"] </th> ").append("\n");
        desp.append("    <th>   "+sendBean.getType()+"  "+sendBean.getPeriod()+" ["+ParseUtils.parseCrossStatus(sendBean.getStatus())+"] *"+sendBean.getTime()+"* </th> ").append("\n");
        desp.append("  </tr> ").append("\n");

        desp.append("  <tr> ").append("\n");
        desp.append("   <td>   "+"支撑阻力位:----------"+"  </td> ").append("\n");
        for(PositionBean position :sendBean.getFibonacciList()) {
            desp.append("   <td>    "+position.getPostion()+" ["+position.getPrice()+"] </td> ").append("\n");
        }
        desp.append("  </tr> ").append("\n");

        for (WxSendQuotaBean quotaBean :sendBean.getQuotaList()) {
            desp.append("  <tr> ").append("\n");
            desp.append("   <td>   "+quotaBean.getType()+"  </td> ").append("\n");
            for(WxSendQuotaPeriodBean periodBean :quotaBean.getPeriodList()) {
                desp.append("   <td>     "+periodBean.getPeriod()+"  ["+ParseUtils.parseCrossStatus(periodBean.getStatus())+"]    </td> ").append("\n");
            }
            desp.append("  </tr> ").append("\n");
        }
        desp.append(" </table> ").append("\n");
        return  desp.toString();
    }

    private String getMarkdownDesp () {
        StringBuffer desp = new StringBuffer();
        desp.append("<table>").append("\n");
        desp.append("  <tr>").append("\n");
        desp.append("    <th >binance   </th>").append("\n");
        desp.append("    <th >    BNBBTC  [MACD]</th>").append("\n");
        desp.append("    <th >    1H  [金叉]    *2018/01/26 22:10:22*</th>").append("\n");
        desp.append("  </tr>").append("\n");
        desp.append("  <tr>").append("\n");
        desp.append("   <td>  MACD  </td>").append("\n");
        desp.append("   <td>     1H  [金叉]    *2018/01/26 22:10:22*</td>").append("\n");
        desp.append("   <td>     4H  [金叉]    *2018/01/26 22:10:22*</td>").append("\n");
        desp.append("  </tr>").append("\n");
        desp.append("  <tr>").append("\n");
        desp.append("    <td>  KDJ  </td>").append("\n");
        desp.append("    <td>    1H  [金叉]    *2018/01/26 22:10:22*</td>").append("\n");
        desp.append("    <td>    4H  [金叉]    *2018/01/26 22:10:22*</td>").append("\n");
        desp.append("  </tr>").append("\n");
        desp.append("  <tr>").append("\n");
        desp.append("    <td>  RSI  </td>").append("\n");
        desp.append("    <td>    1H  [金叉]    *2018/01/26 22:10:22*</td>").append("\n");
        desp.append("    <td>    4H  [金叉]    *2018/01/26 22:10:22*</td>").append("\n");
        desp.append("  </tr>").append("\n");
        desp.append("</table>").append("\n");
        return  desp.toString();
    }
}

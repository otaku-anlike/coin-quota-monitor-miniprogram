package wang.raye.springboot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import wang.raye.springboot.bean.WxSendBean;

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
        String desp="新币["+sendBean.getSymbol()+"]目前价格:"+sendBean.getPrice()+ "\\n";
        desp=desp+DateUtils.getToday();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("text", text);
        params.add("desp", desp);
//        Object result = httpRequestUtils.request(url,HttpMethod.GET, params);
        String result = httpRequestUtils.get(url, params);
//        log.info("微信提醒[新币]发送结果:"+result);
        log.info("上新币微信发送提醒:"+desp);
    }

    /**
     * 指标交叉发送提示
     * @param sendBean 微信发送bean
     */
    public void sendCross(WxSendBean sendBean){
        String url = "https://sc.ftqq.com/"+sendBean.getSckey()+".send";
        String status = "金叉";
        if ("4".equals(sendBean.getStatus())) {
            status = "死叉";
        }
        String text= sendBean.getExchange()+"["+sendBean.getSymbol()+"]" +" ["+status+"]";// + "📈";
        String desp=" ["+sendBean.getSymbol()+"]  ->  "+sendBean.getType()+"  ->  "+sendBean.getPeriod()+"  ->  "+"["+status+"]";
        desp=desp+"<br />                  ==========                   <br /> ";
        desp=desp+"> 1. ["+sendBean.getSymbol()+"]目前价格:["+sendBean.getPrice() + "]";
        desp=desp+"当前时间:["+DateUtils.getToday() + "]";
//        desp=desp+
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("text", text);
        params.add("desp", desp);
//        Object result = httpRequestUtils.request(url,HttpMethod.GET, params);
        String result = httpRequestUtils.get(url, params);
//        log.info("微信提醒[交叉]发送结果:"+result);

        log.info("交叉指标微信发送提醒:"+desp);
    }
}

package wang.raye.springboot.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class WxSendBean {

    /** Server酱的SCKEY*/
    private String sckey;
    /** 交易所*/
    private String exchange;
    /** 价格*/
    private String price;
    /** 币种*/
    private String symbol;
    /** 交叉类型*/
    private String type;
    /** 周期*/
    private String period;
    /** 交叉状态*/
    private String status;
    /** 交叉时间*/
    private String time;
    /** 各种指标*/
    private List<WxSendQuotaBean> quotaList;


}
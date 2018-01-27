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
public class WxSendQuotaBean {

    /** 交叉类型*/
    private String type;
    /** 周期*/
    private List<WxSendQuotaPeriodBean> periodList;
    /** 交叉时间*/
    private String time;


}
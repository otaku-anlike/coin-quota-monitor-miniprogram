package wang.raye.springboot.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class WxSendQuotaPeriodBean {

    /** 周期*/
    private String period;
    /** 交叉状态*/
    private String status;
    /** 交叉时间*/
    private String time;

}
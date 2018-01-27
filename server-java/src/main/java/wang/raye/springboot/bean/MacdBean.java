package wang.raye.springboot.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class MacdBean {

    /** 差离率（DIF） */
    private double dif;
    /** 九日DIF平滑移动平均值 */
    private double dea;
    /** 柱状线（BAR） */
    private double bar;


}
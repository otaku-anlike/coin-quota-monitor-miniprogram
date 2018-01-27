package wang.raye.springboot.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class KdjBean {

    /** K线是快速确认线——数值在90以上为超买，数值在10以下为超卖 */
    private double k;
    /** D线是慢速主干线——数值在80以上为超买，数值在20以下为超卖 */
    private double d;
    /** J线为方向敏感线 */
    private double j;


}
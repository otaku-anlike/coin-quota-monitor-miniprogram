package wang.raye.springboot.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class DojiBean {

    /** 十字星:=ABS(C-O)/C<=0.002 AND C/REF(C,1)<1.02; */
    private double doji;
    /** 缩量:=V<=HHV(V,10)*0.5; */
    private double hhv;
    /** J线为方向敏感线 */
    private double j;


}
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
public class PositionBean {

    /** 支撑阻力位*/
    private String postion;
    /** 价格*/
    private String price;


}
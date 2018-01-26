package wang.raye.springboot.bean;

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

    public String getSckey() {
        return sckey;
    }

    public void setSckey(String sckey) {
        this.sckey = sckey;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
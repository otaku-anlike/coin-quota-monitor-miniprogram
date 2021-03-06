package wang.raye.springboot.model;

import java.util.Date;

public class MacdCrossHistory {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column macd_cross_history.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column macd_cross_history.exchange
     *
     * @mbg.generated
     */
    private String exchange;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column macd_cross_history.symbol
     *
     * @mbg.generated
     */
    private String symbol;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column macd_cross_history.type
     *
     * @mbg.generated
     */
    private String type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column macd_cross_history.status
     *
     * @mbg.generated
     */
    private String status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column macd_cross_history.quota1
     *
     * @mbg.generated
     */
    private Double quota1;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column macd_cross_history.quota2
     *
     * @mbg.generated
     */
    private Double quota2;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column macd_cross_history.quota3
     *
     * @mbg.generated
     */
    private Double quota3;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column macd_cross_history.price
     *
     * @mbg.generated
     */
    private Double price;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column macd_cross_history.period
     *
     * @mbg.generated
     */
    private String period;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column macd_cross_history.time
     *
     * @mbg.generated
     */
    private Date time;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column macd_cross_history.id
     *
     * @return the value of macd_cross_history.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column macd_cross_history.id
     *
     * @param id the value for macd_cross_history.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column macd_cross_history.exchange
     *
     * @return the value of macd_cross_history.exchange
     *
     * @mbg.generated
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column macd_cross_history.exchange
     *
     * @param exchange the value for macd_cross_history.exchange
     *
     * @mbg.generated
     */
    public void setExchange(String exchange) {
        this.exchange = exchange == null ? null : exchange.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column macd_cross_history.symbol
     *
     * @return the value of macd_cross_history.symbol
     *
     * @mbg.generated
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column macd_cross_history.symbol
     *
     * @param symbol the value for macd_cross_history.symbol
     *
     * @mbg.generated
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column macd_cross_history.type
     *
     * @return the value of macd_cross_history.type
     *
     * @mbg.generated
     */
    public String getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column macd_cross_history.type
     *
     * @param type the value for macd_cross_history.type
     *
     * @mbg.generated
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column macd_cross_history.status
     *
     * @return the value of macd_cross_history.status
     *
     * @mbg.generated
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column macd_cross_history.status
     *
     * @param status the value for macd_cross_history.status
     *
     * @mbg.generated
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column macd_cross_history.quota1
     *
     * @return the value of macd_cross_history.quota1
     *
     * @mbg.generated
     */
    public Double getQuota1() {
        return quota1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column macd_cross_history.quota1
     *
     * @param quota1 the value for macd_cross_history.quota1
     *
     * @mbg.generated
     */
    public void setQuota1(Double quota1) {
        this.quota1 = quota1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column macd_cross_history.quota2
     *
     * @return the value of macd_cross_history.quota2
     *
     * @mbg.generated
     */
    public Double getQuota2() {
        return quota2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column macd_cross_history.quota2
     *
     * @param quota2 the value for macd_cross_history.quota2
     *
     * @mbg.generated
     */
    public void setQuota2(Double quota2) {
        this.quota2 = quota2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column macd_cross_history.quota3
     *
     * @return the value of macd_cross_history.quota3
     *
     * @mbg.generated
     */
    public Double getQuota3() {
        return quota3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column macd_cross_history.quota3
     *
     * @param quota3 the value for macd_cross_history.quota3
     *
     * @mbg.generated
     */
    public void setQuota3(Double quota3) {
        this.quota3 = quota3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column macd_cross_history.price
     *
     * @return the value of macd_cross_history.price
     *
     * @mbg.generated
     */
    public Double getPrice() {
        return price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column macd_cross_history.price
     *
     * @param price the value for macd_cross_history.price
     *
     * @mbg.generated
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column macd_cross_history.period
     *
     * @return the value of macd_cross_history.period
     *
     * @mbg.generated
     */
    public String getPeriod() {
        return period;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column macd_cross_history.period
     *
     * @param period the value for macd_cross_history.period
     *
     * @mbg.generated
     */
    public void setPeriod(String period) {
        this.period = period == null ? null : period.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column macd_cross_history.time
     *
     * @return the value of macd_cross_history.time
     *
     * @mbg.generated
     */
    public Date getTime() {
        return time;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column macd_cross_history.time
     *
     * @param time the value for macd_cross_history.time
     *
     * @mbg.generated
     */
    public void setTime(Date time) {
        this.time = time;
    }
}
/**
 * model业务实现类
 */
/**
 * @author Raye
 * @since 2016年9月21日20:58:46
 */
package wang.raye.springboot.server.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import wang.raye.springboot.model.*;
import wang.raye.springboot.model.mapper.AlertMapper;
import wang.raye.springboot.model.mapper.MacdCrossHistoryMapper;
import wang.raye.springboot.model.mapper.MacdCrossMapper;
import wang.raye.springboot.model.mapper.SymbolsMapper;
import wang.raye.springboot.server.CrossApiServer;
import wang.raye.springboot.utils.ParseUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Repository
public class CrossApiServerImpl implements CrossApiServer {

    @Value("${self.exchange.binance}")
    private String BINANCE;
    @Value("${self.exchange.bittrex}")
    private String BITTREX;

    @Autowired
    private MacdCrossMapper macdCrossMapper;
    @Autowired
    private SymbolsMapper symbolsMapper;
    @Autowired
    private AlertMapper alertMapper;

    @Override
    public List<MacdCross> findMacdCrossList(String exchange, String type, String period, String status) {
        List<MacdCross> list = this.getMacdCrossList(exchange, type, period, status);
        List<Symbols> symbolList = this.getSymbolList(exchange, null);
        for (MacdCross cross: list) {
            for (Symbols symbol:symbolList) {
                if (cross.getSymbol().equals(symbol.getSymbol())) {
                    cross.setLastPrice(symbol.getPrice());
                    break;
                }
            }
        }
        return list;
    }

    @Override
    public MacdCross findMacdCrossById(int id) {
        return this.getMacdCrossById(id);
    }

    @Override
    public MacdCross findMacdCrossBySymbolLike(String exchange, String type, String period, String status, String symbol) {

        MacdCross result = this.getMacdCrossBySymbolLike(exchange, type, period, status, symbol);

//        String symbolName = null;
//        if (exchange.equals(BINANCE)) {
//            symbolName = "BTCUSDT";
//        } else if (exchange.equals(BITTREX)) {
//            symbolName = "USDT-BTC";
//        }
        List<Symbols> symbolList = this.getSymbolList(exchange, symbol);

        if (null != symbolList && symbolList.size() > 0) {
            if (null == result) {
                result = new MacdCross();
                result.setExchange(exchange);
                result.setType(type);
                result.setPeriod(period);
                result.setSymbol(symbol);
                result.setPrice(symbolList.get(0).getPrice());
                result.setLastTime(symbolList.get(0).getTime());
            }
            result.setLastPrice(symbolList.get(0).getPrice());
            result.setLastTime(symbolList.get(0).getTime());
        }


        return result;
    }

    /**
     * @param exchange
     * @param type
     * @param period
     * @param status
     * @param limitHour
     * @return
     */
    @Override
    public List<String> findAlertList(String exchange, String type, String period, String status, Integer limitHour) {
        List<Alert> list = this.getAlertList(exchange, type, period, status, limitHour);
        List<String> resultList = new ArrayList<>();
        for (Alert alert: list) {
            String text= exchange+"["+alert.getSymbol()+"]" +" ["+ ParseUtils.parseCrossType(alert.getType())+"]";// + "📈";
            text = text+ "["+alert.getPeriod()+"]" +" ["+ParseUtils.parseCrossStatus(alert.getStatus())+"]";
            resultList.add(text);
        }
        return resultList;
    }


    private List<Alert> getAlertList(String exchange, String type, String period, String status, Integer limitHour) {
        AlertCriteria cond = new AlertCriteria();
        AlertCriteria.Criteria criteriaAlert = cond.createCriteria();
        if (!StringUtils.isEmpty(exchange)) {
            criteriaAlert.andExchangeEqualTo(exchange);
        }
        if (!StringUtils.isEmpty(type)) {
            criteriaAlert.andTypeEqualTo(type);
        }
        if (!StringUtils.isEmpty(period)) {
            criteriaAlert.andPeriodEqualTo(period);
        }
        if (!StringUtils.isEmpty(status)) {
            criteriaAlert.andStatusEqualTo(status);
        }
        if (null != limitHour) {
            Calendar calendar = Calendar.getInstance();
            // HOUR_OF_DAY 指示一天中的小时
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - limitHour);
            criteriaAlert.andTimeGreaterThan(calendar.getTime());
        }
        criteriaAlert.andSendEqualTo("1");

        List<Alert> list = alertMapper.selectByExample(cond);

        return list;
    }

    /**
     *
     * @param exchange
     * @param type
     * @param period
     * @param type
     * @return
     */
    private List<MacdCross> getMacdCrossList(String exchange, String type, String period, String status) {
        MacdCrossCriteria condMacdCross = new MacdCrossCriteria();
        MacdCrossCriteria.Criteria criteriaMacdCross = condMacdCross.createCriteria();
        if (!StringUtils.isEmpty(exchange)) {
            criteriaMacdCross.andExchangeEqualTo(exchange);
        }
        if (!StringUtils.isEmpty(type)) {
            criteriaMacdCross.andTypeEqualTo(type);
        }
        if (!StringUtils.isEmpty(period)) {
            criteriaMacdCross.andPeriodEqualTo(period);
        }
        if (!StringUtils.isEmpty(status)) {
            criteriaMacdCross.andStatusEqualTo(status);
        }
        if (exchange.equals(BINANCE)) {
            criteriaMacdCross.andSymbolLike("%BTC");
        } else if (exchange.equals(BITTREX)) {
            criteriaMacdCross.andSymbolLike("BTC%");
        }

        condMacdCross.setOrderByClause("symbol desc");

        List<MacdCross> list = macdCrossMapper.selectByExample(condMacdCross);

//        MacdCross btcMacdCross = this.getMacdCrossByBTC(exchange, type, period);
//        if (null != btcMacdCross) {
//            list.add(btcMacdCross);
//        }
        Collections.reverse(list);
        return list;
    }

    /**
     *
     * @param exchange
     * @return
     */
    private List<Symbols> getSymbolList(String exchange, String symbol) {
        SymbolsCriteria condMacdCross = new SymbolsCriteria();
        SymbolsCriteria.Criteria criteriaMacdCross = condMacdCross.createCriteria();
        if (!StringUtils.isEmpty(exchange)) {
            criteriaMacdCross.andExchangeEqualTo(exchange);
        }
        if (!StringUtils.isEmpty(symbol)) {
            criteriaMacdCross.andSymbolEqualTo(symbol);
        }
        List<Symbols> list = symbolsMapper.selectByExample(condMacdCross);
        return list;
    }

    /**
     *
     * @return
     */
    private MacdCross getMacdCrossBySymbolLike(String exchange, String type, String period, String status, String symbol) {

        MacdCross result = null;

        MacdCrossCriteria condMacdCross = new MacdCrossCriteria();
        MacdCrossCriteria.Criteria criteriaMacdCross = condMacdCross.createCriteria();
        if (!StringUtils.isEmpty(symbol)) {
//            String[] symbols = symbol.split("-");
//            criteriaMacdCross.andSymbolLike(symbols[0]);
//            criteriaMacdCross.andSymbolLike(symbols[1]);
            criteriaMacdCross.andSymbolEqualTo(symbol);
        }
        if (!StringUtils.isEmpty(exchange)) {
            criteriaMacdCross.andExchangeEqualTo(exchange);
        }
        if (!StringUtils.isEmpty(type)) {
            criteriaMacdCross.andTypeEqualTo(type);
        }
        if (!StringUtils.isEmpty(period)) {
            criteriaMacdCross.andPeriodEqualTo(period);
        }
        if (!StringUtils.isEmpty(status)) {
            criteriaMacdCross.andPeriodEqualTo(status);
        }
        List<MacdCross> list = macdCrossMapper.selectByExample(condMacdCross);
        if (null != list && list.size() > 0) {
            result =  list.get(0);
        }
        return result;
    }

    /**
     *
     * @param id
     * @return
     */
    private MacdCross getMacdCrossById(int id) {
        return macdCrossMapper.selectByPrimaryKey(id);
    }
}
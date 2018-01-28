/**
 * model业务实现类
 */
/**
 * @author Raye
 * @since 2016年9月21日20:58:46
 */
package wang.raye.springboot.server.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import wang.raye.springboot.model.MacdCross;
import wang.raye.springboot.model.MacdCrossCriteria;
import wang.raye.springboot.model.mapper.MacdCrossMapper;
import wang.raye.springboot.server.MacdCrossApiServer;

import java.util.Collections;
import java.util.List;

@Repository
public class MacdCrossApiServerImpl implements MacdCrossApiServer{

    @Autowired
    private MacdCrossMapper macdCrossMapper;

    @Override
    public List<MacdCross> findMacdCrossList(String exchange, String type, String period, String status) {
        return this.getMacdCrossList(exchange, type, period, status);
    }

    @Override
    public MacdCross findMacdCrossById(int id) {
        return this.getMacdCrossById(id);
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
        criteriaMacdCross.andSymbolLike("%BTC");
        condMacdCross.setOrderByClause("symbol desc");

        List<MacdCross> list = macdCrossMapper.selectByExample(condMacdCross);

        MacdCross btcMacdCross = this.getMacdCrossByBTC(exchange, type, period);
        if (null != btcMacdCross) {
            list.add(btcMacdCross);
        }
        Collections.reverse(list);
        return list;
    }

    /**
     *
     * @return
     */
    private MacdCross getMacdCrossByBTC(String exchange, String type, String period) {

        MacdCross result = null;

        MacdCrossCriteria condMacdCross = new MacdCrossCriteria();
        MacdCrossCriteria.Criteria criteriaMacdCross = condMacdCross.createCriteria()
                .andSymbolEqualTo("BTCUSDT");
        if (!StringUtils.isEmpty(exchange)) {
            criteriaMacdCross.andExchangeEqualTo(exchange);
        }
        if (!StringUtils.isEmpty(type)) {
            criteriaMacdCross.andTypeEqualTo(type);
        }
        if (!StringUtils.isEmpty(period)) {
            criteriaMacdCross.andPeriodEqualTo(period);
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
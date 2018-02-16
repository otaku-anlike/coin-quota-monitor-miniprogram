package wang.raye.springboot.controller;

import com.binance.api.client.domain.market.CandlestickInterval;
import de.elbatya.cryptocoins.bittrexclient.api.model.publicapi.BittrexInterval;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.raye.springboot.server.CurrentPriceServer;
import wang.raye.springboot.server.NetChangeServer;


@Api(value="定时任务相关的接口")
@RestController
@RequestMapping("/schedule/netChange")
public class ScheduleNetChangeController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NetChangeServer netChangeServer;

    @Value("${self.exchange.binance}")
    private String BINANCE;
    @Value("${self.exchange.bittrex}")
    private String BITTREX;

    @Scheduled(cron="${spring.schedule.binance.change.30m}")
    public void getBinancePrice() {
        log.info("线程 Binance 涨跌幅监控 30m:"+Thread.currentThread().getName()+"运行开始.....");
        netChangeServer.binanceNetChange(BINANCE, CandlestickInterval.HALF_HOURLY);
        log.info("线程 Binance 涨跌幅监控 30m:"+Thread.currentThread().getName()+"运行结束.....");
    }

    @Scheduled(cron="${spring.schedule.bittrex.change.1h}")
    public void getBittrexPrice() {
        log.info("线程 Bittrex 涨跌幅监控 1h:"+Thread.currentThread().getName()+"运行开始.....");
        netChangeServer.bittrexNetChange(BITTREX, BittrexInterval.HOURLY);
        log.info("线程 Bittrex 涨跌幅监控 1h:"+Thread.currentThread().getName()+"运行结束.....");
    }


}

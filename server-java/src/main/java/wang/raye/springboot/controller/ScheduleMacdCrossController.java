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
import wang.raye.springboot.server.MacdCrossServer;


@Api(value="定时任务相关的接口")
@RestController
@RequestMapping("/schedule/currentPrice")
public class ScheduleMacdCrossController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MacdCrossServer macdCrossServer;

    @Value("${self.exchange.binance}")
    private String BINANCE;
    @Value("${self.exchange.bittrex}")
    private String BITTREX;

    @Scheduled(cron="${spring.schedule.binance.macd.1h}")
    public void binanceMacd1h() {
        log.info("线程 binance macd 1h:"+Thread.currentThread().getName()+"运行开始.....");
        macdCrossServer.binanceMacdCross(BINANCE, CandlestickInterval.HOURLY);
        log.info("线程 binance macd 1h:"+Thread.currentThread().getName()+"运行结束.....");
    }

    @Scheduled(cron="${spring.schedule.binance.macd.4h}")
    public void binanceMacd4h() {
        log.info("线程 binance macd 4h:"+Thread.currentThread().getName()+"运行开始.....");
        macdCrossServer.binanceMacdCross(BINANCE, CandlestickInterval.FOUR_HORLY);
        log.info("线程 binance macd 4h:"+Thread.currentThread().getName()+"运行结束.....");
    }

    @Scheduled(cron="${spring.schedule.binance.macd.1d}")
    public void binanceMacd1d() {
        log.info("线程 Bittrex macd 1d:"+Thread.currentThread().getName()+"运行开始.....");
        macdCrossServer.binanceMacdCross(BINANCE, CandlestickInterval.DAILY);
        log.info("线程 Bittrex macd 1d:"+Thread.currentThread().getName()+"运行结束.....");
    }

    @Scheduled(cron="${spring.schedule.bittrex.macd.1h}")
    public void bittrexMacd1h() {
        log.info("线程 Bittrex macd 1h:"+Thread.currentThread().getName()+"运行开始.....");
        macdCrossServer.bittrexMacdCross(BITTREX, BittrexInterval.HOURLY);
        log.info("线程 Bittrex macd 1h:"+Thread.currentThread().getName()+"运行结束.....");
    }

//    @Scheduled(cron="${spring.schedule.bittrex.macd.4h}")
//    public void bittrexMacd4h() {
//        log.info("线程 Bittrex macd 4h:"+Thread.currentThread().getName()+"运行开始.....");
//        macdCrossServer.bittrexMacdCross(BITTREX, CandlestickInterval.FOUR_HORLY);
//        log.info("线程 Bittrex macd 4h:"+Thread.currentThread().getName()+"运行结束.....");
//    }

    @Scheduled(cron="${spring.schedule.bittrex.macd.1d}")
    public void bittrexMacd1d() {
        log.info("线程 Bittrex macd 1d:"+Thread.currentThread().getName()+"运行开始.....");
        macdCrossServer.bittrexMacdCross(BITTREX, BittrexInterval.DAILY);
        log.info("线程 Bittrex macd 1d:"+Thread.currentThread().getName()+"运行结束.....");
    }


}

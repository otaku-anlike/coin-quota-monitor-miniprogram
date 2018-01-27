package wang.raye.springboot.controller;

import com.binance.api.client.domain.market.CandlestickInterval;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Scheduled(cron="${spring.schedule.binance.macd.1h}")
    public void binanceMacd1h() {
        log.info("线程 binance macd 1h:"+Thread.currentThread().getName()+"运行开始.....");
        macdCrossServer.binanceMacdCross(CandlestickInterval.HOURLY);
        log.info("线程 binance macd 1h:"+Thread.currentThread().getName()+"运行结束.....");
    }

    @Scheduled(cron="${spring.schedule.binance.macd.4h}")
    public void binanceMacd4h() {
        log.info("线程 binance macd 4h:"+Thread.currentThread().getName()+"运行开始.....");
        macdCrossServer.binanceMacdCross(CandlestickInterval.FOUR_HORLY);
        log.info("线程 binance macd 4h:"+Thread.currentThread().getName()+"运行结束.....");
    }

    @Scheduled(cron="${spring.schedule.binance.macd.1d}")
    public void binanceMacd1d() {
        log.info("线程 binance macd 1d:"+Thread.currentThread().getName()+"运行开始.....");
        macdCrossServer.binanceMacdCross(CandlestickInterval.DAILY);
        log.info("线程 binance macd 1d:"+Thread.currentThread().getName()+"运行结束.....");
    }


}

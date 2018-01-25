package wang.raye.springboot.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.raye.springboot.server.CurrentPriceServer;
import wang.raye.springboot.server.UserServer;

import java.util.Date;
import java.util.Random;


@Api(value="定时任务相关的接口")
@RestController
@RequestMapping("/schedule/currentPrice")
public class ScheduleCurrentPriceController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CurrentPriceServer currentPriceServer;

    @Scheduled(cron="${spring.schedule.binance.price}")
    public void getCurrentPrice() {
        log.info("线程 binance 实时价格:"+Thread.currentThread().getName()+"运行开始.....");
        currentPriceServer.binancePrice();
        log.info("线程 binance 实时价格:"+Thread.currentThread().getName()+"运行结束.....");
    }


}

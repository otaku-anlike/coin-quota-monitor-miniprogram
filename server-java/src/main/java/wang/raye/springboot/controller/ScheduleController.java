package wang.raye.springboot.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;


@Api(value="定时任务相关的接口")
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

//    @Scheduled(cron="${spring.schedule.test}")
    public void getReport() {
        Date date = new Date();
        System.out.println(date);
        log.info("线程getReport:"+Thread.currentThread().getName()+"运行中.....");
    }

//    @Scheduled(cron="${spring.schedule.test2}")
    public void getReport2() {
        Date date = new Date();
        System.out.println(new Random());
        log.info("线程getReport2:"+Thread.currentThread().getName()+"运行中.....");
    }

}

package com.mkyong.controller;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = "30 5 * * MON,TUE,WED,THU,FRI")
    public void reportCurrentTime() {
    	System.out.println("The time is now {}"+ dateFormat.format(new Date()));
        log.info("The time is now {}", dateFormat.format(new Date()));
    }
}
package com.icashflow.controller;


import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.icashflow.service.IcashService;

@Component
public class ScheduledTasks {
	
	final static Logger logger = Logger.getLogger(ScheduledTasks.class);

	
	private IcashService icashService;

    public IcashService getIcashService() {
		return icashService;
	}

    @Autowired
    @Qualifier("iCashServiceImpl")
	public void setIcashService(IcashService icashService) {
		this.icashService = icashService;
	}

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    //This annotation schedules a corn job to run 17.30 MON - FRIDAY
    // @Scheduled(cron = "0 30 17 ? * MON-FRI")
    @Scheduled(cron = "0 30 17 ? * MON-FRI")
    public void generateAwardsFile() {
    	try {
			icashService.generateAwardsFile();
			logger.info("Awards file creation is success");
		} catch (ClassNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
			logger.info("Awards file creation is Failed");
		}
    }
}
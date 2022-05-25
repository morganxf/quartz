package com.example.quartz.service.impl.job;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class DemoJob implements Job {
    private static final Logger L = LoggerFactory.getLogger(DemoJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String schedulerName = getSchedulerName(context);
        JobDetail jobDetail = context.getJobDetail();
        Trigger trigger = context.getTrigger();
        String instanceId = context.getFireInstanceId();
        Date schedulerFireTime = context.getScheduledFireTime();
        Date fireTime = context.getFireTime();
        long runTime = context.getJobRunTime();
        JobDataMap dataContext = context.getMergedJobDataMap();
        L.info("scheduler={}, job={}, trigger={}, instance={}, schedulerFireTime={}, fireTime={}, runTime={}ms, dataContext={}",
                schedulerName, jobDetail.getKey(), trigger.getKey(), instanceId, schedulerFireTime, fireTime, runTime, dataContext);
    }

    private String getSchedulerName(JobExecutionContext context) {
        try {
            return context.getScheduler().getSchedulerName();
        } catch (SchedulerException e) {
            L.warn("failed to getSchedulerName", e);
            return null;
        }
    }
}

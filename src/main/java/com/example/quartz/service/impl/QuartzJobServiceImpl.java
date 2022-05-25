package com.example.quartz.service.impl;

import com.example.quartz.model.dto.Task;
import com.example.quartz.service.impl.job.DemoJob;
import com.example.quartz.service.intf.JobService;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.StringMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service("jobService")
public class QuartzJobServiceImpl implements JobService {

    private final Scheduler scheduler;

    private final Scheduler pqlScheduler;

    private Map<String, Scheduler> schedulerMap;

    public QuartzJobServiceImpl(@Autowired @Qualifier("schedulerFactoryBean") SchedulerFactoryBean schedulerFactoryBean,
                                @Autowired @Qualifier("schedulerFactoryBean2") SchedulerFactoryBean schedulerFactoryBean2) {
        scheduler = schedulerFactoryBean.getScheduler();
        pqlScheduler = schedulerFactoryBean2.getScheduler();
    }

    private Scheduler getSchedulerByName(String name) throws SchedulerException {
        if (schedulerMap == null) {
            schedulerMap = new HashMap<>();
            schedulerMap.put(scheduler.getSchedulerName(), scheduler);
            schedulerMap.put(pqlScheduler.getSchedulerName(), pqlScheduler);
        }
        return schedulerMap.get(name);
    }

    @Override
    public List<String> getSchedulers() throws SchedulerException {
        List<String> schedulerNames = new ArrayList<>();
        schedulerNames.add(scheduler.getSchedulerName());
        schedulerNames.add(pqlScheduler.getSchedulerName());
        return schedulerNames;
    }

    @Override
    public List<String> getJobGroups(String schedulerName) throws SchedulerException {
        Scheduler scheduler = getSchedulerByName(schedulerName);
        return scheduler.getJobGroupNames();
    }

    @Override
    public Set<JobKey> getJobs(String schedulerName, String groupName, StringMatcher.StringOperatorName operator) throws SchedulerException {
        Scheduler scheduler = getSchedulerByName(schedulerName);
        GroupMatcher<JobKey> matcher = getGroupMatcher(groupName, operator);
        return scheduler.getJobKeys(matcher);
    }

    private GroupMatcher<JobKey> getGroupMatcher(String group, StringMatcher.StringOperatorName operator) {
        if (group == null) {
            operator = StringMatcher.StringOperatorName.ANYTHING;
        } else if (operator == null) {
            operator = StringMatcher.StringOperatorName.EQUALS;
        }
        GroupMatcher<JobKey> matcher;
        switch (operator) {
            case CONTAINS:
                matcher = GroupMatcher.groupContains(group);
                break;
            case STARTS_WITH:
                matcher = GroupMatcher.groupStartsWith(group);
                break;
            case ENDS_WITH:
                matcher = GroupMatcher.groupEndsWith(group);
                break;
            case ANYTHING:
                matcher = GroupMatcher.anyJobGroup();
                break;
            default:
                matcher = GroupMatcher.groupEquals(group);
                break;
        }
        return matcher;
    }

    @Override
    public List<TriggerKey> getTriggers(String schedulerName, JobKey jobKey) throws SchedulerException {
        Scheduler scheduler = getSchedulerByName(schedulerName);
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
        if (CollectionUtils.isEmpty(triggers)) {
            return null;
        }
        return triggers.stream().map(Trigger::getKey).collect(Collectors.toList());
    }

    @Override
    public void scheduleJob(Task task) throws SchedulerException {
        JobKey jobKey = task.getJobKey();
        String description = task.getDescription();
        Class<? extends Job> jobClass = task.getJobClass();
        JobDataMap dataMap = task.getDataMap();
        JobDetail jobDetail = getJobDetail(jobKey, description, dataMap, jobClass);

        String cron = task.getCronExpression();
        Trigger trigger = getTrigger(jobKey, description, dataMap, cron);

        Scheduler scheduler = getSchedulerByName(task.getSchedulerName());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    @Override
    public void pauseJob(String schedulerName, JobKey jobKey) throws SchedulerException {
        Scheduler scheduler = getSchedulerByName(schedulerName);
        scheduler.pauseJob(jobKey);
    }

    @Override
    public void resumeJob(String schedulerName, JobKey jobKey) throws SchedulerException {
        Scheduler scheduler = getSchedulerByName(schedulerName);
        scheduler.resumeJob(jobKey);
    }

    @Override
    public void deleteJob(String schedulerName, JobKey jobKey) throws SchedulerException {
        Scheduler scheduler = getSchedulerByName(schedulerName);
        scheduler.deleteJob(jobKey);
    }

    @Override
    public List<Class<? extends Job>> getAvailableJobs() {
        List<Class<? extends Job>> jobs = new ArrayList<>();
        jobs.add(DemoJob.class);
        return jobs;
    }

    public JobDetail getJobDetail(JobKey jobKey, String description, JobDataMap jobDataMap, Class<? extends Job> jobClass) {
        return JobBuilder.newJob(jobClass)
                .withIdentity(jobKey)
                .withDescription(description)
                .setJobData(jobDataMap)
                .usingJobData(jobDataMap)
                .requestRecovery()
                .storeDurably()
                .build();
    }

    public Trigger getTrigger(JobKey jobKey, String description, JobDataMap jobDataMap, String cronExpression) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobKey.getName(), jobKey.getGroup())
                .withDescription(description)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .usingJobData(jobDataMap)
                .build();
    }
}

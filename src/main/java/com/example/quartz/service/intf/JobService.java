package com.example.quartz.service.intf;

import com.example.quartz.model.dto.Task;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.StringMatcher;

import java.util.List;
import java.util.Set;

public interface JobService {

    List<String> getSchedulers() throws SchedulerException;

    List<String> getJobGroups(String schedulerName) throws SchedulerException;

    Set<JobKey> getJobs(String schedulerName, String groupName, StringMatcher.StringOperatorName operator) throws SchedulerException;

    List<TriggerKey> getTriggers(String schedulerName, JobKey jobKey) throws SchedulerException;

    void scheduleJob(Task task) throws SchedulerException;

    void pauseJob(String schedulerName, JobKey jobKey) throws SchedulerException;

    void resumeJob(String schedulerName, JobKey jobKey) throws SchedulerException;

    void deleteJob(String schedulerName, JobKey jobKey) throws SchedulerException;

    List<Class<? extends Job>> getAvailableJobs();

}

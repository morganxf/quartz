package com.example.quartz.controller;

import com.example.quartz.model.dto.Task;
import com.example.quartz.service.intf.JobService;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.StringMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping("/jobs")
    public List<Class<? extends Job>> getAvailableJobs() {
        return jobService.getAvailableJobs();
    }

    @GetMapping("/schedulers")
    public List<String> getSchedulers() throws SchedulerException {
        return jobService.getSchedulers();
    }

    @GetMapping("/scheduler/groups")
    public List<String> getJobGroups(@RequestParam String schedulerName) throws SchedulerException {
        return jobService.getJobGroups(schedulerName);
    }

    @GetMapping("/scheduler/group/jobs")
    public Set<JobKey> getGroupJobs(@RequestParam String schedulerName, @RequestParam String groupName,
                                    @RequestParam StringMatcher.StringOperatorName operator) throws SchedulerException {
        return jobService.getJobs(schedulerName, groupName, operator);
    }

    @GetMapping("/scheduler/job/triggers")
    public List<TriggerKey> getJobTriggers(@RequestParam String schedulerName, @RequestParam String groupName,
                                           @RequestParam String jobName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);
        return jobService.getTriggers(schedulerName, jobKey);
    }

    @PostMapping("/scheduler/job/start")
    public void scheduleJob(@RequestBody Task task) throws SchedulerException {
        jobService.scheduleJob(task);
    }

    @PostMapping("/scheduler/job/pause")
    public void pauseJob(@RequestBody Task task) throws SchedulerException {
        jobService.pauseJob(task.getSchedulerName(), task.getJobKey());
    }

    @PostMapping("/scheduler/job/resume")
    public void resumeJob(@RequestBody Task task) throws SchedulerException {
        jobService.resumeJob(task.getSchedulerName(), task.getJobKey());
    }

    @DeleteMapping("/scheduler/job")
    public void deleteJob(@RequestBody Task task) throws SchedulerException {
        jobService.deleteJob(task.getSchedulerName(), task.getJobKey());
    }

}

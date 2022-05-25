package com.example.quartz.model.dto;

import com.example.quartz.utils.JobKeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobKey;

import javax.validation.constraints.NotNull;

@Data
public class Task {

    private String schedulerName;

    @NotNull
    @JsonDeserialize(using = JobKeyDeserializer.class)
    private JobKey jobKey;

    private String description;

    private Class<? extends Job> jobClass;

    private JobDataMap dataMap = new JobDataMap();

    private String cronExpression;

}

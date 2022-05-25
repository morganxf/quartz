package com.example.quartz.model.dto;

import com.example.quartz.service.impl.job.DemoJob;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskTest {
    private static final Logger L = LoggerFactory.getLogger(TaskTest.class);

    @Test
    public void testToJson() throws JsonProcessingException {
        Task task = new Task();
        task.setSchedulerName("Default");
        task.setJobKey(new JobKey("job_1", "group_1"));
        task.setDescription("test 1");
        task.setJobClass(DemoJob.class);
        ObjectMapper mapper = new ObjectMapper();
        String out = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(task);
        L.info("out: {}", out);
    }

    @Test
    public void testFromJson() throws JsonProcessingException, JsonMappingException {
        String in = "{\n" +
                "  \"schedulerName\" : \"Default\",\n" +
                "  \"jobKey\" : {\n" +
                "    \"name\" : \"job_1\",\n" +
                "    \"group\" : \"group_1\"\n" +
                "  },\n" +
                "  \"description\" : \"test 1\",\n" +
                "  \"jobClass\" : \"com.example.quartz.service.impl.job.DemoJob\"\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        Task out = mapper.readValue(in, Task.class);
        System.out.println(out);
    }
}

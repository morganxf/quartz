package com.example.quartz;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class LoggerTest {
    private static final Logger L = LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void test() {
        L.info("data={}", new Date());
    }

}

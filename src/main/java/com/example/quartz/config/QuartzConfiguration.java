package com.example.quartz.config;

import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@AutoConfigureAfter(DatasourceConfiguration.class)
public class QuartzConfiguration {

    private static final String LOCATION = "quartz.properties";
    private static final String LOCATION_PQL = "quartz.pql.properties";

    private static final int START_UP_DELAY_SECOND = 60;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

//    @Bean("quartzProperties")
    public Properties quartzProperties(String location) throws IOException {
        PropertiesFactoryBean factoryBean = new PropertiesFactoryBean();
        factoryBean.setLocation(new ClassPathResource(location));
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    @Bean
    public JobFactory jobFactory(ApplicationContext context) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(context);
        return jobFactory;
    }

    @Bean("schedulerFactoryBean")
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) throws IOException {
        DatasourceConfiguration.checkDataSource(dataSource);
        return _schedulerFactoryBean(jobFactory, dataSource, LOCATION);
    }

    @Bean("schedulerFactoryBean2")
    public SchedulerFactoryBean schedulerFactoryBean2(JobFactory jobFactory) throws IOException {
        return _schedulerFactoryBean(jobFactory, dataSource, LOCATION_PQL);
    }

    private SchedulerFactoryBean _schedulerFactoryBean(JobFactory jobFactory, DataSource dataSource, String location) throws IOException {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setOverwriteExistingJobs(true);
        factoryBean.setAutoStartup(true);
        factoryBean.setStartupDelay(START_UP_DELAY_SECOND);
        factoryBean.setDataSource(dataSource);
        factoryBean.setJobFactory(jobFactory);
        Properties properties = quartzProperties(location);
        factoryBean.setQuartzProperties(properties);
        factoryBean.setSchedulerName(properties.getProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME));
        return factoryBean;
    }

    public final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
        private AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(final ApplicationContext context) throws BeansException {
            beanFactory = context.getAutowireCapableBeanFactory();
        }

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            return job;
        }
    }

}

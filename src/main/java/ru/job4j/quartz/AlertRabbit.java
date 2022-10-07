package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.*;
import java.util.*;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    private int interval;

    public AlertRabbit(Properties properties) {
        this.interval = Integer.parseInt(properties.getProperty("rabbit.interval"));
    }

    public static Properties initProperties() throws Exception {
        Properties config = new Properties();
        try (InputStream in = AlertRabbit
                .class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            config.load(in);
        }
        return config;
    }

    public static void main(String[] args) throws Exception {
        AlertRabbit alertRabbit = new AlertRabbit(initProperties());
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(alertRabbit.interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }
}

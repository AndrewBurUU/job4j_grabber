package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.*;
import java.sql.*;
import java.util.*;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit implements AutoCloseable {

    private Connection connection;

    private Properties properties;

    public AlertRabbit(Properties properties) throws Exception {
        this.properties = properties;
        initConnection();
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

    private void initConnection() throws Exception {
        Class.forName(properties.getProperty("driver"));
        String url = properties.getProperty("url");
        String login = properties.getProperty("username");
        String password = properties.getProperty("password");
        connection = DriverManager.getConnection(url, login, password);
    }

    public void insert(Long createdDateTime) {
        try (PreparedStatement statement =
                     connection.prepareStatement("insert into rabbit(created_date) values (?)")) {
            statement.setTimestamp(1, new Timestamp(createdDateTime));
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) throws Exception {
        AlertRabbit alertRabbit = new AlertRabbit(initProperties());
        int interval = Integer.parseInt(
                alertRabbit.properties.getProperty("rabbit.interval"));
        try {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", store);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(String.format("store: %s", store));
            store.stream().forEach(aLong -> alertRabbit.insert(aLong));
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(String.format("hashCode: %s", hashCode()));
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            store.add(System.currentTimeMillis());
        }
    }
}

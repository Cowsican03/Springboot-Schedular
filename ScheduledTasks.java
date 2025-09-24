package com.example.demo.tasks;

import com.example.demo.model.JobRun;
import com.example.demo.repo.JobRunRepository;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {

    @Autowired
    JobRunRepository repo;

    private final JdbcTemplate jdbcTemplate;

    public ScheduledTasks(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    @Scheduled(fixedRate = 10_000)
    public void writeHeartbeat() {
        LocalDateTime start = LocalDateTime.now();
        JobRun run = new JobRun();
        run.setTaskName("heartbeat");
        run.setStartedAt(start);

        try {
            run.setStatus("SUCCESS");
            run.setNotes("Inserted by scheduler");
            run.setFinishedAt(LocalDateTime.now());
            run.setDurationMs(java.time.Duration.between(start, run.getFinishedAt()).toMillis());
        } catch (Exception ex) {
            run.setStatus("ERROR");
            run.setNotes(ex.getMessage());
            run.setFinishedAt(LocalDateTime.now());
            run.setDurationMs(java.time.Duration.between(start, run.getFinishedAt()).toMillis());
        }
        System.out.println("10 sec task from the writeHeartbeat method....");
        repo.save(run);
    }
    @Transactional
    @Scheduled(cron = "0 20 10 * * ?", zone = "Asia/Kolkata")
    public void dailySummary() {
        JobRun run = new JobRun();
        run.setTaskName("daily_summary");
        run.setStartedAt(LocalDateTime.now());
        run.setStatus("SUCCESS");
        run.setNotes("Daily summary placeholder");
        run.setFinishedAt(LocalDateTime.now());
        run.setDurationMs(0L);
        System.out.println("Daily task from the daily summary method...");
        repo.save(run);
    }

    @Scheduled(fixedRate = 20000)
    public void insertViaJdbcTemplate() {
        String sql = "INSERT INTO job_runs (task_name, started_at, finished_at, status, notes) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                "jdbc_task",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "SUCCESS",
                "Inserted using JdbcTemplate");
        System.out.println("Inserted row via JdbcTemplate at " + LocalDateTime.now());
    }

    @Scheduled(fixedRate = 30000)
    public void logSuccessRuns() {
        List<JobRun> successRuns = repo.findByStatus("SUCCESS");
        System.out.println("Total SUCCESS runs so far: " + successRuns.size());
    }
}

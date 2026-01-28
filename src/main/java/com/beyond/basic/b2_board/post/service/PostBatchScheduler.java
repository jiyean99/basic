package com.beyond.basic.b2_board.post.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PostBatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job job;

    @Autowired
    public PostBatchScheduler(JobLauncher jobLauncher, Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void batchScheduler() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) // 시스템 시간을 이용하여
                    .toJobParameters();
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

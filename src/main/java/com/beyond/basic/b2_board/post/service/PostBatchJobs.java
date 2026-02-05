//package com.beyond.basic.b2_board.post.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
///*
// * [@Configuration] : 빈 객체르 쓰겠다는 뜻
// * */
//@Slf4j
//@Configuration
//public class PostBatchJobs {
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager platformTransactionManager;
//
//    @Autowired
//    public PostBatchJobs(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
//        this.jobRepository = jobRepository;
//        this.platformTransactionManager = platformTransactionManager;
//    }
//
//    @Bean
//    public Job postJob() {
//        return new JobBuilder("postJob", jobRepository)
//                .start(firstStep())
//                .next(secondStep())
//                .build();
//    }
//
//    @Bean
//    public Step firstStep() {
//        return new StepBuilder("firstStep", jobRepository)
//                .tasklet((a, b) -> {
//                    log.info("=== batch task 1 start ===");
//                    log.info("=== batch task 1 end ===");
//                    return RepeatStatus.FINISHED;
//                }, platformTransactionManager)
//                .build();
//    }
//
//    @Bean
//    public Step secondStep() {
//        return new StepBuilder("secondStep", jobRepository)
//                .tasklet((a, b) -> {
//                    log.info("=== batch task 2 start ===");
//                    log.info("=== batch task 2 end ===");
//                    return RepeatStatus.FINISHED;
//                }, platformTransactionManager)
//                .build();
//    }
//}
//

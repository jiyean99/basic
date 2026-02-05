//package com.beyond.basic.b2_board.post.service;
//
//
//import com.beyond.basic.b2_board.post.domain.Post;
//import com.beyond.basic.b2_board.post.repository.PostRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Slf4j
//@Component
//@Transactional
//public class PostScheduler {
//    private final PostRepository postRepository;
//
//    public PostScheduler(PostRepository postRepository) {
//        this.postRepository = postRepository;
//    }
//
//    // (1) fixedDelay를 통해 간단히 주기적인 작업 수행
//    /*
//    @Scheduled(fixedDelay = 1000) // 1초마다 실행
//    public void simpleScheduler(){
//        log.info("==== 스케쥴러 시작 ====");
//        log.info("==== 스케쥴러 로직 수행 ====");
//        log.info("==== 스케쥴러 끝 ====");
//    }
//    */
//
//    // (2) cron을 통해 작업 수행 미세 조정 가능
//    // - cron의 각 자리는 "초 분 시 일 월 요일"의 의미를 갖고있다.
//    //   * * * * * * : 매월, 매일, 매시, 매분, 매초 마다의 의미
//    //   0 0 * * * * : 매월, 매일, 매시, 0분, 0초에 의미
//    //   0 0 11 * * * : 매월, 매일, 11시, 0분, 0초에 의미
//    //   0 0/1 * * * * : 매월, 매일, 매시, 1분 마다의 의미
//    @Scheduled(cron = "0 0/1 * * * *")
//    public void cronScheduler() {
//        log.info("==== 스케쥴러 시작 ====");
//        log.info("==== 스케쥴러 로직 수행 ====");
//        // Post 전체 데이터 중 예약여부가 YES 인 건을 조회 후, 그 중 현재시간보다 이전인 데이터는 NO로 변경
//
//        LocalDateTime now = LocalDateTime.now(); // 현재 시간
//
//        List<Post> postList = postRepository.findByAppointment("YES");
//        for (Post p : postList) {
//            if (p.getAppointmentTime().isBefore(now)) {
//                p.updateAppointment("NO");
//            }
//        }
//
//        log.info("==== 스케쥴러 끝 ====");
//    }
//
//}

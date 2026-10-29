package com.renish.backend.scheduler;

import com.renish.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentScheduledTask {

    private final StudentRepository studentRepository;

    // Sec, Min, Hrs, Day of Month, Month, Day of Week
    @Scheduled(cron = "0 0 10 * * *")
    public void printStudentCount(){
        Long studentCount = studentRepository.count();
        log.info("Scheduled task: Total students = {}",studentCount);
    }


//    @Scheduled(fixedDelay = 10000)
//    public void printStudentCount(){
//        Long studentCount = studentRepository.count();
//        log.info("Scheduled task: Total students = {}",studentCount);
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            throw new RuntimeException(e);
//        }
//    }

//    @Scheduled(fixedRate = 10000)
//    public void printStudentCount(){
//        Long studentCount = studentRepository.count();
//        log.info("Scheduled task: Total students = {}",studentCount);
//    }


    //    Cron expression	Meaning
//*/5 * * * * *	Every 5 seconds
//*/10 * * * * *	Every 10 seconds
//*/30 * * * * *	Every 30 seconds
//0 * * * * *	Every minute, at second 0
//            0 */5 * * * *	Every 5 minutes
//0 */10 * * * *	Every 10 minutes
//0 */30 * * * *	Every 30 minutes
//0 0 * * * *	Every hour
//0 0 */2 * * *	Every 2 hours
//0 0 0 * * *	Every day at midnight
//0 0 9 * * *	Every day at 9:00 AM
//0 30 18 * * *	Every day at 6:30 PM
//0 0 9 * * MON	Every Monday at 9:00 AM
//0 0 9 * * MON-FRI	Weekdays at 9:00 AM
//0 0 9 1 * *	First day of every month at 9:00 AM
//0 0 0 1 1 *	Every January 1 at midnight

}

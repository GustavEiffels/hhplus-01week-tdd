package io.hhplus.tdd.Integration.currency;

import io.hhplus.tdd.service.PointService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest
public class PointServiceConCurrencyTest {

    @Autowired
    private PointService pointService;

    private static final int THREAD_NUM = 100;

    void timeRecorder(long start, long end){
        double elapsedTime = (end - start) / 1000.0;
        String formattedTime = String.format("%.4f", elapsedTime);
        System.out.println("경과 시간 : " + formattedTime + " 초");
    }

    /**
     * 같은 사용자가
     * 동시에 10_000 포인트 충전
     */
    @DisplayName("같은 사용자가 한번에 스레드 수 만큼 충전할 때")
    @Test
    void sameUser_pointCharge() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);
        long start_time = System.currentTimeMillis();

        for(int i = 0; i<THREAD_NUM; i++){
            executor.submit(()->{
               pointService.pointCharge(1L,10_000L);
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long end_time = System.currentTimeMillis();
        timeRecorder(start_time,end_time);

        Assertions.assertEquals(10_000*THREAD_NUM,pointService.findUserPointByUserId(1L).point());
    }

    /**
     * 예외 발생하는 경우
     * 동시에 10_000 포인트 충전
     * 특정 숫자 횟수에 예외 발생
     */
    @DisplayName("한 사람이 여러번 요청 중 하나가 예외일 때")
    @Test
    void sameUser_pointCharge_ExceptionHandling_Once_Exception() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);

        long start_time = System.currentTimeMillis();
        AtomicInteger exceptionInteger = new AtomicInteger(0);

        for (int i = 0; i < THREAD_NUM; i++) {
            executor.submit(() -> {
                final long chargePoint = (exceptionInteger.getAndIncrement()  == 55) ? 2_000_000L : 10_000L;
                pointService.pointCharge(1L, chargePoint);
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);


        long end_time = System.currentTimeMillis();
        timeRecorder(start_time, end_time);

        long expectedPoint = 10_000 * (THREAD_NUM - 1);
        Assertions.assertEquals(expectedPoint, pointService.findUserPointByUserId(1L).point());
    }

    @DisplayName("쓰래드 수 만큼의 서로 다른 유저들이 각자 충전 할 때")
    /**
     * 전부 다른 유저 들로 할 경우
     * @throws InterruptedException
     */
    @Test
    void differentUser_pointCharge() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);

        long start_time = System.currentTimeMillis();
        AtomicLong exceptionLong = new AtomicLong(1);


        for(int i = 1; i<=THREAD_NUM; i++){
            executor.submit(()->{
                final long userid = exceptionLong.getAndIncrement();
                pointService.pointCharge(userid,10_000L);
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long end_time = System.currentTimeMillis();
        timeRecorder(start_time,end_time);

        long totalPoint = 0;

        for(int i = 1; i<=THREAD_NUM; i++){
            final long userId = i;
            long point = pointService.findUserPointByUserId(userId).point();
            totalPoint+= point;
//            System.out.println("사용자 "+userId+" 현재 포인트 : "+point);
        }

        Assertions.assertEquals(10_000*THREAD_NUM,totalPoint);
    }
}

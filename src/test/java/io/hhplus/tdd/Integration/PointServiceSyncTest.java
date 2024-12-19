package io.hhplus.tdd.Integration;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.unit.service_validation.ServiceValidation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class PointServiceSyncTest {


    PointServiceSync pointService;
    @BeforeEach
    void setUp(){
        UserPointTable userPointTable = new UserPointTable();
        PointHistoryTable pointHistoryTable = new PointHistoryTable();
        ServiceValidation serviceValidation = new ServiceValidation();
        pointService = new PointServiceSync(userPointTable,pointHistoryTable,serviceValidation);
    }

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
        System.out.println("실제 저장된 포인트 : "+pointService.findUserPointByUserId(1L).point());
    }

    @DisplayName("서로 다른 유저들이 각자 충전할때")
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


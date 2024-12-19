package io.hhplus.tdd.Integration;


import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.*;
import io.hhplus.tdd.service.PointService;
import io.hhplus.tdd.service.ServiceValidation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PointServiceTest {

    @Autowired
    PointService pointService;


// findUserPointByUserId
    /**
     * findUserPointByUserId
     * 유효하지 않은 userId 값 입력 시 예외
     */
    @Test
    void findUserPointByUserId_유효하지_않은_userId값(){
        UserPointException exceptionInstance =  Assertions.assertThrows(UserPointException.class,()->{
            pointService.findUserPointByUserId(-1L);
        });
        Assertions.assertEquals(POINT_STATUS.INVALID_USERID,exceptionInstance.status);
    }

    /**
     * findUserPointByUserId
     * 새로운 UserPoint 인스턴스 생성시 point 0
     */
    @Test
    void findUserPointByUserId_인스턴스생성시_point_0(){
        Assertions.assertEquals(pointService.findUserPointByUserId(100L).point(),0L);
    }

// pointCharge
    /**
     * pointCharge
     * 유효하지 않은 userId 값 입력 시 예외
     */
    @Test
    void pointCharge_NOT_INVALID_USERID(){
        UserPointException exceptionInstance =  Assertions.assertThrows(UserPointException.class,()->{
            pointService.pointCharge(-1L,10_000L);
        });
        Assertions.assertEquals(POINT_STATUS.INVALID_USERID,exceptionInstance.status);
    }
    /**
     * pointCharge
     * 충전 최소 포인트 미달
     */
    @Test
    void pointCharge_INVALID_CHARGE_AMOUNT_MIN(){
        UserPointException exceptionInstance =  Assertions.assertThrows(UserPointException.class,()->{
            pointService.pointCharge(1L,1000L);
        });
        Assertions.assertEquals(POINT_STATUS.INVALID_CHARGE_AMOUNT,exceptionInstance.status);
    }
    /**
     * pointCharge
     * 충전 최대 포인트 초과
     */
    @Test
    void pointCharge_INVALID_CHARGE_AMOUNT_MAX(){
        UserPointException exceptionInstance =  Assertions.assertThrows(UserPointException.class,()->{
            pointService.pointCharge(1L,200_001L);
        });
        Assertions.assertEquals(POINT_STATUS.INVALID_CHARGE_AMOUNT,exceptionInstance.status);
    }
    /**
     * pointCharge
     * 포인트 충전 시 검증
     */
    @Test
    void pointCharge_SUCCESS(){
        pointService.pointCharge(1L,100_000L);
        List<PointHistory> pointHistoryList = pointService.findAllPointHistoryByUserId(1L);
        UserPoint userPoint = pointService.findUserPointByUserId(1L);
        Assertions.assertEquals(userPoint.point(),100_000L);
        Assertions.assertEquals(pointHistoryList.size(),1);
        Assertions.assertEquals(pointHistoryList.get(0).type(), TransactionType.CHARGE);
        Assertions.assertEquals(pointHistoryList.get(0).amount(), 100_000L);
    }

// pointUse
    /**
     * pointCharge 테스트를 진행하면서
     * 테스트 코드 작성 시간을 간소화 할 수 있는 메소드가 있으면 좋다고 생각.
     * @param userid
     * @param pointToUse
     * @param expectStatus
     */
    void pointUse_ExceptionCaseTest(long userid, long pointToUse, long amount, POINT_STATUS expectStatus){
        UserPointException exceptionInstance = Assertions.assertThrows(UserPointException.class,()->{
            pointService.pointUse(userid,pointToUse);
        });
        Assertions.assertEquals(expectStatus,exceptionInstance.status);
    }
    void pointUse_ExceptionCaseTest(long userid, long pointToUse, POINT_STATUS expectStatus){
        pointUse_ExceptionCaseTest( userid, pointToUse, 0L, expectStatus);
    }

    /**
     * pointUse - exception
     * 유효하지 않은 아이디
     */
    @Test
    void pointUse_NOT_INVALID_USERID(){
        pointUse_ExceptionCaseTest(-1L,200_000L,POINT_STATUS.INVALID_USERID);
    }
    /**
     * pointUse - exception
     * 입력 받은 포인트가 적절한지
     * - 최소 사용 금액 <= 포인트 <= 최대 사용 금액
     */
    @Test
    void pointUser_INVALID_USE_AMOUNT(){
        // 최소 사용 금액 보다 작은 경우
        pointUse_ExceptionCaseTest(1L,900L,POINT_STATUS.INVALID_USE_AMOUNT);
        // 최대 사용 금액 보다 큰 경우
        pointUse_ExceptionCaseTest(1L,1_000_001L,POINT_STATUS.INVALID_USE_AMOUNT);
    }
    /**
     * pointUse - exception
     * 사용할 포안트가 남은 포인트 보다 많을 경우
     * 남은 포인트 - 사용할 포인트 < 0
     */
    @Test
    void pointUser_USED_POINT_UNERFLOW(){
        pointUse_ExceptionCaseTest(1L,1_000L, 900L, POINT_STATUS.USED_POINT_UNDERFLOW);
    }
    @Test
    void pointUser_SUCCESS(){
        // 포인트 충전
        pointService.pointCharge(1L,200_000L);

        // 충전 후 사용
        pointService.pointUse(1L,99_999L);

        // 남은 포인트 계산 검증
        Assertions.assertEquals((200_000L-99_999L),pointService.findUserPointByUserId(1L).point());

        List<PointHistory> pointHistoryList = pointService.findAllPointHistoryByUserId(1L);

        // 유저 1L 의 거래 내역 수 : 2 -> 충전:1 , 사용:1
        Assertions.assertEquals(2,pointHistoryList.size());

        boolean hasSpecificHistory = pointHistoryList.stream()
                .anyMatch(history -> history.amount() == 99_999L && history.type().equals(TransactionType.USE));

        Assertions.assertTrue(hasSpecificHistory);
    }


// findAllPointHistoryByUserId
    /**
     * exception
     * findAllPointHistoryByUserId
     * userId 가 유효한지
     */
    @Test
    void findAllPointHistoryByUserId_INVALID_USERID(){
        UserPointException exceptionInstance = Assertions.assertThrows(UserPointException.class,()->{
            pointService.findAllPointHistoryByUserId(-1L);
        });
        Assertions.assertEquals(POINT_STATUS.INVALID_USERID,exceptionInstance.status);
    }

    /**
     * findAllPointHistoryByUserId
     * 모든_거래내역이_있는지
     * 정상 작동
     */
    @Test
    void findAllPointHistoryByUserId_AllHistory_ALL_RIGHT(){
        pointService.pointCharge(1L,100_000L);
        pointService.pointCharge(1L,200_000L);
        pointService.pointUse(1L,200_000L);

        List<PointHistory> histories = pointService.findAllPointHistoryByUserId(1L);
        boolean hasCharge100K = histories.stream()
                .anyMatch(history -> history.amount() == 100_000L && history.type().equals(TransactionType.CHARGE));
        boolean hasCharge200K = histories.stream()
                .anyMatch(history -> history.amount() == 200_000L && history.type().equals(TransactionType.CHARGE));
        boolean hasUse200K = histories.stream()
                .anyMatch(history -> history.amount() == 200_000L && history.type().equals(TransactionType.USE));
        Assertions.assertTrue(hasCharge100K);
        Assertions.assertTrue(hasCharge200K);
        Assertions.assertTrue(hasUse200K);
        Assertions.assertEquals(100_000L,pointService.findUserPointByUserId(1L).point());
    }


    /**
     * findAllPointHistoryByUserId
     * 모든_거래내역이_있는지
     * 정상 작동
     */
    @Test
    void findAllPointHistoryByUserId_AllHistory_EXCEPTION_EMERGE(){
        pointService.pointCharge(1L,100_000L);

        // 예외 발생
        Assertions.assertThrows(UserPointException.class,()->{
            pointService.pointUse(1L,200_000L);
        });
        pointService.pointCharge(1L,200_000L);
        pointService.pointUse(1L,300_000L);

        List<PointHistory> histories = pointService.findAllPointHistoryByUserId(1L);
        Assertions.assertEquals(3,histories.size());

        boolean hasCharge100K = histories.stream()
                .anyMatch(history -> history.amount() == 100_000L && history.type().equals(TransactionType.CHARGE));
        boolean hasCharge200K = histories.stream()
                .anyMatch(history -> history.amount() == 200_000L && history.type().equals(TransactionType.CHARGE));
        boolean hasUse300K = histories.stream()
                .anyMatch(history -> history.amount() == 300_000L && history.type().equals(TransactionType.USE));

        Assertions.assertTrue(hasCharge100K);
        Assertions.assertTrue(hasCharge200K);
        Assertions.assertTrue(hasUse300K);
        Assertions.assertEquals(0,pointService.findUserPointByUserId(1L).point());
    }
}
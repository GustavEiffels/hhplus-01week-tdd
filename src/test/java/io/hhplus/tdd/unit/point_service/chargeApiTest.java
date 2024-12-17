package io.hhplus.tdd.unit.point_service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.unit.POINT_STATUS;
import io.hhplus.tdd.unit.UserPointException;
import io.hhplus.tdd.unit.user_point_validation.UserPointValidation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;


// CHARGING
/**
 * userId 와 amount 를 입력 받으면
 * userId 의 현재 값에 전달받은 amount 값을 더한다.
 *
 * 1.userId 검증
 * 2.입력받은 amount 검증
 *  3.userId 조회
 * 3.충전 정책에 맞는지 검증
 * 4.History 에 추가 검증
 */
public class chargeApiTest {
    @Mock
    private UserPointTable      userPointTable      ;
    @Mock
    private PointHistoryTable   pointHistoryTable   ;
    private UserPointValidation userPointValidation ;
    private PointService        userPointService    ;

    /**
     * Test 시 자주 UserPointService 를 반복적으로 생성하기 위해서
     * 작성한 함수
     * @return
     */
    @BeforeEach
    public void setUp(){
        userPointTable      = Mockito.mock(UserPointTable.class);
        pointHistoryTable   = Mockito.mock(PointHistoryTable.class);
        userPointValidation = new UserPointValidation();
        userPointService    = new PointService(userPointTable,userPointValidation,pointHistoryTable);
    }


    /**
     * test1.
     * 유효한 유저 아이디 값이 들어오는지 확인
     * 그렇지 않을 경우 예외 발생
     */
    @Test
    void when_notValidUserID_then_Exception(){
        UserPointException exception = Assertions.assertThrows(UserPointException.class,()->{
            userPointService.charge(-1L,10_000L);
        });
        Assertions.assertEquals(exception.getStatus(), POINT_STATUS.INVALID_USERID);
    }

    /**
     * test2.
     * 입력받은 amount 검증
     * amount 의 정책 검증 -> 그렇지 않을 경우 예외 발생
     *  amount 최소 기준을 맞추지 못하거나
     *  amount 최대 기준을 맞추지 못하거나
     */
    @Test
    void when_amountNotEnough_then_Exception_Emerge(){
        UserPointException exception = Assertions.assertThrows(UserPointException.class,()->{
            userPointService.charge(1L,1_000L);
        });
        Assertions.assertEquals(exception.getStatus(), POINT_STATUS.INVALID_CHARGE_AMOUNT);
    }


    /**
     * test3.
     * 충전 정책에 맞는지 검증
     * amount 의 정책 검증 -> 그렇지 않을 경우 예외 발생
     *  최대 충전 금액을 초과하는 경우 예외
     */
    @Test
    void when_OutOfCharging_then_Exception_Emerge(){

        // user Id 가 1L 인 유저의 레코드를 미리 생성 
        when(userPointTable.selectById(1L))
                .thenReturn(new UserPoint(1L,999_999_999L,System.currentTimeMillis()));

        UserPointException exception = Assertions.assertThrows(UserPointException.class,()->{
            userPointService.charge(1L,5_000L);
        });
        Assertions.assertEquals(exception.getStatus(), POINT_STATUS.OUT_OF_CHARGE);
    }

}

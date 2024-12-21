package io.hhplus.tdd.unit.user_point;

import io.hhplus.tdd.point.POINT_STATUS;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.UserPointException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserPointTest {

    /**
     * recharge
     * 충전을 위한 테스트 함수
     * @param leftPoint
     * @param pointToCharge
     * @param pointStatus
     */
    private void getChargeTest(long leftPoint,long pointToCharge,POINT_STATUS pointStatus){
        UserPointException exception = Assertions.assertThrows(UserPointException.class,()->{
            new UserPoint(
                    1L,
                    leftPoint,
                    System.currentTimeMillis()).charge(pointToCharge);
        });
        Assertions.assertEquals(pointStatus, exception.status);
    }

    /**
     * use
     * 포인트 사용을 위한 테스트 함수
     * @param leftPoint
     * @param pointToUse
     * @param pointStatus
     */
    private void getUsePointTest(long leftPoint,long pointToUse,POINT_STATUS pointStatus){
        UserPointException exception = Assertions.assertThrows(UserPointException.class,()->{
            new UserPoint(
                    1L,
                    leftPoint,
                    System.currentTimeMillis()).use(pointToUse);
        });
        Assertions.assertEquals(pointStatus, exception.status);
    }


    /**
     * recharge
     * 1.충전하려는 금액이 최소 금액 보다 작으면 예외가 발생해야한다.
     */
    @Test
    void 충전하려는_금액이_최소_금액보다_작으면_예외(){
        getChargeTest(0L,9_000L,POINT_STATUS.INVALID_CHARGE_AMOUNT);
    }

    /**
     * recharge
     * 2.충전하려는 금액이 최대 금액 보다 크면 예외가 발생해야한다.
     */
    @Test
    void 충전하려는_금액이_최대_금액보다_크면_예외(){
        getChargeTest(0L,200_001L,POINT_STATUS.INVALID_CHARGE_AMOUNT);
    }

    /**
     * recharge
     * 3.충전 완료 후 금액이 최대 보유 포인트보다 클 경우 에러
     */
    @Test
    void 충전_완료_후_금액이_최대_보유_포인트보다_클_경우_에러(){
        getChargeTest(1_990_001L,10_000L,POINT_STATUS.CHARGE_POINT_OVERFLOW);
    }

    /**
     * use
     * 1.사용하려는 금액이 최소 사용금액 보다 적을 경우 예외 발생
     */
    @Test
    void 사용하려는_금액이_최소_사용금액_보다_적을_경우_예외(){
        getUsePointTest(0L,100L,POINT_STATUS.INVALID_USE_AMOUNT);
    }

    /**
     * use
     * 2.사용하려는 금액이 최대 사용금액 보다 많을 경우 예외 발생
     */
    @Test
    void 사용하려는_금액이_최대_사용금액_보다_많을_경우_예외(){
        getUsePointTest(0L,1_000_001L,POINT_STATUS.INVALID_USE_AMOUNT);
    }

    /**
     * use
     * 3.사용 후 금액이 0 보다 작으면 예외
     */
    @Test
    void 사용_후_금액이_0_보다_작으면_예외(){
        getUsePointTest(10_000L,20_000L,POINT_STATUS.USED_POINT_UNDERFLOW);
    }
}

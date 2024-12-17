package io.hhplus.tdd.unit.user_point_validation;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * UserPoint 의 검증 컴포넌트를 테스트
 *
 * 검증 목록
 * 1. userId 는 0보다 큰 양의 정수 여야 한다 .
 * 2. point 의 최소 값은 0 이어야 한다.
 * 3. point 의 최대 값은 1_000_000_000 포인트 ( 10억 포인트 ) 이어야 하며, 초과 해서는 안된다.
 * 4. Transaction 에 사용할 포인트 ( 충전이나 사용 시 포인트 ) 는 반드시 0보다 큰 양의 정수 여야 한다.
 */
public class UserPointValidationTest {

    private UserPointValidation pointValidation;

    @BeforeEach
    public void setUp(){
        pointValidation = new UserPointValidation();
    }

// PARAMETER : pointToCharge
    /**
     * @Method : isValidChargePoint
     * 충전 가능한 포인트인지 확인
     *
     * - 최대 충전  2_000_000 포인트
     * - 최소 충전  5_000     포인트
     *
     * point  > 2_000_000  : f
     * point  < 5_000      : f
     *
     */
    @Test
    void 충전_가능한_포인트인지_확인(){

        // ** point < 5_000 : false
        Assertions.assertFalse(pointValidation.isValidRechargeAmount(4_000L));

        // ** point > 2_000_000 : false
        Assertions.assertFalse(pointValidation.isValidRechargeAmount(2_000_001L));

        // ** 4_999L < point < 2_000_001 : false
        Assertions.assertFalse(pointValidation.isValidRechargeAmount(2_000_001L));
    }


// PARAMETER : pointToUse
    /**
     * @Method : isValidUsePoint
     * 한번에 사용 가능한 포인트인지 확인
     *
     * - 최대 충전  1_000_000 포인트
     * - 최소 충전  10_000     포인트
     *
     * point  > 1_000_000  : f
     * point  < 10_000     : f
     *
     */
    @Test
    void 사용_가능한_포인트인지_확인(){

        // ** point < 5_000 : false
        Assertions.assertFalse(pointValidation.isValidUsePoint(4_000L));

        // ** point > 2_000_000 : false
        Assertions.assertFalse(pointValidation.isValidUsePoint(2_000_001L));

        // ** 4_999L < point < 2_000_001 : false
        Assertions.assertFalse(pointValidation.isValidUsePoint(2_000_001L));
    }


// FIELD : userID
    /**
     * @Method : isValidUserId(long userId)
     *
     * userId 검증 테스트
     * userId 는 0보다 큰 정수, 즉 자연수만 사용한다 .
     *
     * true  : 0 보다큰 정수
     * false :  userId = 0L;
     *          userId = -1L;
     */
    @Test
    void 유저_아이디가_양수가_아니면_예외_발생(){
        // ** userId 가 음수일 경우
        Assertions.assertFalse(pointValidation.isValidUserId(-1L));

        // ** userId 가 0일 경우
        Assertions.assertFalse(pointValidation.isValidUserId(0L));
    }


// FIELD : point
    /**
     * @Method : isPointAvailable(long currentPoint, long pointToUse )
     * long currentPoint : 현재 남은 포인트
     * long pointToUse   : 사용할 포인트
     *  남아 있는 포인트 <  사용 할 포인트 : false
     *  남아 있는 포인트 == 사용 할 포인트 : true
     *  남아 있는 포인트 >  사용 할 포인트 : true
     */
    @Test
    void 현재남은포인트_보다_사용할_포인트가_많은_경우_예외(){

        // 남아 있는 포인트 < 사용 할 포인트 : false
        Assertions.assertFalse(pointValidation.canUsePoints(3000L,10000L));

        // 남아 있는 포인트 == 사용 할 포인트 : true
        Assertions.assertTrue(pointValidation.canUsePoints(10000L,10000L));

        // 남아 있는 포인트 > 사용 할 포인트 : true
        Assertions.assertTrue(pointValidation.canUsePoints(20000L,10000L));
    }


    /**
     * @Method : isChargeAvailable(long currentPoint, long pointToCharge )
     * long currentPoint : 현재 남은 포인트
     * long pointToUse   : 충전 할 포인트
     *
     * 1. 남은 포인트 + 충전 포인트 <= 최대 포인트 -> true
     * 2. 남은 포인트 > 최대 포인트             -> false
     * 3. 남은 포인트 + 충전 포인트 > 최대 포인트  -> false
     */
    @Test
    void 충전_가능한_포인트_테스트(){
        // ** 1. 남은 포인트 + 충전 포인트 <= 최대 포인트 -> true
        Assertions.assertTrue(
                pointValidation.canRecharge(
                        900_000_000L,
                        100_000_000L));

        // ** 2. 남은 포인트 > 최대 포인트             -> false
        Assertions.assertFalse(
                pointValidation.canRecharge(
                        1_000_000_001L,
                        1L));

        // ** 3. 남은 포인트 + 충전 포인트 > 최대 포인트  -> false
        Assertions.assertFalse(
                pointValidation.canRecharge(
                        900_000_000L,
                        120_000_000L));
    }

}

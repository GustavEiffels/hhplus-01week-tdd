package io.hhplus.tdd.unit.user_point_validation;

/**
    사용하지 않은 로직
 -> 해당 포인트 검증 로직을 domain 으로 이동
 */
public class UserPointValidation {

    private final long maxPoint       = 2_000_000L;
// use point
    private final long maxUsePoint    = 1_000_000L;
    private final long minUsePoint    = 1_000L;
// charge point
    private final long maxChargePoint = 200_000L;
    private final long minChargePoint = 10_000L;

    /**
     * 정책 : userId 는 0 이상의 양수이어야 한다.
     * @param userId
     * @return
     */
    public boolean isValidUserId( long userId ){ return userId > 0; }

    /**
     * @Method : canUsePoints(long currentPoint, long pointToUse )
     * 포인트를 사용할 수 있는 상태인지 확인하는 함수
     *  남아 있는 포인트 <  사용 할 포인트 : false
     *  남아 있는 포인트 == 사용 할 포인트 : true
     *  남아 있는 포인트 >  사용 할 포인트 : true
     * @param currentPoint : 현재 남은 포인트
     * @param pointToUse   : 사용할 포인트
     * @return
     */
    public boolean canUsePoints(long currentPoint, long pointToUse) {
        // 최소 포인트가 0 이기 때문에 가능
        return  currentPoint >= pointToUse;
    }

    /**
     * @Method : isValidUsePoint(long pointToUse)
     * - 최대 충전  maxUsePoint     포인트
     * - 최소 충전  minUsePoint     포인트
     * @param pointToUse : 사용 할 포인트
     * @return
     */
    public boolean isValidUsePoint(long pointToUse) {
        return pointToUse >= minUsePoint && pointToUse <= maxUsePoint;
    }

    /**
     * @Method : canRecharge(long currentPoint, long pointToCharge)
     * 현재 충전이 가능한 상태 확ㅇ니
     *
     * 1. 남은 포인트 + 충전 포인트 <= 최대 포인트 -> true
     * 2. 남은 포인트 > 최대 포인트             -> false
     * 3. 남은 포인트 + 충전 포인트 > 최대 포인트  -> false
     * @param currentPoint  : 현재 남은 포인트
     * @param pointToCharge : 충전 할 포인트
     * @return
     */
    public boolean canRecharge(long currentPoint, long pointToCharge) {
        if(currentPoint > maxPoint) return false;
        else                        return currentPoint+pointToCharge <= maxPoint;
    }

    /**
     * @Method : isValidRechargeAmount(long pointToCharge)
     * 충전할 포인트가 정책에 맞는 포인트인지 확인
     * - 최대 충전  maxChargePoint     포인트
     * - 최소 충전  minChargePoint     포인트
     * @param pointToCharge : 충전 할 포인트
     * @return
     */
    public boolean isValidRechargeAmount(long pointToCharge) {
        return pointToCharge >= minChargePoint && pointToCharge <= maxChargePoint;
    }
}



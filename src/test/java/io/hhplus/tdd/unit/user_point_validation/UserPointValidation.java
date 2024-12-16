package io.hhplus.tdd.unit.user_point_validation;

/**
    검증 컴포넌트를 만들어서 하는 것이 단위 테스트 시 이점이 있다고 하여
    새로 생성 후 테스트 수행
 */
public class UserPointValidation {

    private final long maxPoint       = 1_000_000_000L;
    private final long maxUsePoint    = 1_000_000L;
    private final long minUsePoint    = 10_000L;
    private final long maxChargePoint = 2_000_000L;
    private final long minChargePoint = 5_000L;

    /**
     * 정책 : userId 는 0 이상의 양수이어야 한다.
     * @param userId
     * @return
     */
    public boolean isValidUserId( long userId ){ return userId > 0; }

    /**
     * @Method : isPointAvailable(long currentPoint, long pointToUse )
     *  남아 있는 포인트 <  사용 할 포인트 : false
     *  남아 있는 포인트 == 사용 할 포인트 : true
     *  남아 있는 포인트 >  사용 할 포인트 : true
     * @param currentPoint : 현재 남은 포인트
     * @param pointToUse   : 사용할 포인트
     * @return
     */
    public boolean isPointAvailable(long currentPoint, long pointToUse) {
        // 최소 포인트가 0 이기 때문에 가능
        return  currentPoint >= pointToUse;
    }

    /**
     * @Method : isChargeAvailable(long currentPoint, long pointToCharge)
     * 1. 남은 포인트 + 충전 포인트 <= 최대 포인트 -> true
     * 2. 남은 포인트 > 최대 포인트             -> false
     * 3. 남은 포인트 + 충전 포인트 > 최대 포인트  -> false
     * @param currentPoint  : 현재 남은 포인트
     * @param pointToCharge : 충전 할 포인트
     * @return
     */
    public boolean isChargeAvailable(long currentPoint, long pointToCharge) {
        if(currentPoint > maxPoint) return false;
        else                        return currentPoint+pointToCharge <= maxPoint;
    }

    /**
     * @Method : isValidChargePoint(long pointToCharge)
     * - 최대 충전  maxChargePoint     포인트
     * - 최소 충전  minChargePoint     포인트
     * @param pointToCharge : 충전 할 포인트
     * @return
     */
    public boolean isValidChargePoint(long pointToCharge) {
        return pointToCharge >= minChargePoint && pointToCharge <= maxChargePoint;
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
}

package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    private static final long maxPoint       = 2_000_000L;
    private static final long maxUsePoint    = 1_000_000L;
    private static final long minUsePoint    = 1_000L;
    private static final long maxChargePoint = 200_000L;
    private static final long minChargePoint = 10_000L;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public long charge(long pointToCharge) {
        if(minChargePoint > pointToCharge){
            throw new UserPointException(POINT_STATUS.INVALID_CHARGE_AMOUNT);
        }
        if(maxChargePoint < pointToCharge){
            throw new UserPointException(POINT_STATUS.INVALID_CHARGE_AMOUNT);
        }
        if( (this.point+pointToCharge) > maxPoint ) {
            throw new UserPointException(POINT_STATUS.CHARGE_POINT_OVERFLOW);
        }
        return this.point+pointToCharge;
    }

    public long use(long pointToUse) {
        if( pointToUse < minUsePoint ){
            throw new UserPointException(POINT_STATUS.INVALID_USE_AMOUNT);
        }
        if( pointToUse > maxUsePoint ){
            throw new UserPointException(POINT_STATUS.INVALID_USE_AMOUNT);
        }
        if( this.point-pointToUse < 0 ){
            throw new UserPointException(POINT_STATUS.USED_POINT_UNDERFLOW);
        }
        return this.point-pointToUse;
    }
}

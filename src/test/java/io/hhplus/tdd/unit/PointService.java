package io.hhplus.tdd.unit;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.unit.user_point_validation.UserPointValidation;

/**
 * UserService
    - Validation 기능을 통과하면
    - Repository API 기능을 사용하도록 구성

 */
public class PointService {
    private final UserPointTable      userPointTable     ;
    private final UserPointValidation userPointValidation;
    public PointService(UserPointTable userPointTable, UserPointValidation userPointValidation){
        this.userPointTable      = userPointTable;
        this.userPointValidation = userPointValidation;
    }

    /**
     *
     * @param userId
     * @return
     */
    public UserPoint findByUserId(long userId) {
        if( !userPointValidation.isValidUserId(userId) ){ throw new UserPointException(POINT_STATUS.INVALID_NUMBER_FORMAT);}
        return userPointTable.selectById(userId);
    }
}

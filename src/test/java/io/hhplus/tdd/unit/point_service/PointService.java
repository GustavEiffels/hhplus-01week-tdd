package io.hhplus.tdd.unit.point_service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.unit.POINT_STATUS;
import io.hhplus.tdd.unit.UserPointException;
import io.hhplus.tdd.unit.user_point_validation.UserPointValidation;

/**
 * UserService
    - Validation 기능을 통과하면
    - Repository API 기능을 사용하도록 구성
 */
public class PointService {
    private final UserPointTable      userPointTable        ;
    private final UserPointValidation userPointValidation   ;

    private final PointHistoryTable   pointHistoryTable     ;

    public PointService(
            UserPointTable userPointTable,
            UserPointValidation userPointValidation,
            PointHistoryTable   pointHistoryTable
            ){
        this.userPointTable      = userPointTable       ;
        this.userPointValidation = userPointValidation  ;
        this.pointHistoryTable   = pointHistoryTable    ;
    }

//  READ
    /**
     * userId 를 검증하고 DataBase 에서 UserPoint 조회
     * @param userId
     * @return
     */
    // @Transactional
    public UserPoint findByUserId(long userId) {
        if( !userPointValidation.isValidUserId(userId) ){ throw new UserPointException(POINT_STATUS.INVALID_USERID);}
        return userPointTable.selectById(userId);
    }


    // UPDATE
    /**
     * ? @Transactional 과 같은 기능은 있나?
     * @param userId
     * @param amount
     * @return
     */
    public UserPoint charge(long userId, long amount) {
        if( !userPointValidation.isValidUserId(userId) )        { throw new UserPointException(POINT_STATUS.INVALID_USERID);}
        if( !userPointValidation.isValidChargePoint(amount) )   { throw new UserPointException(POINT_STATUS.INVALID_CHARGE_AMOUNT);}

        // findUser
        UserPoint userPoint = findByUserId(userId);

        if( !userPointValidation.isChargeAvailable(userPoint.point(),amount) ) {
            throw new UserPointException(POINT_STATUS.OUT_OF_CHARGE);
        }

        // charging
        userPoint = userPointTable.insertOrUpdate(userId,amount);

        // InsertHistoryTable
        pointHistoryTable.insert(userId,userPoint.point()+amount, TransactionType.CHARGE,System.currentTimeMillis());

        return userPoint;
    }

// CREATE
    /**
     *
     */

}

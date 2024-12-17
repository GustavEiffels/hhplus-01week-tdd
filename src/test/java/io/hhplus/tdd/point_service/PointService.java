package io.hhplus.tdd.point_service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.POINT_STATUS;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.UserPointException;
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
            UserPointTable       userPointTable,
            UserPointValidation  userPointValidation,
            PointHistoryTable    pointHistoryTable
            ){
        this.userPointTable      = userPointTable       ;
        this.userPointValidation = userPointValidation  ;
        this.pointHistoryTable   = pointHistoryTable    ;
    }

    /**
     * userId 를 검증하고 DataBase 에서 UserPoint 조회
     * @param userId
     * @return
     */
    public UserPoint findByUserId(long userId) {
        if( !userPointValidation.isValidUserId(userId) ){ throw new UserPointException(POINT_STATUS.INVALID_USERID);}
        return userPointTable.selectById(userId);
    }


    /**
     /**
     * userId 와 amount 를 입력 받으면
     * userId 의 현재 값에 전달받은 amount 값을 더한다.
     *
     * 1.userId 검증
     * 2.입력받은 amount 검증
     *  3.userId 조회
     * 3.충전 정책에 맞는지 검증
     * 4.History 에 추가 검증
     *
     * @param userId
     * @param amount
     * @return
     */
    public UserPoint charge(long userId, long amount) {
        if( !userPointValidation.isValidUserId(userId) )        { throw new UserPointException(POINT_STATUS.INVALID_USERID);}
        if( !userPointValidation.isValidRechargeAmount(amount) )   { throw new UserPointException(POINT_STATUS.INVALID_CHARGE_AMOUNT);}

        // findUser
        UserPoint userPoint = findByUserId(userId);

        if( !userPointValidation.canUsePoints(userPoint.point(),amount) ) {
            throw new UserPointException(POINT_STATUS.CHARGE_POINT_OVERFLOW);
        }

        // charging
        userPoint = userPointTable.insertOrUpdate(userId,amount);

        // InsertHistoryTable
        pointHistoryTable.insert(userId,userPoint.point()+amount, TransactionType.CHARGE,System.currentTimeMillis());

        return userPoint;
    }



}

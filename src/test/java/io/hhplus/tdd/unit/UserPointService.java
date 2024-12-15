package io.hhplus.tdd.unit;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;

public class UserPointService {
    private final UserPointTable userPointTable;
    public UserPointService(UserPointTable userPointTable){
        this.userPointTable = userPointTable;
    }
    public UserPoint findByUserId(long userId) {
        if(userId < 1){
            throw new UserPointException(POINT_STATUS.INVALID_NUMBER_FORMAT);
        }
        return userPointTable.selectById(userId);
    }
}

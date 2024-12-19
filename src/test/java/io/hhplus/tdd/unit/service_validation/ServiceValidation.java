package io.hhplus.tdd.unit.service_validation;

import io.hhplus.tdd.point.POINT_STATUS;
import io.hhplus.tdd.point.UserPointException;

public class ServiceValidation {
    public void isValidUserId(long userId) {
        if(userId < 1L){
            throw new UserPointException(POINT_STATUS.INVALID_USERID);
        }
    }
}

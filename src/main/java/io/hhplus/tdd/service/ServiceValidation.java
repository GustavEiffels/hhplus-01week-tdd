package io.hhplus.tdd.service;

import io.hhplus.tdd.point.POINT_STATUS;
import io.hhplus.tdd.point.UserPointException;
import org.springframework.stereotype.Component;

@Component
public class ServiceValidation {
    /** isValidUserId
     * userid 의 정책 검증
     * @param userId
     */
    public void isValidUserId(long userId) {
        if(userId < 1L){
            throw new UserPointException(POINT_STATUS.INVALID_USERID);
        }
    }
}

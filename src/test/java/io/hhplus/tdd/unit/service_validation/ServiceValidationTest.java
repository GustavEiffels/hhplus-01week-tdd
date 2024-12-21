package io.hhplus.tdd.unit.service_validation;

import io.hhplus.tdd.point.POINT_STATUS;
import io.hhplus.tdd.point.UserPointException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceValidationTest {

    /**
     * userId 는 자연수만 사용
     * userId가_1보다_작으면_예외
     */
    @Test
    void userId가_1보다_작으면_예외(){
        ServiceValidation serviceValidation = new ServiceValidation();
        UserPointException exceptionInstance = Assertions.assertThrows(UserPointException.class,()->{
            serviceValidation.isValidUserId(0L);
        });
        Assertions.assertEquals(POINT_STATUS.INVALID_USERID,exceptionInstance.status);
    }

    /**
     * userId 는 자연수만 사용
     * userId 가 자연수이면 예외를 발생시키지 않음
     */
    @Test
    void userId가_자연수이면_예외발생하지_않음(){
        ServiceValidation serviceValidation = new ServiceValidation();
        Assertions.assertDoesNotThrow(()->serviceValidation.isValidUserId(1L));
    }
}

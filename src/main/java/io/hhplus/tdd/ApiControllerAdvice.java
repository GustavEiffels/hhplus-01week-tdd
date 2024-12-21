package io.hhplus.tdd;

import io.hhplus.tdd.point.POINT_STATUS;
import io.hhplus.tdd.point.UserPointException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentTypeMismatchException e){
        return ResponseEntity.status(400).body(new ErrorResponse("400", "잘못된 인자를 입력하였습니다. "+e.getMessage()));
    }

    @ExceptionHandler(value = UserPointException.class)
    public ResponseEntity<ErrorResponse> handleException(UserPointException e){
        ErrorResponse response;
        if(e.status.equals(POINT_STATUS.INVALID_USERID)){
            return ResponseEntity.status(400).body(new ErrorResponse("400", "잘못된 유저 아이디 입니다."));
        }
        if(e.status.equals(POINT_STATUS.INVALID_CHARGE_AMOUNT)){
            return ResponseEntity.status(400).body(new ErrorResponse("400", "충전가능한 충전 금액이 아닙니다. 충전 가능 포인트 10,000 ~ 200,000 Point "));
        }
        if(e.status.equals(POINT_STATUS.CHARGE_POINT_OVERFLOW)){
            return ResponseEntity.status(400).body(new ErrorResponse("400", "충전 시 최대 충전 금액을 초과합니다. 최대 충전 금액 : 2,000,000 Point"));
        }
        if(e.status.equals(POINT_STATUS.INVALID_USE_AMOUNT)){
            return ResponseEntity.status(400).body(new ErrorResponse("400", "사용 가능한 금액이 아닙니다. 사용 가능 포인트 : 1,000 ~ 1,000,000 Point"));
        }
        if(e.status.equals(POINT_STATUS.USED_POINT_UNDERFLOW)){
            return ResponseEntity.status(400).body(new ErrorResponse("400", "포인트가 부족합니다."));
        }
        return ResponseEntity.status(400).body(new ErrorResponse("400", e.getMessage()));

    }
}

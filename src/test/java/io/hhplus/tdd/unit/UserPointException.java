package io.hhplus.tdd.unit;

public class UserPointException extends RuntimeException {
    private final POINT_STATUS status;

    public UserPointException(POINT_STATUS status, String message){
        super(message);
        this.status = status;
    }

    public UserPointException(POINT_STATUS status){
        this.status = status;
    }
}

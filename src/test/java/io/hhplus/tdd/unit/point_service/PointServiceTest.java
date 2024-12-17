package io.hhplus.tdd.unit.point_service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.unit.POINT_STATUS;
import io.hhplus.tdd.unit.UserPointException;
import io.hhplus.tdd.unit.user_point_validation.UserPointValidation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class PointServiceTest {

    /**
     * 사전스터디에서 공부한 내용인 Mock 을 사용해 보았다.
     * DataBase 를 사용하는 부분을 대체하여 사용하였다.
     * 어떤 이점이 있는지 알아보는 것이 좋을 것 같다.
     */
    @Mock
    private UserPointTable      userPointTable      ;
    @Mock
    private PointHistoryTable   pointHistoryTable   ;
    private UserPointValidation userPointValidation ;
    private PointService        userPointService    ;

    /**
     * Test 시 자주 UserPointService 를 반복적으로 생성하기 위해서
     * 작성한 함수
     * @return
     */
    @BeforeEach
    public void setUp(){
        userPointTable      = Mockito.mock(UserPointTable.class);
        pointHistoryTable   = Mockito.mock(PointHistoryTable.class);
        userPointValidation = new UserPointValidation();
        userPointService    = new PointService(userPointTable,userPointValidation,pointHistoryTable);
    }

    /**
     * 정책에 맞는 userId 입력 받고
     * userId 를 return
     *
     * userId 가 음수 : f -> userPointValidation 에 구현
     * userId 가 0   : f -> userPointValidation 에 구현
     *
     */
    @Test
     void userId로_user_찾기_faile(){
         UserPointException userPointException = Assertions.assertThrows(UserPointException.class,()->{
             userPointService.findByUserId(-1L);
         });
         Assertions.assertEquals(userPointException.getStatus(), POINT_STATUS.INVALID_NUMBER_FORMAT);
     }

// CHARGING
    /**
     * userId 와 amount 를 입력 받으면
     * userId 의 현재 값에 전달받은 amount 값을 더한다.
     *
     * 1.userId 검증
     * 2.입력받은 amount 검증
     * 3.충전 정책에 맞는지 검증
     * 4.History 에 추가 검증
     */



}

package io.hhplus.tdd.unit;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.unit.user_point_validation.UserPointValidation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class PointServiceTest {

    /**
     * 사전스터디에서 공부한 내용인 Mock 을 사용해 보았다.
     * DataBase 를 사용하는 부분을 대체하여 사용하였다.
     *
     * 어떤 이점이 있는지 알아보는 것이 좋을 것 같다.
     */
    @Mock
    private UserPointTable      userPointTable      ;
    private UserPointValidation userPointValidation ;
    private PointService        userPointService;

    /**
     * Test 시 자주 UserPointService 를 반복적으로 생성하기 위해서
     * 작성한 함수
     * @return
     */
    @BeforeEach
    public void setUp(){
        userPointTable      = Mockito.mock(UserPointTable.class);

        userPointValidation = new UserPointValidation();
        userPointService    = new PointService(userPointTable,userPointValidation);
    }
}

package io.hhplus.tdd.unit;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class UserPointServiceTest {

    /**
     * 사전스터디에서 공부한 내용인 Mock 을 사용해 보았다.
     * DataBase 를 사용하는 부분을 대체하여 사용하였다.
     *
     * 어떤 이점이 있는지 알아보는 것이 좋을 것 같다.
     */
    @Mock
    private UserPointTable userPointTable;

    private UserPointService userPointService;

    /**
     * Test 시 자주 UserPointService 를 반복적으로 생성하기 위해서
     * 작성한 함수
     * @return
     */
    @BeforeEach
    public void setUp(){
        userPointTable   = Mockito.mock(UserPointTable.class);
        userPointService = new UserPointService(userPointTable);
    }


    /**
     * UserPoint 를 조회하는 함수
     * - userId 를 사용하여 UserPointTable 에서 UserPoint 를 조회하는 함수
     *
     * 2024-12-15 의문 : Controller 의 PathVariable 에서 userId 에 대한 예외 처리를 하는데 ( Long 으로 받을텐데 무슨 걱정 ??? )
     * 굳이 해야할 필요가 있을까? => 범용적으로 사용하기 위한 함수 이기 때문에 검증을 하는 것이 좋다. ( 멘토링 때 물어볼것 )
     *
     * userId 가 음수 일 경우 예외 처리한다. => 내가 정한 userId 정책 ( userId 는 양수만 사용 )
     */
    @Test
    void userId가_양수가_아니면_예외_발생(){
        Assertions.assertThrows(UserPointException.class,()->{
            userPointService.findByUserId(-1L);
        });
    }


}

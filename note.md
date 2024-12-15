## 2024-12-15

# Table 분석 
> Table 영역은 우리가 건드리면 안되는 영역이다,

# Custom Policy 
## 조회 시 정책
1. 사용자 id 는 항상 양의 정수여야 한다.
2. 사용자 id 로 히스토리 조회 시 list size 가 존재하지 않으면 예외를 발생 


## UserPointTable 
- selectById
  @Param : long id; 
  > 사용자 id 를 Parameter 로 받는다. 
  > 받은 id 를 table 로 조회 => id 가 존재하지 않을 경우 새로운 UserPoint 생성 
  - id 에 대한 유효성 검증 
    - id 가 null 이나 '' 이면 안된다. 
    - id 는 0 보다 커야 한다.
    - long의 최대 수 보다 큰 값이 입력 되는 경우 처리 
  
- insertOrUpdate 
  @Param : long id      ;
  @Param : long amount  ;
  > 사용자 id 와 amount 를 입력받아 새로운 UserPoint 객체를 생성
  > :: 연산이 끝난 뒤에 id 에 다시 조정 
  - 이미 올바른 값의 amount 가 입력이 되어야 한다.
  - amount 에 대한 검증 
    - amount 가 0보다 작으면 안된다.
  - id 에 대한 유효성 검증 
  
### UserPoint
- long id,          // 사용자 아이디 
- long point,       // 해당 사용자의 남은 포인트 
- long updateMillis // 가장 최근 업데이트 된 시간 
  
  
## PointHistoryTable 
- insert 
  @Param  : long userId,
  @Param  : long amount,
  @Param  : TransactionType type,
  @Param  : updateMillis 
  @Return : PointHistory 
  - Point 를 사용하거나, 충전하는 경우 
  > 사용자 아이디와 요청들어온 금액, 트랜젹션 타입 및 업데이트 시간을 받는다.
  - UserPoint 에 적용된 이후에 실행 되어야 한다. -> UserPoint 에 정상적으로 적용된 것만 기재 ( Transaction 횟수가 줄어듦 )
  - 생성한 History 를 리턴 
  
- selectAllByUserId
  @Param : long userId
  - userId 를 입력 받아, userId 가 같은 모든 history 를 추출 
  - return 하는 개수도 생각해야하지 않을까? 근데 DB 기능이 고정적이여서.. 



# 기능 
## userId 로 유저 조회하기 
- userId null 이나 '' 이면 에러 발생 => compile 에러  
- userId 가 음수 이면 에러 발생 

## 예외 처리 해야할 것 
- userId 가 음수로 될 때 에러 
- userPoint 연산 결과가 음수일 경우 에러 
- userPoint history 가 존재하지 않을 경우 에러 
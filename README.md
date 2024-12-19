# 동시성 제어에 대한 분석 및 보고서


## 동시성이란 무엇인가?

<aside>

자바를 하려면 동시성에 대해서 잘 알아 놓으라는 말을 들은 
기억이 있어 관련된 인프런 강의를 들었다.
[인프런][https://www.inflearn.com/course/%EC%9E%90%EB%B0%94-%EB%8F%99%EC%8B%9C%EC%84%B1-%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D-%EB%A6%AC%EC%95%A1%ED%8B%B0%EB%B8%8C-part1/dashboard]

 
여기서 배운 내용을 머리속에서 끄집어 내어  정의를 한다면
<br>
>**동시성이란 하나의 코어에서 다수의 쓰레드가 번갈아 실행되는 것**
이 때문에 같은 시간에 실행되는 것처럼 보이는 상태를 말한다.   
이는 다른 코어에서 같은 시간에 실행되는 병렬성과는 완전 다른 개념이다.   
동시성은 여러 작업을 효율적으로 처리할 수 있게 도와주며, I/O 대기 시간을 줄여 성능을 개선할 수 있다.    





<br>

이번 주차에서는 **동시성 제어**가 필요한 부분은    
**포인트 사용**과 **포인트 충전** 로직 이다.    
데이터 테이블의 데이터를 수정하는 비즈니스 로직이기 때문에,    
<br>
여러 스레드가 동시에 접근할 때 발생할 수 있는 문제를 방지하기 위해    
동시성 제어가 필요해 보였다.   
<br>
<br>
## 테스트 조건    

- Thread : 100 개 사용   
- TEST CODE   
```java

    private static final int THREAD_NUM = 100;

    void timeRecorder(long start, long end){
        double elapsedTime = (end - start) / 1000.0;
        String formattedTime = String.format("%.4f", elapsedTime);
        System.out.println("경과 시간 : " + formattedTime + " 초");
    }

 /**
     * 같은 사용자가
     * 동시에 10_000 포인트 충전
     */
    @Test
    void sameUser_pointCharge() throws InterruptedException {
        System.out.println("같은 사용자가 한번에 스레드 수 만큼 충전할 때");
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);
        long start_time = System.currentTimeMillis();

        for(int i = 0; i<THREAD_NUM; i++){
            executor.submit(()->{
                pointService.pointCharge(1L,10_000L);
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long end_time = System.currentTimeMillis();
        timeRecorder(start_time,end_time);
        System.out.println("예상 포인트 : "+THREAD_NUM*10_000);
        System.out.println("실제 저장된 포인트 : "+pointService.findUserPointByUserId(1L).point());
    }

    /**
     * 전부 다른 유저 들로 할 경우
     * @throws InterruptedException
     */
    @Test
    void differentUser_pointCharge() throws InterruptedException {
        System.out.println("동시에 다른 유저들이 각각 요청");
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);

        long start_time = System.currentTimeMillis();
        AtomicLong exceptionLong = new AtomicLong(1);

        for(int i = 1; i<=THREAD_NUM; i++){
            executor.submit(()->{
                final long userid = exceptionLong.getAndIncrement();
                pointService.pointCharge(userid,10_000L);
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long end_time = System.currentTimeMillis();
        timeRecorder(start_time,end_time);

        long totalPoint = 0;

        for(int i = 1; i<=THREAD_NUM; i++){
            final long userId = i;
            long point = pointService.findUserPointByUserId(userId).point();
            totalPoint+= point;
        }
        System.out.println("예상 포인트 : "+THREAD_NUM*10_000);
        System.out.println("포인트 총합 : "+totalPoint);
    }
```
<br>

### 1. 동시성을 보장하지 않은 상태에서 테스트   
1. 동시에 같은 유저 여러번 요청   
    ![Screenshot_2024-12-20_at_03 22 26](https://github.com/user-attachments/assets/a99d67c9-6a3b-4ab2-be53-afff918d4f4a)

1. 다른 유저 각각 요청  
    
  ![Screenshot_2024-12-20_at_03 23 05](https://github.com/user-attachments/assets/be772182-41e8-4b4a-9bf3-0fa8af05d035)
    

아무것도 적용시키지 않으면    
1번 처럼 예상 포인트보다 한참 모자란 실제포인트를 획득 했다.   
이는 여러 쓰레드들이 서로 영향을 미치는 작업에 대한 수정을 했기 때문이다.    
<br>
이처럼 여러 쓰레드가 공유자원을 접근 및 수정시 발생하는 문제를    
***race condition*** 이라고 하며, 발생을 막기 위해서 반드시 동기화를    
어떻게 해야할지 생각해 보아야한다.   
<br>
처음 내가 생각했던 동기화 방식은 **synchronized** 이다.   
사용하기 쉽고 무엇보다 lock 을 획득한 스레드만   
공유자원에 접근하여 race conditon 을 방지 할 수 있을 거란 생각에 선택하였다.   
<br>

### 2.  synchronized 적용   
1. 동시에 같은 유저 여러번 요청
   
    ![Screenshot_2024-12-20_at_03 29 41](https://github.com/user-attachments/assets/85ac714e-9052-4a23-8d2f-90684c3b04e9)

1. 다른 유저 각각 요청
   
    ![Screenshot_2024-12-20_at_03 30 59](https://github.com/user-attachments/assets/5ec279fb-61b7-473f-81f9-58fb62a0732b)
    
    synchronized 를 적용하여 race condition 을 방지하여    
    1번 케이스의 경우 아무것도 설정하지 않았을 때와 달리     
    예상포인트와 실제 저장된 포인트의 값이 같다.     
    
    <br>
    하지만 synchronized 사용함으로 인해   
    성능적으로 문제가 발생했다.   

    기존에 1초 내외로 걸리던 시간이 1번케이스와 비슷한 시간을 소요하였다.   
    <br>
    이런 이유는 락을 가진 자원밖에 접근을 하지 못하여     
    동시에 접근하지 못하여 스레드가 순차적으로 작업을 수행하여 시간이 오래 걸렸다.    
    <br>

    해당 문제로 인해 멘토링에서 코치님께서 synchronized 는 좋은 방법이 아니며      
    이번 과제에 엄청 큰 힌트를 주셨다.   
    

### 3. ConcurrentHashMap

ConcurrentHashMap 을 사용해봐라고 하셨는데,   
부끄럽지만 이번 멘토링을 계기로 처음 접하게 된 내용이였다.    
<br>
동기화 관련된 api 가 존재하지 않은  HashMap 은    
멀티 스레드 환경에서 사용하기 부적합 한데,   
이를 보안하기 위해 HashMap 자체에 lock 을 거는 등의 방식을 사용해 왔다.    
<br>
HashMap 자체에 lock 을 걸면 synchronized 와 마찬가지로 성능면에서 문제가 발생한다.    
이를 해결하기 위해 여러 개의 버킷에 분산 저장하며    
버킷 마다 lock 을 거는 방식을 사용하는 concurrentMap 이 나왔다.   
<br>
여기서 비킷은 해시 버킷을 말하며   
key 값에 해시함수를 적용한 인덱스를 통해 실제 값이 저장되는 장소를 말한다.   
버킷 별로 동기화 구현이 가능해 진다는  말이다.   

![image](https://github.com/user-attachments/assets/05134570-3bcf-4eee-be1c-71bf6d475e63)

이 덕분에 서로 각각 다른 버킷에 대해서 접근한다면    
동시성을 만족 시킬 수 있다.   

### 3-1. ConcurrentHashMap + ReentrantLock   
ConcurrentHashMap 만을 사용하면 각각 다른 버킷에 대한 접근에 대해서   
동시성을 만족 시킬 수 있다.   
<br>
하지만 같은 키에 대한 수정 작업을 하거나 복잡한 연산을 수행하려고 하면   
추가적인 동기화 작업이 필요하고 이때 ReentrantLock 을 사용한다.   
<br>
ReentrantLock 의 추가적인 동기화 작업으로    
버킷 단위가 아닌 동일한 key 에 대한 동시성을 보장할 수 있기 때문이다.   

### 3-2. ConcurrentHashMap + ReentrantLock  → TEST   

1.  동시에 같은 유저 여러번 요청   
    ![Screenshot_2024-12-20_at_04 41 11](https://github.com/user-attachments/assets/f6d71188-f9c2-4bdf-bf02-f7fcd69f2b56)

1. 다른 유저 각각 요청

    ![Screenshot_2024-12-20_at_04 45 51](https://github.com/user-attachments/assets/85ebc994-90bd-4f49-8923-2fe3da8d05d9)


### 과제를 수행하면서   

많은 생각이 들었습니다.   
우선 사전 스터디에서 진행했던 tdd 를 직접 써보는 시간이였고,   
동시성에 대해서 생각하는 기회가 되었습니다.    

코치님이 말씀하신대로 팀원분들이나 사전 스터디 분들께도 물어보고 청강을    
듣거나 해서 과제를 끝낼 수 있었습니다.    
많이 미흡하다고 돌이켜보는 시간이였습니다.   

과제 제출 이후    
다른 동시성에 대해서 알아보는 시간을 가지는게   
추후에 더 도움이 될 것 같습니다.

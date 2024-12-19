package io.hhplus.tdd.Integration.currency;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.service.PointService;
import io.hhplus.tdd.unit.service_validation.ServiceValidation;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 동시성 제어를 하지 않고
 * 오직 기능만 구현
 */
public class PointServiceCCH {
    private final UserPointTable    userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final ServiceValidation serviceValidation;

    private final ConcurrentHashMap<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    public PointServiceCCH(
            UserPointTable    userPointTable,
            PointHistoryTable pointHistoryTable,
            ServiceValidation serviceValidation ){
        this.pointHistoryTable = pointHistoryTable   ;
        this.userPointTable    = userPointTable      ;
        this.serviceValidation = serviceValidation   ;
    }

    /** findUserPointByUserId
     * userid 로 userPoint 조회
     * - userid 는 정책에 맞는지 확인
     * - 이후 조회
     * @param userId
     * @return
     */
    public UserPoint findUserPointByUserId(long userId) {
        serviceValidation.isValidUserId(userId);
        return userPointTable.selectById(userId);
    }

    /** findAllPointHistoryByUserId
     * userid 로 List<PointHistory> 조회
     * - userid 는 정책에 맞는지 확인
     * - 이후 조회
     * @param userId
     * @return
     */
    public List<PointHistory> findAllPointHistoryByUserId(long userId){
        serviceValidation.isValidUserId(userId);
        return pointHistoryTable.selectAllByUserId(userId);
    }


    /** upsertPoint
     * 포인트 사용과 포인트 충전 연산의
     * 공통 부분
     * @param userid
     * @param changePoint
     * @param updatedPoints
     * @param type
     * @return
     */
    public UserPoint upsertPoint(long userid, long changePoint, long updatedPoints, TransactionType type){
        UserPoint updateUserPoint = userPointTable.insertOrUpdate(userid,updatedPoints);
        pointHistoryTable.insert(userid,changePoint, type,System.currentTimeMillis());
        return  updateUserPoint;
    }



    /** pointCharge
     * userid 와 pointToCharge 로 point 충전
     * - userId 는 정책에 맞는지
     * - pointToCharge 는 정책에 맞는지 - 도메인에 적용
     * - 이후 연산
     * @param userid
     * @param pointToCharge
     * @return
     */
    public UserPoint pointCharge(long userid, long pointToCharge){
        return executeWithLock(userid,()->{
            UserPoint userPoint         = findUserPointByUserId(userid);
            long      updatedPoints     = userPoint.charge(pointToCharge);
            return upsertPoint(userid,pointToCharge,updatedPoints,TransactionType.CHARGE);
        });
    }

    /** pointUse
     * userid 와 pointToUse 로 point 충전
     * - userId 는 정책에 맞는지
     * - pointToUse 는 정책에 맞는지 - 도메인에 적용
     * - 이후 연산
     * @param userid
     * @param pointToUse
     * @return
     */
    public UserPoint pointUse(long userid, long pointToUse){
        return executeWithLock(userid,()->{
            UserPoint userPoint       = findUserPointByUserId(userid);
            long      updatedPoints   = userPoint.use(pointToUse);
            return upsertPoint(userid,pointToUse,updatedPoints,TransactionType.USE);
        });
    }


    public  <T> T executeWithLock(long userId, PointService.Action<T> action) {
        ReentrantLock lock = userLocks.computeIfAbsent(userId, id -> new ReentrantLock());
        lock.lock();
        try {
            return action.execute();
        } finally {
            lock.unlock();
        }
    }

    @FunctionalInterface
    public interface Action<T> {
        T execute();
    }
}

package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public UserPoint getPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> getPointHistory(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public UserPoint chargePoint(long userId, long amount) {
        ReentrantLock lock = lockMap.computeIfAbsent(userId, k -> new ReentrantLock());
        lock.lock();
        try {
            UserPoint userPoint = userPointTable.selectById(userId);
            long newAmount = userPoint.point() + amount;
            UserPoint updated = userPointTable.insertOrUpdate(userId, newAmount);

            synchronized (pointHistoryTable) {
                pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, System.currentTimeMillis());
            }
            return updated;
        } finally {
            lock.unlock();
        }
    }


    public UserPoint usePoint(long userId, long amount) {
        ReentrantLock lock = lockMap.computeIfAbsent(userId, k -> new ReentrantLock());
        lock.lock();
        try {
            UserPoint userPoint = userPointTable.selectById(userId);
            if (amount > userPoint.point()) {
                throw new IllegalArgumentException("사용 포인트가 부족합니다.");
            }

            long newAmount = userPoint.point() - amount;
            UserPoint updated = userPointTable.insertOrUpdate(userId, newAmount);

            pointHistoryTable.insert(userId, amount, TransactionType.USE, System.currentTimeMillis());

            return updated;
        } finally {
            lock.unlock();
        }
    }

}

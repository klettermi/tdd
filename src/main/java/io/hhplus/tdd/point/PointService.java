package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint getPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> getPointHistory(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public UserPoint chargePoint(long userId, long amount) {
        UserPoint userPoint = userPointTable.selectById(userId);

        userPoint = userPointTable.insertOrUpdate(userPoint.id(), userPoint.point() + amount);
        pointHistoryTable.insert(userPoint.id(), amount, TransactionType.CHARGE, System.currentTimeMillis());

        return userPoint;
    }

    public UserPoint usePoint(long userId, long amount) {
        UserPoint userPoint = userPointTable.selectById(userId);

        if (amount > userPoint.point()) {
            throw new IllegalArgumentException("사용 포인트가 부족합니다.");
        }
        UserPoint updateUserPoint = userPointTable.insertOrUpdate(userPoint.id(), userPoint.point() - amount);

        pointHistoryTable.insert(userPoint.id(), amount, TransactionType.USE, System.currentTimeMillis());

        return updateUserPoint;
    }
}

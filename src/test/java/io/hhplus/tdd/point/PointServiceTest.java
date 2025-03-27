package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {
    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("포인트 조회 - 사용자가 포인트 조회를 했을 때 UserPoint를 반환")
    void getPoint_Success() {
        // given
        long userId = 1L;
        long point = 1000L;
        long updateMillis = 2000L;

        UserPoint existedUserPoint = new UserPoint(userId, point, updateMillis);
        when(userPointTable.selectById(userId)).thenReturn(existedUserPoint);

        // when
        UserPoint userPoint = pointService.getPoint(userId);

        // then
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(userPoint.point()).isEqualTo(existedUserPoint.point());
    }


    @Test
    @DisplayName("포인트 내역 조회 - 사용자가 포인트 내역을 조회했을 때 PointHistory에서 userId가 같은 값들을 반환")
    void getPointHistory_Success() {
        // given
        long userId = 1L;
        long point = 1000L;
        long updateMillis = 2000L;

        UserPoint existedUserPoint = new UserPoint(userId, point, updateMillis);

        List<PointHistory> testHistory = new ArrayList<>();
        PointHistory history1 = new PointHistory(
                1L,
                existedUserPoint.id(),
                existedUserPoint.point(),
                TransactionType.CHARGE,
                1000L
        );
        PointHistory history2 = new PointHistory(
                2L,
                existedUserPoint.id(),
                existedUserPoint.point(),
                TransactionType.CHARGE,
                1000L
        );
        testHistory.add(history1);
        testHistory.add(history2);
        when(pointHistoryTable.selectAllByUserId(1L)).thenReturn(testHistory);

        // when
        List<PointHistory> pointHistory = pointService.getPointHistory(1L);

        // then
        assertThat(pointHistory.size()).isEqualTo(2);
        assertThat(pointHistory.get(0)).isEqualTo(history1);
        assertThat(pointHistory.get(1)).isEqualTo(history2);
    }

    @Test
    @DisplayName("포인트 충전 - 사용자가 포인트를 충전 시 UserPoint에 충전된 금액으로 수정해준 후, PointHistory에 저장")
    void chargePoint_Success() {
        // given
        long id = 1L;
        long userId = 1L;
        long point = 1000L;
        long amount = 1000L;
        TransactionType type = TransactionType.CHARGE;

        UserPoint existedUserPoint = new UserPoint(userId, point, System.currentTimeMillis());
        PointHistory samplePointHistory = new PointHistory(id, userId, amount, type, System.currentTimeMillis());
        UserPoint updatedUserPoint = new UserPoint(userId, point + amount, System.currentTimeMillis());

        when(userPointTable.selectById(eq(userId))).thenReturn(existedUserPoint);
        when(pointHistoryTable.insert(eq(userId), eq(amount), eq(type), anyLong()))
                .thenReturn(samplePointHistory);
        when(userPointTable.insertOrUpdate(eq(userId), eq(point + amount)))
                .thenReturn(updatedUserPoint);
        // when
        UserPoint userPoint = pointService.chargePoint(userId, 1000L);

        // then
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(userPoint.point()).isEqualTo(amount + point);
    }

    @Test
    @DisplayName("포인트 사용 - 사용자가 포인트를 사용할 때 UserPoint에 사용된 금액으로 수정하고, PointHistory에 내역을 저장")
    void usePoint_Success() {
        // given
        long id = 1L;
        long userId = 1L;
        long point = 2000L;
        long amount = 1000L;
        TransactionType type = TransactionType.USE;

        UserPoint existedUserPoint = new UserPoint(userId, point, System.currentTimeMillis());
        PointHistory samplePointHistory = new PointHistory(id, userId, amount, type, System.currentTimeMillis());

        when(userPointTable.selectById(eq(userId))).thenReturn(existedUserPoint);
        when(pointHistoryTable.insert(eq(userId), eq(amount), eq(type), anyLong()))
                .thenReturn(samplePointHistory);
        // when
        UserPoint userPoint = pointService.usePoint(userId, 1000L);

        // then
        verify(userPointTable).insertOrUpdate(eq(userId), eq(point - amount));
    }

    @Test
    @DisplayName("포인트 사용 - 사용자가 포인트를 초과하여 사용할 때 IllegalArgumentException을 throw")
    void usePoint_InsufficientBalance_Fail() {
        // given
        long id = 1L;
        long userId = 1L;
        long point = 500L;
        long amount = 1000L;

        UserPoint existedUserPoint = new UserPoint(userId, point, System.currentTimeMillis());

        when(userPointTable.selectById(eq(userId))).thenReturn(existedUserPoint);

        // when & then
        assertThatThrownBy(() -> pointService.usePoint(userId, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용 포인트가 부족합니다.");
    }
}
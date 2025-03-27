package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class PointServiceIntegrationTest {

    @Autowired
    PointService pointService;

    @Autowired
    UserPointTable userPointTable;

    @Autowired
    PointHistoryTable pointHistoryTable;

    final long userId = 1L;

    @BeforeEach
    void setUp() {
        userPointTable.clear();
        pointHistoryTable.clear();
        userPointTable.insertOrUpdate(userId, 0L);
    }

    @Test
    @DisplayName("포인트 조회")
    void getPointTest() {
        userPointTable.insertOrUpdate(userId, 500L);

        UserPoint userPoint = pointService.getPoint(userId);

        assertThat(userPoint).isNotNull();
        assertThat(userPoint.point()).isEqualTo(500L);
    }

    @Test
    @DisplayName("포인트 충전 - 1000포인트 충전")
    void chargePointTest() {
        UserPoint charged = pointService.chargePoint(userId, 1000L);

        assertThat(charged.point()).isEqualTo(1000L);

        List<PointHistory> history = pointService.getPointHistory(userId);
        assertThat(history).hasSize(1);
        assertThat(history.get(0).type()).isEqualTo(TransactionType.CHARGE);
    }

    @Test
    @DisplayName("포인트 사용 - 2000포인트 충전 후 800포인트 사용")
    void usePointTest() {
        pointService.chargePoint(userId, 2000L);

        UserPoint updated = pointService.usePoint(userId, 800L);

        assertThat(updated.point()).isEqualTo(1200L);

        List<PointHistory> history = pointService.getPointHistory(userId);
        assertThat(history).hasSize(2);
        assertThat(history.get(1).type()).isEqualTo(TransactionType.USE);
    }

    @Test
    @DisplayName("포인트 사용 실패 - 0포인트 중 500 포인트 사용 시 IllegalArgumentException")
    void usePointFailTest() {
        assertThatThrownBy(() -> pointService.usePoint(userId, 500L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용 포인트가 부족합니다.");
    }
}

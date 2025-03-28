package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointServiceConcurrencyIntegrationTest {

    @Autowired
    PointService pointService;

    @Autowired
    UserPointTable userPointTable;


    long chargeUserId = 1L;
    long useUserId = 2L;

    @BeforeEach
    void setUp() {
        userPointTable.insertOrUpdate(chargeUserId, 0L);
        userPointTable.insertOrUpdate(useUserId, 10_000L);
    }


    @Test
    @DisplayName("100번씩 1000포인트 충전 시 100_000포인트 충전")
    void chargePoint_Concurrent() throws InterruptedException {
        int threadCount = 100;
        long amount = 1000L;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    pointService.chargePoint(chargeUserId, amount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        UserPoint userPoint = pointService.getPoint(chargeUserId);
        assertThat(userPoint.point()).isEqualTo(threadCount * amount);

        List<PointHistory> historyList = pointService.getPointHistory(chargeUserId);
        assertThat(historyList).hasSize(threadCount);
    }

    @Test
    @DisplayName("100번씩 100포인트 사용 시 10_000")
    void usePoint_Concurrent() throws InterruptedException {
        int threadCount = 100;
        long amount = 100L;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    pointService.usePoint(useUserId, amount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        UserPoint userPoint = pointService.getPoint(useUserId);
        assertThat(userPoint.point()).isEqualTo(0L);

        List<PointHistory> historyList = pointService.getPointHistory(useUserId);
        assertThat(historyList).hasSize(threadCount);
    }
}

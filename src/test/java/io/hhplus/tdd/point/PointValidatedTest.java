package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointValidatedTest {
    @Test
    @DisplayName("포인트를 충전 시 validate에 성공한다.")
    public void validPoint_chargeType_Success() {
        // given
        long id = 1L;
        long userId = 1L;
        long amount = 1000L;
        TransactionType chargeType = TransactionType.CHARGE;
        long updateMillis = 1000L;

        // when
        PointHistory pointHistory = new PointHistory(id, userId, amount, chargeType, updateMillis);

        // then
        assertThat(pointHistory.id()).isEqualTo(id);
        assertThat(pointHistory.userId()).isEqualTo(userId);
        assertThat(pointHistory.amount()).isEqualTo(amount);
        assertThat(pointHistory.type()).isEqualTo(chargeType);
        assertThat(pointHistory.updateMillis()).isEqualTo(updateMillis);
    }

    @Test
    @DisplayName("포인트 충전 시 최소 포인트인 100포인트를 넘지 못한다.")
    public void validPoint_chargeType_underThousand_Fail() {
        // given
        long id = 1L;
        long userId = 1L;
        long amount = 999L;
        TransactionType chargeType = TransactionType.CHARGE;
        long updateMillis = 1000L;

        // when & then
        assertThatThrownBy(() ->  new PointHistory(id, userId, amount, chargeType, updateMillis))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 금액은 1000 포인트 이상이어야 합니다.");
    }

    @Test
    @DisplayName("포인트 충전 시 1일 포인트 한도인 10만을 넘는다.")
    public void validPoint_chargeType_DayMaxPoint_Fail() {
        // given
        long id = 1L;
        long userId = 1L;
        long amount = 100001L;
        TransactionType chargeType = TransactionType.CHARGE;
        long updateMillis = 1000L;

        // when & then
        assertThatThrownBy(() ->  new PointHistory(id, userId, amount, chargeType, updateMillis))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1일 충전 금액은 최대 10만 포인트입니다.");
    }

    @Test
    @DisplayName("포인트 사용 시 validate에 성공한다.")
    public void validPoint_useType_Success() {
        // given
        long id = 1L;
        long userId = 1L;
        long amount = 1000L;
        TransactionType useType = TransactionType.USE;
        long updateMillis = 1000L;

        // when
        PointHistory pointHistory = new PointHistory(id, userId, amount, useType, updateMillis);

        // then
        assertThat(pointHistory.id()).isEqualTo(id);
        assertThat(pointHistory.userId()).isEqualTo(userId);
        assertThat(pointHistory.amount()).isEqualTo(amount);
        assertThat(pointHistory.type()).isEqualTo(useType);
        assertThat(pointHistory.updateMillis()).isEqualTo(updateMillis);
    }

    @Test
    @DisplayName("포인트를 사용 시 최소 사용 포인트인 100 포인트를 넘지 않는다.")
    public void validPoint_useType_underHundred_Fail() {
        // given
        long id = 1L;
        long userId = 1L;
        long amount = 90L;
        TransactionType useType = TransactionType.USE;
        long updateMillis = 1000L;

        // when & then
        assertThatThrownBy(() ->  new PointHistory(id, userId, amount, useType, updateMillis))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용 금액은 1000 포인트 이상이어야 합니다.");
    }
}

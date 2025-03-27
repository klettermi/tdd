package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PointValidationServiceTest {

    PointValidationService validator = new PointValidationService();

    @Test
    @DisplayName("충전 금액이 1000 이상이면 성공")
    void validateChargeAmount_Success() {
        assertThatCode(() -> validator.validate(1000L, TransactionType.CHARGE))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("충전 금액이 1000 미만면 IllegalArgumentException 발생")
    void validateChargeAmountTooLow() {
        assertThatThrownBy(() -> validator.validate(999L, TransactionType.CHARGE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1000 포인트 이상");
    }

    @Test
    @DisplayName("충전 금액이 100000 초과면 예외 발생")
    void validateChargeAmountTooHigh() {
        assertThatThrownBy(() -> validator.validate(100_001L, TransactionType.CHARGE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10만 포인트");
    }

    @Test
    @DisplayName("사용 금액이 100 이상이면 성공")
    void validateUseAmount_Success() {
        assertThatCode(() -> validator.validate(100L, TransactionType.USE))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("사용 금액이 100 미만면 IllegalArgumentException 발생")
    void validateUseAmountTooLow() {
        assertThatThrownBy(() -> validator.validate(99L, TransactionType.USE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("100 포인트 이상");
    }

    @Test
    @DisplayName("유효하지 않은 타입이면 IllegalArgumentException 발생")
    void validateInvalidType() {
        TransactionType invalidType = null;

        assertThatThrownBy(() -> validator.validate(1000L, invalidType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 거래 타입");
    }
}

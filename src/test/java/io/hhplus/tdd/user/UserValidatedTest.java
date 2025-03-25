package io.hhplus.tdd.user;

import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserValidatedTest {

    @Test
    @DisplayName("사용자 포인트 validate에 성공한다.")
    void userValidated_Success() {
        // given
        long userId = 1L;
        long pont = 1000L;
        long updateMillis = 2000L;

        // when
        UserPoint userPoint =  new UserPoint(userId, pont, updateMillis);

        // then
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(userPoint.point()).isEqualTo(pont);
        assertThat(userPoint.updateMillis()).isEqualTo(updateMillis);
    }

    @Test
    @DisplayName("사용자의 id가 0 이하입니다.")
    void userValidated_Fail() {
        // given
        long userId = -1L;
        long pont = 1000L;
        long updateMillis = 2000L;

        assertThatThrownBy(() -> new UserPoint(userId, pont, updateMillis))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자 id가 유효하지 않습니다.");
    }

    @Test
    @DisplayName("사용자 최대 포인트를 초과하였습니다.")
    void userValidated_overMaxPoint_Fail() {
        // given
        long userId = 1L;
        long pont = 100000000L;
        long updateMillis = 2000L;

        assertThatThrownBy(() -> new UserPoint(userId, pont, updateMillis))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최대 포인트 금액 넘었습니다.");
    }

}

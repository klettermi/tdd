package io.hhplus.tdd.point;

import java.nio.file.AccessDeniedException;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public UserPoint {
        if (id < 1) {
            throw new IllegalArgumentException("사용자 id가 유효하지 않습니다.");
        }
        if (point >= 100000000) {
            throw new IllegalArgumentException("최대 포인트 금액 넘었습니다.");
        }
    }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

}

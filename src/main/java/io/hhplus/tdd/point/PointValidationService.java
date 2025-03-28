package io.hhplus.tdd.point;

import org.springframework.stereotype.Component;

@Component
public class PointValidationService {

    public void validate(long amount, TransactionType type) {
        if (type == TransactionType.CHARGE) {
            if (amount < 1000) {
                throw new IllegalArgumentException("충전 금액은 1000 포인트 이상이어야 합니다.");
            }
            if (amount > 100_000) {
                throw new IllegalArgumentException("1일 충전 금액은 최대 10만 포인트입니다.");
            }
        } else if (type == TransactionType.USE) {
            if (amount < 100) {
                throw new IllegalArgumentException("사용 금액은 100 포인트 이상이어야 합니다.");
            }
        } else {
            throw new IllegalArgumentException("유효하지 않은 거래 타입입니다.");
        }
    }
}

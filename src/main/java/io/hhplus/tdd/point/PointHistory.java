package io.hhplus.tdd.point;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
    public PointHistory {
        if (type.equals(TransactionType.CHARGE)) {
            if (amount < 1000) {
                throw new IllegalArgumentException("충전 금액은 1000 포인트 이상이어야 합니다.");
            }
            if (amount > 100000) {
                throw new IllegalArgumentException("1일 충전 금액은 최대 10만 포인트입니다.");
            }

        } else if (type.equals(TransactionType.USE)) {
            if (amount < 100) {
                throw new IllegalArgumentException("사용 금액은 1000 포인트 이상이어야 합니다.");
            }
        }

    }
}

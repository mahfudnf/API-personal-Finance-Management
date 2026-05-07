package com.personalfinance.management.constant;

import lombok.Getter;

@Getter
public enum MonthValue {
    JANUARI(1),
    FEBRUARI(2),
    MARET(3),
    APRIL(4),
    MEI(5),
    JUNI(6),
    JULI(7),
    AGUSTUS(8),
    SEPTEMBER(9),
    OKTOBER(10),
    NOVEMBER(11),
    DESEMBER(12);

    private final int value;

    MonthValue(int value) {
        this.value = value;
    }

    public static MonthValue fromInt(Integer value) {
        if (value == null) return null;

        for (MonthValue month : MonthValue.values()) {
            if (month.getValue() == value) {
                return month;
            }
        }
        throw new IllegalArgumentException("Invalid month: " + value);
    }
}

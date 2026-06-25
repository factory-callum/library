package io.pillopl.library.lending.patron.model;

import lombok.Value;

@Value
public class DaysOverdue {

    int days;

    private DaysOverdue(int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Days overdue cannot be negative");
        }
        this.days = days;
    }

    public static DaysOverdue of(int days) {
        return new DaysOverdue(days);
    }

    public static DaysOverdue none() {
        return new DaysOverdue(0);
    }

    boolean isPositive() {
        return days > 0;
    }
}

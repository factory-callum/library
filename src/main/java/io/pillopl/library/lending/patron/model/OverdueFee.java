package io.pillopl.library.lending.patron.model;

import lombok.Value;

@Value
public class OverdueFee {

    double amount;

    private OverdueFee(double amount) {
        this.amount = amount;
    }

    public static OverdueFee of(double amount) {
        return new OverdueFee(amount);
    }

    public static OverdueFee zero() {
        return new OverdueFee(0.0);
    }

    public OverdueFee plus(OverdueFee other) {
        return new OverdueFee(this.amount + other.amount);
    }

    public OverdueFee cappedAt(double cap) {
        if (amount > cap) {
            return new OverdueFee(cap);
        }
        return this;
    }
}

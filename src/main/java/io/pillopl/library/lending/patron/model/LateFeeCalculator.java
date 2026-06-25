package io.pillopl.library.lending.patron.model;

import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class LateFeeCalculator {

    public OverdueFee calculate(@NonNull DaysOverdue daysOverdue, @NonNull PatronType patronType) {
        return LateFeePolicy.maxFeeCapPolicy.apply(daysOverdue, patronType);
    }
}

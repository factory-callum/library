package io.pillopl.library.lending.patron.model;

import io.vavr.Function2;
import io.vavr.collection.List;

interface LateFeePolicy extends Function2<DaysOverdue, PatronType, OverdueFee> {

    double BASE_FEE_PER_DAY = 0.25;
    double RESEARCHER_DISCOUNT_RATE = 0.5;
    double MAX_FEE_CAP = 30.0;

    LateFeePolicy baseFeePerDayPolicy = (DaysOverdue daysOverdue, PatronType patronType) -> {
        if (!daysOverdue.isPositive()) {
            return OverdueFee.zero();
        }
        return OverdueFee.of(daysOverdue.getDays() * BASE_FEE_PER_DAY);
    };

    LateFeePolicy researcherDiscountPolicy = (DaysOverdue daysOverdue, PatronType patronType) -> {
        OverdueFee base = baseFeePerDayPolicy.apply(daysOverdue, patronType);
        if (patronType.equals(PatronType.Researcher)) {
            return OverdueFee.of(base.getAmount() * RESEARCHER_DISCOUNT_RATE);
        }
        return base;
    };

    LateFeePolicy maxFeeCapPolicy = (DaysOverdue daysOverdue, PatronType patronType) ->
            researcherDiscountPolicy.apply(daysOverdue, patronType).cappedAt(MAX_FEE_CAP);

    static List<LateFeePolicy> allCurrentPolicies() {
        return List.of(
                baseFeePerDayPolicy,
                researcherDiscountPolicy,
                maxFeeCapPolicy);
    }
}

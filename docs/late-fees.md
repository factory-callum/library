# Late Fees

During the Big Picture EventStorming we deferred the **Fees application** process that starts
when an overdue book is returned (see [Big Picture EventStorming](big-picture.md)). This page
describes the part of that process that is already modelled: calculating the **late fee** owed
for an overdue checkout.

## Domain concept

A book can be checked out for up to 60 days. Once that period passes, the checkout becomes
**overdue** and the patron owes a **late fee** that grows with the number of overdue days.
The fee depends on two things only:

* how many days the checkout is overdue (`DaysOverdue`),
* what type of patron holds it (`PatronType` - `Regular` or `Researcher`).

The calculation is implemented as immutable value objects and pure policy functions, in line
with the [functional thinking](../README.md#functional-thinking) used across the lending context.

## Value objects

* `DaysOverdue` - the number of overdue days. It can never be negative; `DaysOverdue.none()`
  represents a checkout that is not overdue.
* `OverdueFee` - the monetary amount owed. It exposes `OverdueFee.zero()`, addition via `plus`,
  and `cappedAt` to enforce an upper bound.

## Policies

Late fees are computed by chaining three pure policy functions
(`LateFeePolicy extends Function2<DaysOverdue, PatronType, OverdueFee>`). Each policy builds on
the previous one:

* **Base fee per day** - a checkout that is not overdue owes nothing; otherwise the patron is
  charged a flat fee for every overdue day.
  ```java
  LateFeePolicy baseFeePerDayPolicy = (DaysOverdue daysOverdue, PatronType patronType) -> {
      if (!daysOverdue.isPositive()) {
          return OverdueFee.zero();
      }
      return OverdueFee.of(daysOverdue.getDays() * BASE_FEE_PER_DAY);
  };
  ```

* **Researcher discount** - a `Researcher` patron pays a reduced share of the base fee, while a
  `Regular` patron pays the full base fee.
  ```java
  LateFeePolicy researcherDiscountPolicy = (DaysOverdue daysOverdue, PatronType patronType) -> {
      OverdueFee base = baseFeePerDayPolicy.apply(daysOverdue, patronType);
      if (patronType.equals(PatronType.Researcher)) {
          return OverdueFee.of(base.getAmount() * RESEARCHER_DISCOUNT_RATE);
      }
      return base;
  };
  ```

* **Maximum fee cap** - the final fee is capped, so no single overdue checkout can grow without
  bound. This is the policy applied by the calculator.
  ```java
  LateFeePolicy maxFeeCapPolicy = (DaysOverdue daysOverdue, PatronType patronType) ->
          researcherDiscountPolicy.apply(daysOverdue, patronType).cappedAt(MAX_FEE_CAP);
  ```

### Constants

| Constant                  | Value | Meaning                                                       |
|---------------------------|-------|---------------------------------------------------------------|
| `BASE_FEE_PER_DAY`        | 0.25  | Amount charged for each overdue day.                          |
| `RESEARCHER_DISCOUNT_RATE`| 0.5   | Fraction of the base fee paid by a `Researcher` patron.       |
| `MAX_FEE_CAP`             | 30.0  | Upper bound on the fee for a single overdue checkout.         |

For example, a `Regular` patron with a checkout 10 days overdue owes `10 * 0.25 = 2.5`, while a
patron with no overdue days owes `0.0`.

## Entry points

* `LateFeeCalculator.calculate(DaysOverdue, PatronType)` - a Spring `@Component` that applies the
  capped policy.
* `Patron.lateFeeFor(DaysOverdue)` - lets the `Patron` aggregate compute the fee for its own
  patron type.

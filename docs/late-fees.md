# Late fees for overdue checkouts

A book can be checked out for up to 60 days. When a patron returns a checkout after
that period, the lending context calculates a **late fee** based on how many days the
checkout is overdue and the type of patron.

Late fees are modelled the same way as the other lending rules: as immutable value
objects combined through composable policy functions, so the calculation stays pure,
unit-testable, and free of framework dependencies.

## Domain concepts

| Concept | Type | Responsibility |
| --- | --- | --- |
| `DaysOverdue` | value object | Non-negative number of days a checkout is past due. Rejects negative values. |
| `OverdueFee` | value object | A monetary fee amount. Supports `plus` and `cappedAt`. |
| `LateFeePolicy` | policy function | `(DaysOverdue, PatronType) -> OverdueFee`. Composed from smaller policies. |
| `LateFeeCalculator` | Spring component | Application entry point that applies the current policy. |

## Calculation rules

The fee is derived by composing three policies, each building on the previous one:

1. **Base fee per day** - every overdue day costs `$0.25`. A checkout that is not
   overdue owes nothing.
2. **Researcher discount** - `Researcher` patrons pay 50% of the base fee. `Regular`
   patrons pay the full amount.
3. **Maximum fee cap** - the final fee is capped at `$30.00`, regardless of how many
   days a checkout is overdue.

```java
double BASE_FEE_PER_DAY = 0.25;
double RESEARCHER_DISCOUNT_RATE = 0.5;
double MAX_FEE_CAP = 30.0;
```

### Worked examples

| Patron type | Days overdue | Fee |
| --- | --- | --- |
| Regular | 0 | `$0.00` |
| Regular | 10 | `$2.50` |
| Researcher | 10 | `$1.25` |
| Regular | 200 | `$30.00` (capped) |

## Usage

A fee can be calculated either through the `Patron` aggregate or directly via the
`LateFeeCalculator` component:

```java
OverdueFee fee = patron.lateFeeFor(DaysOverdue.of(10));
```

```java
OverdueFee fee = lateFeeCalculator.calculate(DaysOverdue.of(10), PatronType.Regular);
```

Both paths apply `LateFeePolicy.maxFeeCapPolicy`, so the base fee, researcher discount,
and maximum cap are always enforced together.

# Documentation Update Summary

Generated automatically after a code change was merged. This summary explains what the
documentation bot changed and why, so it can be reviewed alongside the code during
code / release review.

## What changed in the code

A new **late-fee calculation** capability was added to the `lending` bounded context,
under `src/main/java/io/pillopl/library/lending/patron/model/`:

| File | Purpose |
| --- | --- |
| `DaysOverdue.java` | New value object for a non-negative count of overdue days. |
| `OverdueFee.java` | New value object for a fee amount (`plus`, `cappedAt`). |
| `LateFeePolicy.java` | New policy interface composing base fee, researcher discount, and max cap. |
| `LateFeeCalculator.java` | New Spring component exposing `calculate(...)`. |
| `Patron.java` | Added `lateFeeFor(DaysOverdue)` to the aggregate. |
| `LateFeeCalculatorTest.groovy` | New Spock spec covering base fee and zero-fee cases. |

These introduce **new business rules** not previously documented: a base fee of
`$0.25`/overdue day, a 50% discount for `Researcher` patrons, and a `$30.00` cap.

## Step-by-step reasoning

1. **Explored the repo** and classified it as a Java / Spring DDD modular monolith
   where domain rules live as value objects + policy functions, with long-form docs in
   `README.md` and `docs/`.
2. **Read each changed file** and identified the change as a new, self-contained domain
   concept (late fees) rather than a modification of an existing rule.
3. **Searched the docs** for coverage of overdue checkouts. The README domain
   description mentioned the 60-day checkout limit and overdue-checkout daily sheets,
   but contained **no mention of fees** - a documentation gap.
4. **Decided** to (a) create a focused doc page for the new concept and (b) update the
   README domain description so it stays accurate, rather than scattering details.

## Documentation files updated

| File | Change |
| --- | --- |
| `docs/late-fees.md` | **Created.** Documents the domain concepts, the three composed policies, worked examples, and usage via `Patron` / `LateFeeCalculator`. |
| `README.md` | **Updated.** Added one sentence to the domain description summarizing the late-fee rule and linking to `docs/late-fees.md`. |

## Suggested human review

- Confirm the constants (`$0.25`/day, 50% researcher discount, `$30.00` cap) are the
  intended product values and not placeholders.
- Confirm whether late fees should also surface in the patron profile / daily sheet
  documentation once a read model is added.

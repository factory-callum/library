# Documentation Update Summary

## Documentation files created or updated

* **Created** `docs/late-fees.md` - a focused page describing the new late-fee calculation concept.
* **Updated** `README.md` - extended the domain description and added a link to the new page.
* **Updated** `docs/design-level.md` - added a late-fee reference in the "Registering overdue
  checkout" section.

## What changed in the code

The following files were merged to `master` and introduce a brand-new, self-contained domain
concept: calculating the **late fee** owed for an overdue checkout. This realizes the previously
deferred "Fees application" process noted in `docs/big-picture.md`.

* `DaysOverdue.java` - new immutable value object holding the number of overdue days. It rejects
  negative values and offers `of(int)` and `none()` (zero days), plus an `isPositive()` check.
* `OverdueFee.java` - new immutable value object for the monetary amount owed. Offers `of(double)`,
  `zero()`, `plus(OverdueFee)` and `cappedAt(double)`.
* `LateFeePolicy.java` - new functional interface (`Function2<DaysOverdue, PatronType, OverdueFee>`)
  defining three chained policies and the constants that drive them:
  * `BASE_FEE_PER_DAY = 0.25` - charged per overdue day (`baseFeePerDayPolicy`).
  * `RESEARCHER_DISCOUNT_RATE = 0.5` - researcher patrons pay half the base fee
    (`researcherDiscountPolicy`).
  * `MAX_FEE_CAP = 30.0` - upper bound on a single fee (`maxFeeCapPolicy`, the one applied).
* `LateFeeCalculator.java` - new Spring `@Component` exposing
  `calculate(DaysOverdue, PatronType)`, which applies `maxFeeCapPolicy`.
* `Patron.java` - added `lateFeeFor(DaysOverdue)` so the aggregate can compute the fee for its own
  patron type.
* `LateFeeCalculatorTest.groovy` - new Spock spec verifying that a regular patron pays
  `10 * 0.25 = 2.5` for 10 overdue days and that a patron with no overdue days pays `0.0`.

## Why each doc edit was made

* **`docs/late-fees.md`**: the late-fee calculation is a new, self-contained concept (new value
  objects, new policy functions, new constants/invariants), so per the documentation guidelines it
  warranted a dedicated page. The page documents the value objects, the three policies, the
  constants table (with the exact values from `LateFeePolicy`), a worked example matching the test,
  and the two entry points (`LateFeeCalculator`, `Patron.lateFeeFor`).
* **`README.md` domain description**: the existing description covered the 60-day checkout limit and
  overdue checkouts but said nothing about fees. A sentence was added so the domain narrative stays
  accurate, with a link to the new page.
* **`README.md` docs link list**: added the new page alongside the other long-form docs so it is
  discoverable.
* **`docs/design-level.md`**: the "Registering overdue checkout" section is the closest existing
  domain narrative; a short paragraph now points readers to the late-fee details.

## Sections that may need human review or clarification

* The "Fees application" process in `docs/big-picture.md` was intentionally deferred during
  EventStorming. Only the **fee calculation** is implemented so far (not collection, payment, or
  the full return-triggered application). The new page is scoped to calculation only; reviewers may
  want to expand `big-picture.md` once the rest of that process lands.
* `Patron.lateFeeFor` exists on the aggregate but no command/event flow currently invokes it (it is
  not wired into checkout return). Reviewers should confirm whether documentation should describe an
  end-to-end flow or remain at the policy level as it is now.
* No EventStorming diagram exists for late fees, so the new page is text-only (no image). A diagram
  could be added later for visual consistency with the other docs.

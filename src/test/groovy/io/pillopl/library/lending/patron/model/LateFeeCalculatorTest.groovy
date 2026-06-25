package io.pillopl.library.lending.patron.model

import spock.lang.Specification

class LateFeeCalculatorTest extends Specification {

    LateFeeCalculator calculator = new LateFeeCalculator()

    def 'regular patron is charged base fee per day overdue'() {
        given:
            DaysOverdue tenDaysOverdue = DaysOverdue.of(10)
        when:
            OverdueFee fee = calculator.calculate(tenDaysOverdue, PatronType.Regular)
        then:
            fee.amount == 2.5
    }

    def 'patron with no overdue days owes nothing'() {
        when:
            OverdueFee fee = calculator.calculate(DaysOverdue.none(), PatronType.Regular)
        then:
            fee.amount == 0.0
    }
}

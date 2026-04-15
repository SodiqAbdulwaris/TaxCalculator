package com.example.taxcalculator

import org.junit.Assert.assertEquals
import org.junit.Test

class TaxCalculatorTests {

    @Test
    fun noTaxForIncomeInsideTaxFreeBand() {
        val result = calculateTax(800_000.0)

        assertEquals(0.0, result.annualTax, 0.0)
    }

    @Test
    fun calculatesTaxForMiddleIncome() {
        val result = calculateTax(5_000_000.0)

        assertEquals(690_000.0, result.annualTax, 0.0)
    }

    @Test
    fun calculatesTaxForIncomeAboveFiftyMillion() {
        val result = calculateTax(60_000_000.0)

        assertEquals(12_930_000.0, result.annualTax, 0.0)
    }
}

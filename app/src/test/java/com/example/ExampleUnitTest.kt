package com.example

import com.example.data.TradingAccount
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testRealMicroAccountCalculation() {
    val realMicro = TradingAccount(
      name = "REAL-MICRO",
      accountType = "MICRO",
      amount = 30.0,
      totalDrawdown = 12.0,
      pointReference = 1.0,
      lotReference = 0.1,
      slReference = 0.1
    )

    // 1% of drawdown = 30 / 12 = 2.5
    assertEquals(2.5, realMicro.getOnePercentDrawdown(), 0.001)

    // Risk 3% -> Total risk = 2.5 * 3 = 7.5
    assertEquals(7.5, realMicro.calculateTotalRisk(3.0), 0.001)

    // Base ratio for 10 SL points = 7.5 / 10 = 0.75
    assertEquals(0.75, realMicro.calculateBaseLotRatio(3.0, 10.0), 0.001)

    // Calibrated Lot Size = 0.75 * (0.1 / (0.1 * 1)) = 0.75 lots
    assertEquals(0.75, realMicro.calculateLotSize(3.0, 10.0), 0.001)
  }

  @Test
  fun testPropAccountCalculation() {
    val prop = TradingAccount(
      name = "PROP",
      accountType = "PROP",
      amount = 600.0,
      totalDrawdown = 12.0,
      pointReference = 1.0,
      lotReference = 0.01,
      slReference = 1.0
    )

    // 1% of drawdown = 600 / 12 = 50
    assertEquals(50.0, prop.getOnePercentDrawdown(), 0.001)

    // Risk 3% -> Total risk = 50 * 3 = 150
    assertEquals(150.0, prop.calculateTotalRisk(3.0), 0.001)

    // Base ratio for 10 SL points = 150 / 10 = 15.00
    assertEquals(15.0, prop.calculateBaseLotRatio(3.0, 10.0), 0.001)

    // Calibrated Lot Size = 15.00 * (0.01 / (1 * 1)) = 0.15 lots
    assertEquals(0.15, prop.calculateLotSize(3.0, 10.0), 0.001)
  }
}


package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.TradingAccount
import com.example.ui.CalculationResult
import com.example.ui.components.CalculationResultCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleAccount = TradingAccount(
      name = "PROP",
      accountType = "PROP",
      amount = 600.0,
      totalDrawdown = 12.0,
      pointReference = 1.0,
      lotReference = 0.01,
      slReference = 1.0
    )
    val sampleResult = CalculationResult(
      onePercentDrawdown = 50.0,
      totalRisk = 150.0,
      baseLotRatio = 15.0,
      calibratedLotSize = 0.15,
      riskPerPoint = 15.0,
      isValid = true
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        CalculationResultCard(
          account = sampleAccount,
          result = sampleResult,
          riskPercent = "3",
          slPoints = "10",
          onCopyFeedback = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}


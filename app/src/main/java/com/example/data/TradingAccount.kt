package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.round

@Entity(tableName = "trading_accounts")
data class TradingAccount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String, // e.g. "REAL-MICRO", "PROP", "Prop Firm 2", "Real 1"
    val accountType: String = "REAL", // "REAL", "PROP", "MICRO", "STANDARD"
    val amount: Double, // The Amount size (e.g. 30 for Real-Micro, 600 for Prop)
    val totalDrawdown: Double, // Total Drawdown value (e.g. 12)
    val isCustomOnePercent: Boolean = false,
    val customOnePercentValue: Double? = null,
    val pointReference: Double = 1.0, // Point (for reference) e.g. 1.0
    val lotReference: Double = 0.1, // lot (for reference) e.g. 0.1 for Micro, 0.01 for Prop
    val slReference: Double = 0.1, // SL (for reference) e.g. 0.1 for Micro, 1.0 for Prop
    val currencySymbol: String = "$",
    val defaultRiskPercent: Double = 3.0,
    val defaultSlPoints: Double = 10.0,
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * 1% of the drawdown calculation:
     * In the spreadsheet:
     * REAL-MICRO: Amount 30, Drawdown 12 => 30 / 12 = 2.5
     * PROP: Amount 600, Drawdown 12 => 600 / 12 = 50.0
     */
    fun getOnePercentDrawdown(): Double {
        if (isCustomOnePercent && customOnePercentValue != null && customOnePercentValue > 0) {
            return customOnePercentValue
        }
        if (totalDrawdown > 0) {
            return amount / totalDrawdown
        }
        return amount * 0.01
    }

    /**
     * Total risk as per RISK %:
     * Formula: (1% of drawdown) * (risk % want to take)
     * e.g. 2.5 * 3 = 7.5 (Micro)
     * e.g. 50 * 3 = 150 (Prop)
     */
    fun calculateTotalRisk(riskPercent: Double): Double {
        return getOnePercentDrawdown() * riskPercent
    }

    /**
     * Base lot ratio:
     * Formula: Total Risk / SL points
     * e.g. 7.5 / 10 = 0.75 (Micro)
     * e.g. 150 / 10 = 15.00 (Prop)
     */
    fun calculateBaseLotRatio(riskPercent: Double, slPoints: Double): Double {
        if (slPoints <= 0) return 0.0
        val totalRisk = calculateTotalRisk(riskPercent)
        return totalRisk / slPoints
    }

    /**
     * Calculated Final Calibrated LOT SIZE:
     * Formula: (Total Risk / SL points) * (lotReference / (slReference * pointReference))
     * e.g. REAL-MICRO: (7.5 / 10) * (0.1 / (0.1 * 1)) = 0.75 lots
     * e.g. PROP: (150 / 10) * (0.01 / (1 * 1)) = 15.00 * 0.01 = 0.15 lots
     */
    fun calculateLotSize(riskPercent: Double, slPoints: Double): Double {
        if (slPoints <= 0) return 0.0
        val baseRatio = calculateBaseLotRatio(riskPercent, slPoints)
        val denominator = slReference * pointReference
        if (denominator <= 0) return baseRatio
        return baseRatio * (lotReference / denominator)
    }

    /**
     * Helper to format numbers nicely (e.g. 0.75, 15.00, 0.15)
     */
    companion object {
        private val lotFormat = DecimalFormat("0.00", DecimalFormatSymbols(Locale.US))
        private val currencyFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))
        private val compactFormat = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.US))

        fun formatLot(lot: Double): String = lotFormat.format(lot)
        fun formatCurrency(amount: Double, symbol: String = "$"): String = "$symbol${currencyFormat.format(amount)}"
        fun formatCompact(value: Double): String = compactFormat.format(value)
    }
}

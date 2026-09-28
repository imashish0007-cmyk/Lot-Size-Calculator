package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TradingAccount
import com.example.ui.CalculationResult
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderStrong
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceHighlight
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantEmerald
import com.example.ui.theme.ElegantIceBlue
import com.example.ui.theme.ElegantIceBlueOn
import com.example.ui.theme.ElegantTextLight
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CalculationResultCard(
    account: TradingAccount,
    result: CalculationResult,
    riskPercent: String,
    slPoints: String,
    onCopyFeedback: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }

    val formattedLot = TradingAccount.formatLot(result.calibratedLotSize)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = ElegantDarkSurfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            ElegantDarkBorderSubtle
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Label
            Text(
                text = "CALCULATED LOT SIZE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    letterSpacing = 2.sp
                ),
                fontWeight = FontWeight.Bold,
                color = ElegantTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Hero Lot Size
            Text(
                text = if (result.isValid) formattedLot else "0.00",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = (-1).sp
                ),
                color = if (result.isValid) ElegantIceBlue else ElegantTextMuted,
                modifier = Modifier.testTag("lot_size_value")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-pill with Total Risk
            Surface(
                shape = RoundedCornerShape(50),
                color = ElegantDarkSurfaceHighlight,
                modifier = Modifier.clip(RoundedCornerShape(50))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Total Risk: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElegantIceBlue.copy(alpha = 0.8f)
                    )
                    Text(
                        text = TradingAccount.formatCurrency(result.totalRisk, account.currencySymbol),
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        fontWeight = FontWeight.Bold,
                        color = ElegantIceBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = ElegantDarkBorderSubtle.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Metric 1: 1% of Drawdown
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "1% DRAWDOWN",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElegantTextSecondary,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = TradingAccount.formatCurrency(result.onePercentDrawdown, account.currencySymbol),
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        fontWeight = FontWeight.SemiBold,
                        color = ElegantTextPrimary
                    )
                }

                // Metric 2: Risk per Point
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "RISK / POINT",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElegantTextSecondary,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${TradingAccount.formatCurrency(result.riskPerPoint, account.currencySymbol)}/pt",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        fontWeight = FontWeight.SemiBold,
                        color = ElegantIceBlue
                    )
                }

                // Copy Action Pill
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isCopied) ElegantEmerald else ElegantDarkSurfaceHighlight,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCopied) ElegantEmerald else ElegantDarkBorderStrong
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            if (result.isValid) {
                                clipboardManager.setText(AnnotatedString(formattedLot))
                                isCopied = true
                                onCopyFeedback("Copied lot size $formattedLot to clipboard")
                                coroutineScope.launch {
                                    delay(2000)
                                    isCopied = false
                                }
                            }
                        }
                        .testTag("copy_lot_size_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy lot size",
                            tint = if (isCopied) Color(0xFF003822) else ElegantIceBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCopied) "Copied" else "Copy",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCopied) Color(0xFF003822) else ElegantIceBlue
                        )
                    }
                }
            }

            if (account.lotReference != account.slReference || account.pointReference != 1.0) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ElegantDarkSurface.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Base Ratio: ${TradingAccount.formatLot(result.baseLotRatio)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElegantTextSecondary
                        )
                        Text(
                            text = "Ref Calibrated: ${TradingAccount.formatLot(result.calibratedLotSize)} lots",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElegantIceBlue
                        )
                    }
                }
            }
        }
    }
}


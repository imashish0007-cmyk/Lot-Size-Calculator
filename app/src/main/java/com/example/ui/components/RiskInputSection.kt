package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderStrong
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceHighlight
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantIceBlue
import com.example.ui.theme.ElegantIceBlueOn
import com.example.ui.theme.ElegantTextLight
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary

@Composable
fun RiskInputSection(
    riskPercent: String,
    slPoints: String,
    onRiskPercentChange: (String) -> Unit,
    onSlPointsChange: (String) -> Unit,
    onRiskAdjust: (Double) -> Unit,
    onSlAdjust: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // INPUT 1: RISK PERCENTAGE (%)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = ElegantDarkSurface
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                ElegantIceBlue.copy(alpha = 0.85f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RISK PERCENTAGE (%)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElegantIceBlue,
                        letterSpacing = 1.sp
                    )

                    // Stepper buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ElegantDarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorderStrong),
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .clickable { onRiskAdjust(-0.5) }
                                .testTag("risk_minus_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease Risk %",
                                tint = ElegantTextPrimary,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(16.dp)
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = ElegantDarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorderStrong),
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .clickable { onRiskAdjust(0.5) }
                                .testTag("risk_plus_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase Risk %",
                                tint = ElegantTextPrimary,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = riskPercent,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("""^\d*\.?\d*$"""))) {
                            onRiskPercentChange(newValue)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("risk_percent_input"),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        Text(
                            text = "%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ElegantIceBlue,
                            modifier = Modifier.padding(end = 14.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElegantIceBlue,
                        unfocusedBorderColor = ElegantDarkBorderStrong,
                        focusedContainerColor = ElegantDarkSurfaceVariant.copy(alpha = 0.6f),
                        unfocusedContainerColor = ElegantDarkSurfaceVariant.copy(alpha = 0.35f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Presets
                val riskPresets = listOf("0.5", "1.0", "1.5", "2.0", "3.0", "5.0")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    riskPresets.forEach { preset ->
                        val isSelected = riskPercent == preset || riskPercent == preset.toDoubleOrNull()?.toString()
                        val chipBg by animateColorAsState(
                            if (isSelected) ElegantIceBlue else ElegantDarkSurfaceVariant,
                            label = "risk_preset_bg"
                        )
                        val chipText = if (isSelected) ElegantIceBlueOn else ElegantTextSecondary

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = chipBg,
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                                1.dp,
                                ElegantDarkBorderStrong
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onRiskPercentChange(preset) }
                                .testTag("risk_preset_$preset")
                        ) {
                            Text(
                                text = "$preset%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = chipText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // INPUT 2: STOP LOSS (POINTS/PIPS)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = ElegantDarkSurface
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                ElegantDarkBorderStrong
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STOP LOSS (POINTS/PIPS)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElegantTextSecondary,
                        letterSpacing = 1.sp
                    )

                    // Stepper buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ElegantDarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorderStrong),
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .clickable { onSlAdjust(-1.0) }
                                .testTag("sl_minus_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease SL Points",
                                tint = ElegantTextPrimary,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(16.dp)
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = ElegantDarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorderStrong),
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .clickable { onSlAdjust(1.0) }
                                .testTag("sl_plus_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase SL Points",
                                tint = ElegantTextPrimary,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = slPoints,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("""^\d*\.?\d*$"""))) {
                            onSlPointsChange(newValue)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sl_points_input"),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        Text(
                            text = "PTS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ElegantTextSecondary,
                            modifier = Modifier.padding(end = 14.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElegantIceBlue,
                        unfocusedBorderColor = ElegantDarkBorderStrong,
                        focusedContainerColor = ElegantDarkSurfaceVariant.copy(alpha = 0.6f),
                        unfocusedContainerColor = ElegantDarkSurfaceVariant.copy(alpha = 0.35f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick SL Presets
                val slPresets = listOf("5", "10", "15", "20", "25", "30", "50")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    slPresets.forEach { preset ->
                        val isSelected = slPoints == preset || slPoints == preset.toDoubleOrNull()?.toString()
                        val chipBg by animateColorAsState(
                            if (isSelected) ElegantIceBlue else ElegantDarkSurfaceVariant,
                            label = "sl_preset_bg"
                        )
                        val chipText = if (isSelected) ElegantIceBlueOn else ElegantTextSecondary

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = chipBg,
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                                1.dp,
                                ElegantDarkBorderStrong
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSlPointsChange(preset) }
                                .testTag("sl_preset_$preset")
                        ) {
                            Text(
                                text = preset,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = chipText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


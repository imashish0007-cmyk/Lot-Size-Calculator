package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.TradingAccount
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

@Composable
fun AccountEditDialog(
    account: TradingAccount,
    onDismiss: () -> Unit,
    onSave: (TradingAccount) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(account.name) }
    var accountType by remember { mutableStateOf(account.accountType) }
    var amountStr by remember { mutableStateOf(TradingAccount.formatCompact(account.amount)) }
    var totalDrawdownStr by remember { mutableStateOf(TradingAccount.formatCompact(account.totalDrawdown)) }
    var pointRefStr by remember { mutableStateOf(TradingAccount.formatCompact(account.pointReference)) }
    var lotRefStr by remember { mutableStateOf(TradingAccount.formatCompact(account.lotReference)) }
    var slRefStr by remember { mutableStateOf(TradingAccount.formatCompact(account.slReference)) }
    var currencySymbol by remember { mutableStateOf(account.currencySymbol) }

    val amount = amountStr.toDoubleOrNull() ?: 0.0
    val totalDrawdown = totalDrawdownStr.toDoubleOrNull() ?: 0.0
    val previewOnePercent = if (totalDrawdown > 0) amount / totalDrawdown else 0.0

    val isNewAccount = account.id == 0L

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = ElegantDarkSurfaceHighlight,
        unfocusedContainerColor = ElegantDarkSurfaceVariant,
        focusedBorderColor = ElegantIceBlue,
        unfocusedBorderColor = ElegantDarkBorderStrong,
        focusedTextColor = ElegantTextPrimary,
        unfocusedTextColor = ElegantTextPrimary,
        focusedLabelColor = ElegantIceBlue,
        unfocusedLabelColor = ElegantTextSecondary
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = ElegantDarkSurface,
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                ElegantDarkBorderStrong
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Dialog Title Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = ElegantIceBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isNewAccount) "New Account Setup" else "Edit Account Setup",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ElegantTextPrimary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_account_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ElegantTextSecondary
                        )
                    }
                }

                Text(
                    text = "Configure initial reference numbers for this account. These values remain fixed during trading calculations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElegantTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Presets chips for fast configuration
                Text(
                    text = "QUICK PRESETS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ElegantTextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PresetButton("Real Micro (30/12)", isSelected = accountType == "MICRO") {
                        accountType = "MICRO"
                        amountStr = "30"
                        totalDrawdownStr = "12"
                        pointRefStr = "1"
                        lotRefStr = "0.1"
                        slRefStr = "0.1"
                        if (name.isBlank() || name.startsWith("Account") || name.startsWith("Real") || name.startsWith("Prop")) {
                            name = "REAL-MICRO"
                        }
                    }
                    PresetButton("Prop Firm (600/12)", isSelected = accountType == "PROP" && amountStr == "600") {
                        accountType = "PROP"
                        amountStr = "600"
                        totalDrawdownStr = "12"
                        pointRefStr = "1"
                        lotRefStr = "0.01"
                        slRefStr = "1"
                        if (name.isBlank() || name.startsWith("Account") || name.startsWith("Real") || name.startsWith("Prop")) {
                            name = "PROP"
                        }
                    }
                    PresetButton("Prop 100k", isSelected = amountStr == "100000") {
                        accountType = "PROP"
                        amountStr = "100000"
                        totalDrawdownStr = "10"
                        pointRefStr = "1"
                        lotRefStr = "0.01"
                        slRefStr = "1"
                        if (name.isBlank() || name.startsWith("Account") || name.startsWith("Real") || name.startsWith("Prop")) {
                            name = "PROP 100k"
                        }
                    }
                    PresetButton("Real Standard", isSelected = accountType == "STANDARD") {
                        accountType = "STANDARD"
                        amountStr = "1000"
                        totalDrawdownStr = "100"
                        pointRefStr = "1"
                        lotRefStr = "0.01"
                        slRefStr = "1"
                        if (name.isBlank() || name.startsWith("Account") || name.startsWith("Real") || name.startsWith("Prop")) {
                            name = "REAL 1"
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = ElegantDarkBorderSubtle)
                Spacer(modifier = Modifier.height(18.dp))

                // Account Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Account Name") },
                    placeholder = { Text("e.g. REAL-MICRO, PROP 1, Prop Firm 2") },
                    colors = textFieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("account_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Amount Size & Total Drawdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Amount Size") },
                        placeholder = { Text("e.g. 30, 600") },
                        colors = textFieldColors,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("account_amount_input"),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = totalDrawdownStr,
                        onValueChange = { totalDrawdownStr = it },
                        label = { Text("Total Drawdown") },
                        placeholder = { Text("e.g. 12") },
                        colors = textFieldColors,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("account_drawdown_input"),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 1% of Drawdown Preview Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ElegantDarkSurfaceHighlight
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        ElegantDarkBorderStrong
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "1% OF DRAWDOWN (AUTO-CALCULATED)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ElegantIceBlue,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "Amount ($amount) / Drawdown ($totalDrawdown)",
                                style = MaterialTheme.typography.bodySmall,
                                color = ElegantTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "$currencySymbol${TradingAccount.formatCompact(previewOnePercent)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElegantIceBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "REFERENCE CALIBRATION (INITIAL FIX)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ElegantTextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Point Ref, Lot Ref, SL Ref
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = pointRefStr,
                        onValueChange = { pointRefStr = it },
                        label = { Text("Point Ref") },
                        colors = textFieldColors,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("account_point_ref_input"),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = lotRefStr,
                        onValueChange = { lotRefStr = it },
                        label = { Text("Lot Ref") },
                        colors = textFieldColors,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("account_lot_ref_input"),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = slRefStr,
                        onValueChange = { slRefStr = it },
                        label = { Text("SL Ref") },
                        colors = textFieldColors,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("account_sl_ref_input"),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save & Cancel Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ElegantTextSecondary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorderStrong),
                        modifier = Modifier.testTag("cancel_account_button")
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            val finalAccount = account.copy(
                                name = if (name.isNotBlank()) name.trim() else "Account",
                                accountType = accountType,
                                amount = amountStr.toDoubleOrNull() ?: 100.0,
                                totalDrawdown = totalDrawdownStr.toDoubleOrNull() ?: 12.0,
                                pointReference = pointRefStr.toDoubleOrNull() ?: 1.0,
                                lotReference = lotRefStr.toDoubleOrNull() ?: 0.01,
                                slReference = slRefStr.toDoubleOrNull() ?: 1.0,
                                currencySymbol = currencySymbol
                            )
                            onSave(finalAccount)
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElegantIceBlue,
                            contentColor = ElegantIceBlueOn
                        ),
                        modifier = Modifier.testTag("save_account_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = ElegantIceBlueOn,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isNewAccount) "Create Account" else "Save Changes",
                            fontWeight = FontWeight.Bold,
                            color = ElegantIceBlueOn
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isSelected) ElegantIceBlue else ElegantDarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) ElegantIceBlue else ElegantDarkBorderStrong
        ),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = if (isSelected) ElegantIceBlueOn else ElegantTextSecondary
            ),
            border = null,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) ElegantIceBlueOn else ElegantTextSecondary
            )
        }
    }
}

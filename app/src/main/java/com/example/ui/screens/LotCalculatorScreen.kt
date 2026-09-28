package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.TradingAccount
import com.example.ui.LotCalculatorViewModel
import com.example.ui.components.AccountEditDialog
import com.example.ui.components.AccountReferenceCard
import com.example.ui.components.AccountSelectorBar
import com.example.ui.components.CalculationResultCard
import com.example.ui.components.MultiAccountComparisonSheet
import com.example.ui.components.RiskInputSection
import com.example.ui.theme.ElegantDarkBackground
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LotCalculatorScreen(
    viewModel: LotCalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.allAccounts.collectAsStateWithLifecycle()
    val selectedAccountId by viewModel.selectedAccountId.collectAsStateWithLifecycle()
    val selectedAccount by viewModel.selectedAccount.collectAsStateWithLifecycle()
    val riskPercent by viewModel.riskPercentInput.collectAsStateWithLifecycle()
    val slPoints by viewModel.slPointsInput.collectAsStateWithLifecycle()
    val calculationResult by viewModel.calculationResult.collectAsStateWithLifecycle()
    val multiSummaries by viewModel.multiAccountCalculations.collectAsStateWithLifecycle()

    val isAccountDialogOpen by viewModel.isAccountDialogOpen.collectAsStateWithLifecycle()
    val editingAccount by viewModel.editingAccount.collectAsStateWithLifecycle()
    val isMultiAccountSheetOpen by viewModel.isMultiAccountSheetOpen.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var selectedNavTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBackground)
            .windowInsetsPadding(WindowInsets.statusBars),
        containerColor = ElegantDarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TradeCalc",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ElegantTextPrimary
                            )
                            Text(
                                text = "LOT SIZE ENGINE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ElegantTextSecondary,
                                letterSpacing = 1.5.sp,
                                fontSize = 10.sp
                            )
                        }

                        // Right actions: Add Account & Compare
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = ElegantDarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorderStrong),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable { viewModel.openNewAccountDialog() }
                                    .testTag("top_bar_add_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Account",
                                    tint = ElegantTextLight,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(20.dp)
                                    )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ElegantDarkBackground
                )
            )
        },
        bottomBar = {
            // Elegant Dark Navigation Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                color = ElegantDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 1: Calculator
                    NavPillItem(
                        icon = Icons.Default.Calculate,
                        label = "Calculator",
                        isSelected = selectedNavTab == 0,
                        onClick = { selectedNavTab = 0 }
                    )

                    // Tab 2: Compare All
                    NavPillItem(
                        icon = Icons.Default.TableChart,
                        label = "Compare",
                        isSelected = selectedNavTab == 1,
                        onClick = {
                            selectedNavTab = 0
                            viewModel.setMultiAccountSheetOpen(true)
                        }
                    )

                    // Tab 3: Setup / Accounts
                    NavPillItem(
                        icon = Icons.Default.ManageAccounts,
                        label = "Accounts",
                        isSelected = selectedNavTab == 2,
                        onClick = {
                            selectedNavTab = 0
                            if (selectedAccount != null) {
                                viewModel.openEditAccountDialog(selectedAccount!!)
                            } else {
                                viewModel.openNewAccountDialog()
                            }
                        }
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Account Switcher Bar (Horizontal Pills)
            item {
                AccountSelectorBar(
                    accounts = accounts,
                    selectedAccountId = selectedAccountId,
                    onAccountSelect = { viewModel.selectAccount(it) },
                    onAddAccountClick = { viewModel.openNewAccountDialog() },
                    onMultiAccountClick = { viewModel.setMultiAccountSheetOpen(true) }
                )
            }

            // 2. Active Account Reference Card (Static Config)
            item {
                if (selectedAccount != null) {
                    AccountReferenceCard(
                        account = selectedAccount!!,
                        onEditAccount = { viewModel.openEditAccountDialog(it) },
                        onDuplicateAccount = { viewModel.duplicateAccount(it) },
                        onDeleteAccount = { viewModel.deleteAccount(it) },
                        canDelete = accounts.size > 1
                    )
                }
            }

            // 3. Trade Inputs Section (Risk % and Stop Loss)
            item {
                RiskInputSection(
                    riskPercent = riskPercent,
                    slPoints = slPoints,
                    onRiskPercentChange = { viewModel.setRiskPercent(it) },
                    onSlPointsChange = { viewModel.setSlPoints(it) },
                    onRiskAdjust = { viewModel.adjustRiskPercent(it) },
                    onSlAdjust = { viewModel.adjustSlPoints(it) }
                )
            }

            // 4. Primary Result Display Hero Card
            item {
                if (selectedAccount != null) {
                    CalculationResultCard(
                        account = selectedAccount!!,
                        result = calculationResult,
                        riskPercent = riskPercent,
                        slPoints = slPoints,
                        onCopyFeedback = { msg ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(msg)
                            }
                        }
                    )
                }
            }

            // 5. Educational Formula & Calculation Reference
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ElegantDarkSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        ElegantDarkBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = ElegantIceBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CALCULATION ENGINE FORMULA",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ElegantIceBlue,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• 1% of Drawdown = Amount / Total Drawdown\n• Total Risk ($) = (1% of Drawdown) × (Risk %)\n• Lot Size = (Total Risk / SL Points) × (Lot Ref / SL Ref × Point Ref)",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElegantTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }

    // Account Edit/Add Dialog
    if (isAccountDialogOpen && editingAccount != null) {
        AccountEditDialog(
            account = editingAccount!!,
            onDismiss = { viewModel.closeAccountDialog() },
            onSave = { updatedAccount ->
                viewModel.saveAccount(updatedAccount)
            }
        )
    }

    // Multi-Account All Comparison Sheet
    if (isMultiAccountSheetOpen) {
        MultiAccountComparisonSheet(
            summaries = multiSummaries,
            riskPercent = riskPercent,
            slPoints = slPoints,
            selectedAccountId = selectedAccountId,
            onSelectAccount = { viewModel.selectAccount(it) },
            onDismiss = { viewModel.setMultiAccountSheetOpen(false) },
            onCopyFeedback = { msg ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(msg)
                }
            }
        )
    }
}

@Composable
private fun NavPillItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isSelected) ElegantDarkSurfaceHighlight else Color.Transparent,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) ElegantIceBlue else ElegantTextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ElegantIceBlue else ElegantTextMuted
            )
        }
    }
}



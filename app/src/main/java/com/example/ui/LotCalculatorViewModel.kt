package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AccountRepository
import com.example.data.TradingAccount
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CalculationResult(
    val onePercentDrawdown: Double = 0.0,
    val totalRisk: Double = 0.0,
    val baseLotRatio: Double = 0.0,
    val calibratedLotSize: Double = 0.0,
    val riskPerPoint: Double = 0.0,
    val isValid: Boolean = false
)

data class AccountCalculationSummary(
    val account: TradingAccount,
    val onePercentDrawdown: Double,
    val totalRisk: Double,
    val baseLotRatio: Double,
    val lotSize: Double
)

class LotCalculatorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AccountRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = AccountRepository(database.accountDao())
        viewModelScope.launch {
            repository.ensureDefaultAccountsExist()
        }
    }

    val allAccounts: StateFlow<List<TradingAccount>> = repository.allAccounts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedAccountId = MutableStateFlow<Long?>(null)
    val selectedAccountId: StateFlow<Long?> = _selectedAccountId.asStateFlow()

    private val _riskPercentInput = MutableStateFlow("3")
    val riskPercentInput: StateFlow<String> = _riskPercentInput.asStateFlow()

    private val _slPointsInput = MutableStateFlow("10")
    val slPointsInput: StateFlow<String> = _slPointsInput.asStateFlow()

    // Dialog state for Account Create/Edit
    private val _editingAccount = MutableStateFlow<TradingAccount?>(null)
    val editingAccount: StateFlow<TradingAccount?> = _editingAccount.asStateFlow()

    private val _isAccountDialogOpen = MutableStateFlow(false)
    val isAccountDialogOpen: StateFlow<Boolean> = _isAccountDialogOpen.asStateFlow()

    // Multi-account comparison sheet state
    private val _isMultiAccountSheetOpen = MutableStateFlow(false)
    val isMultiAccountSheetOpen: StateFlow<Boolean> = _isMultiAccountSheetOpen.asStateFlow()

    // UI events (e.g. snackbar messages)
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    // Selected account state
    val selectedAccount: StateFlow<TradingAccount?> = combine(
        allAccounts,
        _selectedAccountId
    ) { accounts, selectedId ->
        if (accounts.isEmpty()) return@combine null
        val found = accounts.find { it.id == selectedId }
        if (found != null) {
            found
        } else {
            // Default to first account if none selected or selected was deleted
            val first = accounts.first()
            _selectedAccountId.value = first.id
            first
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Current calculation result
    val calculationResult: StateFlow<CalculationResult> = combine(
        selectedAccount,
        _riskPercentInput,
        _slPointsInput
    ) { account, riskStr, slStr ->
        if (account == null) return@combine CalculationResult()

        val riskPercent = riskStr.toDoubleOrNull() ?: 0.0
        val slPoints = slStr.toDoubleOrNull() ?: 0.0

        if (riskPercent <= 0.0 || slPoints <= 0.0) {
            return@combine CalculationResult(
                onePercentDrawdown = account.getOnePercentDrawdown(),
                totalRisk = 0.0,
                baseLotRatio = 0.0,
                calibratedLotSize = 0.0,
                riskPerPoint = 0.0,
                isValid = false
            )
        }

        val onePercent = account.getOnePercentDrawdown()
        val totalRisk = account.calculateTotalRisk(riskPercent)
        val baseRatio = account.calculateBaseLotRatio(riskPercent, slPoints)
        val calibratedLot = account.calculateLotSize(riskPercent, slPoints)
        val riskPerPt = if (slPoints > 0) totalRisk / slPoints else 0.0

        CalculationResult(
            onePercentDrawdown = onePercent,
            totalRisk = totalRisk,
            baseLotRatio = baseRatio,
            calibratedLotSize = calibratedLot,
            riskPerPoint = riskPerPt,
            isValid = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalculationResult()
    )

    // Multi-account calculations across all accounts for the current Risk% and SL points
    val multiAccountCalculations: StateFlow<List<AccountCalculationSummary>> = combine(
        allAccounts,
        _riskPercentInput,
        _slPointsInput
    ) { accounts, riskStr, slStr ->
        val riskPercent = riskStr.toDoubleOrNull() ?: 0.0
        val slPoints = slStr.toDoubleOrNull() ?: 0.0

        accounts.map { account ->
            val onePercent = account.getOnePercentDrawdown()
            val totalRisk = if (riskPercent > 0) account.calculateTotalRisk(riskPercent) else 0.0
            val baseRatio = if (riskPercent > 0 && slPoints > 0) account.calculateBaseLotRatio(riskPercent, slPoints) else 0.0
            val lot = if (riskPercent > 0 && slPoints > 0) account.calculateLotSize(riskPercent, slPoints) else 0.0

            AccountCalculationSummary(
                account = account,
                onePercentDrawdown = onePercent,
                totalRisk = totalRisk,
                baseLotRatio = baseRatio,
                lotSize = lot
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectAccount(accountId: Long) {
        _selectedAccountId.value = accountId
        val account = allAccounts.value.find { it.id == accountId }
        if (account != null) {
            // Update default inputs if current ones are default
            if (_riskPercentInput.value.isBlank() || _riskPercentInput.value == "0") {
                _riskPercentInput.value = TradingAccount.formatCompact(account.defaultRiskPercent)
            }
            if (_slPointsInput.value.isBlank() || _slPointsInput.value == "0") {
                _slPointsInput.value = TradingAccount.formatCompact(account.defaultSlPoints)
            }
        }
    }

    fun setRiskPercent(value: String) {
        _riskPercentInput.value = value
    }

    fun setSlPoints(value: String) {
        _slPointsInput.value = value
    }

    fun adjustRiskPercent(delta: Double) {
        val current = _riskPercentInput.value.toDoubleOrNull() ?: 0.0
        val updated = (current + delta).coerceAtLeast(0.1)
        _riskPercentInput.value = TradingAccount.formatCompact(updated)
    }

    fun adjustSlPoints(delta: Double) {
        val current = _slPointsInput.value.toDoubleOrNull() ?: 0.0
        val updated = (current + delta).coerceAtLeast(0.1)
        _slPointsInput.value = TradingAccount.formatCompact(updated)
    }

    fun openNewAccountDialog(presetType: String? = null) {
        val preset = when (presetType) {
            "MICRO" -> TradingAccount(
                name = "Real Micro ${allAccounts.value.size + 1}",
                accountType = "MICRO",
                amount = 30.0,
                totalDrawdown = 12.0,
                pointReference = 1.0,
                lotReference = 0.1,
                slReference = 0.1,
                currencySymbol = "$",
                defaultRiskPercent = 3.0,
                defaultSlPoints = 10.0
            )
            "PROP" -> TradingAccount(
                name = "Prop Firm ${allAccounts.value.size + 1}",
                accountType = "PROP",
                amount = 600.0,
                totalDrawdown = 12.0,
                pointReference = 1.0,
                lotReference = 0.01,
                slReference = 1.0,
                currencySymbol = "$",
                defaultRiskPercent = 3.0,
                defaultSlPoints = 10.0
            )
            "PROP_100K" -> TradingAccount(
                name = "FTMO 100k",
                accountType = "PROP",
                amount = 100000.0,
                totalDrawdown = 10.0,
                pointReference = 1.0,
                lotReference = 0.01,
                slReference = 1.0,
                currencySymbol = "$",
                defaultRiskPercent = 1.0,
                defaultSlPoints = 20.0
            )
            "STANDARD" -> TradingAccount(
                name = "Real Standard ${allAccounts.value.size + 1}",
                accountType = "STANDARD",
                amount = 1000.0,
                totalDrawdown = 100.0,
                pointReference = 1.0,
                lotReference = 0.01,
                slReference = 1.0,
                currencySymbol = "$",
                defaultRiskPercent = 2.0,
                defaultSlPoints = 15.0
            )
            else -> TradingAccount(
                name = "Account ${allAccounts.value.size + 1}",
                accountType = "REAL",
                amount = 1000.0,
                totalDrawdown = 10.0,
                pointReference = 1.0,
                lotReference = 0.01,
                slReference = 1.0,
                currencySymbol = "$",
                defaultRiskPercent = 2.0,
                defaultSlPoints = 10.0
            )
        }
        _editingAccount.value = preset
        _isAccountDialogOpen.value = true
    }

    fun openEditAccountDialog(account: TradingAccount) {
        _editingAccount.value = account
        _isAccountDialogOpen.value = true
    }

    fun closeAccountDialog() {
        _isAccountDialogOpen.value = false
        _editingAccount.value = null
    }

    fun saveAccount(account: TradingAccount) {
        viewModelScope.launch {
            if (account.id == 0L) {
                val newId = repository.insertAccount(account)
                _selectedAccountId.value = newId
                _eventFlow.emit("Account '${account.name}' created")
            } else {
                repository.updateAccount(account)
                _eventFlow.emit("Account '${account.name}' updated")
            }
            closeAccountDialog()
        }
    }

    fun deleteAccount(account: TradingAccount) {
        viewModelScope.launch {
            repository.deleteAccount(account)
            _eventFlow.emit("Account '${account.name}' deleted")
            // Pick another account
            val remaining = allAccounts.value.filter { it.id != account.id }
            if (remaining.isNotEmpty()) {
                _selectedAccountId.value = remaining.first().id
            } else {
                _selectedAccountId.value = null
            }
            closeAccountDialog()
        }
    }

    fun duplicateAccount(account: TradingAccount) {
        viewModelScope.launch {
            val copy = account.copy(
                id = 0,
                name = "${account.name} (Copy)",
                createdAt = System.currentTimeMillis()
            )
            val newId = repository.insertAccount(copy)
            _selectedAccountId.value = newId
            _eventFlow.emit("Account duplicated as '${copy.name}'")
        }
    }

    fun setMultiAccountSheetOpen(isOpen: Boolean) {
        _isMultiAccountSheetOpen.value = isOpen
    }

    fun emitMessage(message: String) {
        viewModelScope.launch {
            _eventFlow.emit(message)
        }
    }
}

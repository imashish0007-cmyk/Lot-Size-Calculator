package com.example.data

import kotlinx.coroutines.flow.Flow

class AccountRepository(private val accountDao: AccountDao) {
    val allAccounts: Flow<List<TradingAccount>> = accountDao.getAllAccounts()

    suspend fun getAccountById(id: Long): TradingAccount? = accountDao.getAccountById(id)

    suspend fun insertAccount(account: TradingAccount): Long = accountDao.insertAccount(account)

    suspend fun updateAccount(account: TradingAccount) = accountDao.updateAccount(account)

    suspend fun deleteAccount(account: TradingAccount) = accountDao.deleteAccount(account)

    suspend fun deleteAccountById(id: Long) = accountDao.deleteAccountById(id)

    suspend fun ensureDefaultAccountsExist() {
        AppDatabase.populateInitialAccounts(accountDao)
    }
}

package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM trading_accounts ORDER BY orderIndex ASC, id ASC")
    fun getAllAccounts(): Flow<List<TradingAccount>>

    @Query("SELECT * FROM trading_accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: Long): TradingAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: TradingAccount): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<TradingAccount>)

    @Update
    suspend fun updateAccount(account: TradingAccount)

    @Delete
    suspend fun deleteAccount(account: TradingAccount)

    @Query("DELETE FROM trading_accounts WHERE id = :id")
    suspend fun deleteAccountById(id: Long)

    @Query("SELECT COUNT(*) FROM trading_accounts")
    suspend fun getAccountCount(): Int
}

package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [TradingAccount::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trading_lot_calculator.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialAccounts(database.accountDao())
                    }
                }
            }
        }

        suspend fun populateInitialAccounts(dao: AccountDao) {
            if (dao.getAccountCount() == 0) {
                val defaultAccounts = listOf(
                    TradingAccount(
                        name = "REAL-MICRO",
                        accountType = "MICRO",
                        amount = 30.0,
                        totalDrawdown = 12.0,
                        pointReference = 1.0,
                        lotReference = 0.1,
                        slReference = 0.1,
                        currencySymbol = "$",
                        defaultRiskPercent = 3.0,
                        defaultSlPoints = 10.0,
                        orderIndex = 0
                    ),
                    TradingAccount(
                        name = "PROP",
                        accountType = "PROP",
                        amount = 600.0,
                        totalDrawdown = 12.0,
                        pointReference = 1.0,
                        lotReference = 0.01,
                        slReference = 1.0,
                        currencySymbol = "$",
                        defaultRiskPercent = 3.0,
                        defaultSlPoints = 10.0,
                        orderIndex = 1
                    ),
                    TradingAccount(
                        name = "PROP 2 (100k)",
                        accountType = "PROP",
                        amount = 100000.0,
                        totalDrawdown = 10.0,
                        pointReference = 1.0,
                        lotReference = 0.01,
                        slReference = 1.0,
                        currencySymbol = "$",
                        defaultRiskPercent = 1.0,
                        defaultSlPoints = 20.0,
                        orderIndex = 2
                    ),
                    TradingAccount(
                        name = "REAL 1",
                        accountType = "STANDARD",
                        amount = 1000.0,
                        totalDrawdown = 100.0,
                        pointReference = 1.0,
                        lotReference = 0.01,
                        slReference = 1.0,
                        currencySymbol = "$",
                        defaultRiskPercent = 2.0,
                        defaultSlPoints = 15.0,
                        orderIndex = 3
                    )
                )
                dao.insertAccounts(defaultAccounts)
            }
        }
    }
}

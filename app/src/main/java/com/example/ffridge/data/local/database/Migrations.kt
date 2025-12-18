package com.example.ffridge.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    /**
     * Migration from version 1 to 2
     * Example: Adding a new column to ingredients table
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example: Add new column
            database.execSQL(
                "ALTER TABLE ingredients ADD COLUMN location TEXT DEFAULT 'fridge'"
            )
        }
    }

    /**
     * Migration from version 2 to 3
     * Example: Creating a new table
     */
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example: Create shopping list table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS shopping_list (
                    id TEXT PRIMARY KEY NOT NULL,
                    itemName TEXT NOT NULL,
                    quantity TEXT NOT NULL,
                    isPurchased INTEGER NOT NULL DEFAULT 0,
                    createdAt INTEGER NOT NULL
                )
            """.trimIndent())
        }
    }

    /**
     * Get all migrations as array
     */
    fun getAllMigrations(): Array<Migration> {
        return arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3
        )
    }
}

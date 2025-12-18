package com.example.ffridge.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ffridge.data.local.dao.ChatMessageDao
import com.example.ffridge.data.local.dao.IngredientDao
import com.example.ffridge.data.local.dao.RecipeDao
import com.example.ffridge.data.local.entity.ChatMessageEntity
import com.example.ffridge.data.local.entity.IngredientEntity
import com.example.ffridge.data.local.entity.RecipeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        IngredientEntity::class,
        RecipeEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FfridgeDatabase : RoomDatabase() {

    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeDao(): RecipeDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: FfridgeDatabase? = null

        fun getDatabase(context: Context): FfridgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FfridgeDatabase::class.java,
                    "ffridge_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(): FfridgeDatabase {
            return INSTANCE ?: throw IllegalStateException(
                "Database has not been initialized. Call getDatabase(context) first."
            )
        }
    }

    /**
     * Database Callback to populate initial data
     */
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: FfridgeDatabase) {
            val chatDao = database.chatMessageDao()

            // Insert welcome message
            val welcomeMessage = ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                role = "MODEL",
                text = "Hello! I'm your Sous Chef. Ask me about storage tips, cooking times, or substitutions!",
                timestamp = System.currentTimeMillis()
            )
            chatDao.insertMessage(welcomeMessage)

            // Optional: Add sample ingredients for testing
            val ingredientDao = database.ingredientDao()
            val sampleIngredients = listOf(
                IngredientEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Milk",
                    quantity = "1",
                    unit = "L",
                    category = "DAIRY",
                    expiryDate = System.currentTimeMillis() + (5 * 24 * 60 * 60 * 1000), // 5 days
                    addedDate = System.currentTimeMillis(),
                    notes = "Fresh whole milk",
                    imageUrl = null
                ),
                IngredientEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Eggs",
                    quantity = "12",
                    unit = "pcs",
                    category = "DAIRY",
                    expiryDate = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000), // 7 days
                    addedDate = System.currentTimeMillis(),
                    notes = null,
                    imageUrl = null
                ),
                IngredientEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Chicken Breast",
                    quantity = "500",
                    unit = "g",
                    category = "MEAT",
                    expiryDate = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000), // 3 days
                    addedDate = System.currentTimeMillis(),
                    notes = "Fresh chicken",
                    imageUrl = null
                )
            )
            ingredientDao.insertIngredients(sampleIngredients)
        }
    }
}

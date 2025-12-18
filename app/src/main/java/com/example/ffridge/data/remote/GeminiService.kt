package com.example.ffridge.data.remote

import com.example.ffridge.BuildConfig
import com.example.ffridge.data.model.ChatMessage
import com.example.ffridge.data.model.MessageRole
import com.example.ffridge.data.model.Recipe
import com.example.ffridge.data.model.RecipeDifficulty
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import java.util.UUID

class GeminiService {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 2048
        },
        systemInstruction = content {
            text("""
                You are a professional Sous Chef AI assistant for a smart kitchen app called "ffridge".
                Your role is to help users with:
                - Food storage tips and best practices
                - Cooking times and temperatures
                - Ingredient substitutions
                - Recipe suggestions based on available ingredients
                - Cooking techniques and tips
                
                Guidelines:
                - Be friendly, concise, and helpful
                - Provide accurate, food-safe information
                - Use simple language and avoid overly technical terms
                - When suggesting recipes, be creative but practical
                - Always prioritize food safety
                - Keep responses under 200 words unless specifically asked for more detail
                
                Format:
                - Use clear paragraphs
                - Use bullet points for lists
                - Be encouraging and positive
            """.trimIndent())
        }
    )

    /**
     * Send chat message and get response
     */
    suspend fun sendMessage(
        message: String,
        conversationHistory: List<ChatMessage> = emptyList()
    ): String {
        return try {
            val chat = generativeModel.startChat(
                history = conversationHistory.takeLast(5).map { msg ->
                    content(msg.role.name.lowercase()) { text(msg.text) }
                }
            )

            val response = chat.sendMessage(message)
            response.text ?: "I'm sorry, I couldn't generate a response. Please try again."
        } catch (e: Exception) {
            throw Exception("Failed to get AI response: ${e.message}")
        }
    }

    /**
     * Generate recipe from ingredients with enhanced prompt
     */
    suspend fun generateRecipe(ingredients: List<String>): Recipe {
        val ingredientList = ingredients.joinToString(", ")

        val prompt = """
            Create a delicious and practical recipe using PRIMARILY these ingredients: $ingredientList
            
            You can assume basic pantry items like: salt, pepper, oil, butter, garlic, onions.
            
            Return ONLY the recipe in this EXACT format (no extra text):
            
            TITLE: [Creative, appetizing recipe name]
            DESCRIPTION: [1-2 sentence description that sounds delicious]
            INGREDIENTS: [ingredient 1], [ingredient 2], [ingredient 3], etc.
            INSTRUCTIONS: [Step 1] | [Step 2] | [Step 3] | etc.
            COOKING_TIME: [total time in minutes as a number]
            DIFFICULTY: [EASY or MEDIUM or HARD]
            
            Requirements:
            - Make it realistic and achievable
            - Include exact measurements in ingredients
            - Instructions should be clear and numbered
            - Cooking time should be accurate
            - Choose difficulty based on technique complexity
        """.trimIndent()

        return try {
            val response = generativeModel.generateContent(prompt)
            val text = response.text ?: throw Exception("Empty response from AI")

            parseRecipe(text, ingredients)
        } catch (e: Exception) {
            // Fallback recipe if AI fails
            Recipe(
                id = UUID.randomUUID().toString(),
                title = "Simple ${ingredients.firstOrNull() ?: "Ingredient"} Dish",
                description = "A quick and easy recipe using your available ingredients",
                ingredients = ingredients.map { "1 cup $it" },
                instructions = listOf(
                    "Prepare all ingredients",
                    "Cook according to standard methods",
                    "Season to taste",
                    "Serve hot"
                ),
                cookingTime = 30,
                difficulty = RecipeDifficulty.EASY,
                imageUrl = null,
                createdAt = System.currentTimeMillis(),
                isFavorite = false
            )
        }
    }

    private fun parseRecipe(text: String, fallbackIngredients: List<String>): Recipe {
        val lines = text.lines().filter { it.isNotBlank() }
        var title = "Generated Recipe"
        var description = "A delicious recipe created just for you"
        var ingredients = listOf<String>()
        var instructions = listOf<String>()
        var cookingTime = 30
        var difficulty = RecipeDifficulty.MEDIUM

        lines.forEach { line ->
            when {
                line.startsWith("TITLE:", ignoreCase = true) -> {
                    title = line.substringAfter(":").trim()
                }
                line.startsWith("DESCRIPTION:", ignoreCase = true) -> {
                    description = line.substringAfter(":").trim()
                }
                line.startsWith("INGREDIENTS:", ignoreCase = true) -> {
                    val ingredientText = line.substringAfter(":").trim()
                    ingredients = ingredientText.split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                }
                line.startsWith("INSTRUCTIONS:", ignoreCase = true) -> {
                    val instructionText = line.substringAfter(":").trim()
                    instructions = instructionText.split("|")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .mapIndexed { index, step ->
                            // Remove any existing step numbers
                            step.replaceFirst(Regex("^\\d+\\.?\\s*"), "")
                        }
                }
                line.startsWith("COOKING_TIME:", ignoreCase = true) -> {
                    val timeText = line.substringAfter(":").trim()
                    cookingTime = timeText.filter { it.isDigit() }.toIntOrNull() ?: 30
                }
                line.startsWith("DIFFICULTY:", ignoreCase = true) -> {
                    val diffText = line.substringAfter(":").trim().uppercase()
                    difficulty = try {
                        RecipeDifficulty.valueOf(diffText)
                    } catch (e: Exception) {
                        RecipeDifficulty.MEDIUM
                    }
                }
            }
        }

        // Fallback to input ingredients if parsing failed
        if (ingredients.isEmpty()) {
            ingredients = fallbackIngredients.map { "1 cup $it" }
        }

        // Fallback instructions if parsing failed
        if (instructions.isEmpty()) {
            instructions = listOf(
                "Prepare and clean all ingredients",
                "Heat a pan over medium heat",
                "Cook ingredients until done",
                "Season to taste and serve"
            )
        }

        return Recipe(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            ingredients = ingredients,
            instructions = instructions,
            cookingTime = cookingTime,
            difficulty = difficulty,
            imageUrl = null,
            createdAt = System.currentTimeMillis(),
            isFavorite = false
        )
    }

    /**
     * Get quick cooking tips
     */
    suspend fun getCookingTip(category: String): String {
        val prompt = "Give me one quick, practical $category tip for home cooks. Keep it under 50 words."

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: "Check back later for cooking tips!"
        } catch (e: Exception) {
            "Unable to fetch cooking tip at the moment."
        }
    }

    /**
     * Get storage tips for ingredient
     */
    suspend fun getStorageTips(ingredientName: String): String {
        val prompt = """
            How should I store $ingredientName to keep it fresh? 
            Provide 2-3 practical tips in bullet points.
            Keep it concise and actionable.
        """.trimIndent()

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: "Store in a cool, dry place."
        } catch (e: Exception) {
            "Store properly to maintain freshness."
        }
    }

    /**
     * Get substitution suggestions
     */
    suspend fun getSubstitutions(ingredientName: String): String {
        val prompt = """
            What can I substitute for $ingredientName in cooking?
            Provide 2-3 alternatives with brief explanations.
            Format as bullet points.
        """.trimIndent()

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: "No substitutions available."
        } catch (e: Exception) {
            "Unable to suggest substitutions."
        }
    }
}

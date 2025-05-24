package com.example.letsgogambling

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.random.Random

// ViewModel for managing dice rolling logic and state.
class DiceViewModel : ViewModel() {
    // List to store the results of dice rolls.
    var diceResults by mutableStateOf(emptyList<Int>())
        private set

    // String to store a randomly generated sentence, if enabled.
    var randomSentenceText by mutableStateOf<String?>(null)
        private set

    // Number of dice to roll.
    var numberOfDice by mutableIntStateOf(1)
    // Number of sides on each die.
    var numberOfSides by mutableIntStateOf(20)
    // Flag to enable/disable random number of dice and sides.
    var isRandomDiceEnabled by mutableStateOf(false)
    // Flag to enable/disable generating a random sentence.
    var isRandomSentenceEnabled by mutableStateOf(false)

    private val randomSentenceGenerator = RandomSentenceGenerator()

    // Performs a dice roll based on the current settings.
    fun performRoll() {
        // If random dice is enabled, generate random number of dice and sides.
        if (isRandomDiceEnabled) {
            numberOfDice = Random.nextInt(MainActivity.MIN_DICE_COUNT, MainActivity.MAX_DICE_COUNT + 1)
            numberOfSides = Random.nextInt(MainActivity.MIN_SIDES_COUNT, MainActivity.MAX_SIDES_COUNT + 1)
        }
        // Roll the dice and update the results.
        diceResults = rollDice(numberOfDice, numberOfSides)
        // Generate a random sentence if enabled.
        randomSentenceText = if (isRandomSentenceEnabled) randomSentenceGenerator.generate() else null
    }

    private fun rollDice(numberOfDice: Int, numberOfSides: Int): List<Int> {
        val validNumberOfDice = numberOfDice.coerceIn(MainActivity.MIN_DICE_COUNT, MainActivity.MAX_DICE_COUNT)
        val validNumberOfSides = numberOfSides.coerceIn(MainActivity.MIN_SIDES_COUNT, MainActivity.MAX_SIDES_COUNT)
        return List(validNumberOfDice) { Random.nextInt(1, validNumberOfSides + 1) }
    }
}
package com.example.letsgogambling

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class DiceViewModel : ViewModel() {
    var diceResults by mutableStateOf(emptyList<Int>())
        private set

    var randomSentenceText by mutableStateOf<String?>(null)
        private set

    var numberOfDice by mutableIntStateOf(1)
    var numberOfSides by mutableIntStateOf(20)
    var isRandomDiceEnabled by mutableStateOf(false)
    var isRandomSentenceEnabled by mutableStateOf(false)

    private val randomSentenceGenerator = RandomSentenceGenerator()

    fun performRoll() {
        if (isRandomDiceEnabled) {
            numberOfDice = Random.nextInt(MainActivity.MIN_DICE_COUNT, MainActivity.MAX_DICE_COUNT + 1)
            numberOfSides = Random.nextInt(MainActivity.MIN_SIDES_COUNT, MainActivity.MAX_SIDES_COUNT + 1)
        }
        diceResults = rollDice(numberOfDice, numberOfSides)
        randomSentenceText = if (isRandomSentenceEnabled) randomSentenceGenerator.generate() else null
    }

    private fun rollDice(numberOfDice: Int, numberOfSides: Int): List<Int> {
        val validNumberOfDice = numberOfDice.coerceIn(MainActivity.MIN_DICE_COUNT, MainActivity.MAX_DICE_COUNT)
        val validNumberOfSides = numberOfSides.coerceIn(MainActivity.MIN_SIDES_COUNT, MainActivity.MAX_SIDES_COUNT)
        return List(validNumberOfDice) { Random.nextInt(1, validNumberOfSides + 1) }
    }
}
package com.example.letsgogambling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.letsgogambling.ui.theme.LetsGoGamblingTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private lateinit var shakeDetector: ShakeDetector

    companion object {
        const val MIN_DICE_COUNT = 1
        const val MAX_DICE_COUNT = 10

        const val MIN_SIDES_COUNT = 2
        const val MAX_SIDES_COUNT = 100

        val DEFAULT_SPACING_DP = 16.dp
        val DEFAULT_PADDING_DP = 8.dp
        val SLIDER_PADDING_DP = 16.dp
        val TEXT_SIZE_SP = 24.sp
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LetsGoGamblingTheme {
                var diceResults by rememberSaveable { mutableStateOf(emptyList<Int>()) }
                var randomSentenceText by rememberSaveable { mutableStateOf("") }
                var numberOfDice by rememberSaveable { mutableIntStateOf(1) }
                var numberOfSides by rememberSaveable { mutableIntStateOf(20) }
                var isRandomOnShakeEnabled by rememberSaveable { mutableStateOf(false) }
                var isRandomSentenceEnabled by rememberSaveable { mutableStateOf(false) }

                val formattedValues = diceResults.joinToString(", ")
                val randomSentenceGenerator = remember { RandomSentenceGenerator() }

                shakeDetector = ShakeDetector(this) {
                    if (isRandomOnShakeEnabled) {
                        numberOfDice = Random.nextInt(MIN_DICE_COUNT, MAX_DICE_COUNT + 1)
                        numberOfSides = Random.nextInt(MIN_SIDES_COUNT, MAX_SIDES_COUNT + 1)
                    }
                    diceResults = rollDice(numberOfDice, numberOfSides)
                    randomSentenceText = if (isRandomSentenceEnabled) {
                        randomSentenceGenerator.generate()
                    } else ""
                }

                lifecycle.addObserver(shakeDetector)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = randomSentenceText,
                            fontSize = TEXT_SIZE_SP,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(DEFAULT_SPACING_DP))

                        Text(
                            text = formattedValues,
                            fontSize = TEXT_SIZE_SP,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(DEFAULT_SPACING_DP))

                        Text(text = "Number Of Dice : $numberOfDice", textAlign = TextAlign.Center)

                        Slider(
                            value = numberOfDice.toFloat(),
                            onValueChange = { numberOfDice = it.toInt() },
                            valueRange = MIN_DICE_COUNT.toFloat()..MAX_DICE_COUNT.toFloat(),
                            steps = MAX_DICE_COUNT - MIN_DICE_COUNT - 1,
                            modifier = Modifier.padding(horizontal = SLIDER_PADDING_DP)
                        )

                        Text(text = "Number Of Sides : $numberOfSides", textAlign = TextAlign.Center)


                        Slider(
                            value = numberOfSides.toFloat(),
                            onValueChange = { numberOfSides = it.toInt() },
                            valueRange = MIN_SIDES_COUNT.toFloat()..MAX_SIDES_COUNT.toFloat(),
                            steps = MAX_SIDES_COUNT - MIN_SIDES_COUNT - 1,
                            modifier = Modifier.padding(horizontal = SLIDER_PADDING_DP)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = DEFAULT_PADDING_DP)
                        ){
                            val diceButtons = remember {
                                listOf(
                                    DiceButtonConfig(4, R.drawable.dice_d4, "dice_d4"),
                                    DiceButtonConfig(6, R.drawable.dice_d6, "dice_d6"),
                                    DiceButtonConfig(8, R.drawable.dice_d8, "dice_d8"),
                                    DiceButtonConfig(10, R.drawable.dice_d10, "dice_d10"),
                                    DiceButtonConfig(12, R.drawable.dice_d12, "dice_d12"),
                                    DiceButtonConfig(20, R.drawable.dice_d20, "dice_d20")
                                )
                            }

                            diceButtons.forEach { config ->
                                Button(
                                    onClick = { numberOfSides = config.sides },
                                    modifier = Modifier.padding(horizontal = DEFAULT_PADDING_DP)
                                ) {
                                    Image(
                                        painter = painterResource(id = config.imageResId),
                                        contentDescription = config.contentDescription,
                                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(DEFAULT_SPACING_DP))

                        Button(
                            onClick = {
                                diceResults = rollDice(numberOfDice, numberOfSides)
                                randomSentenceText = if (isRandomSentenceEnabled) {
                                    randomSentenceGenerator.generate()
                                } else ""
                            }) {
                            Text("Roll The Dice")
                        }

                        Spacer(modifier = Modifier.height(DEFAULT_SPACING_DP))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Random on shake")
                            Switch(
                                checked = isRandomOnShakeEnabled,
                                onCheckedChange = { isRandomOnShakeEnabled = it },
                                modifier = Modifier.padding(start = DEFAULT_PADDING_DP)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Random sentences")
                            Switch(
                                checked = isRandomSentenceEnabled,
                                onCheckedChange = { isRandomSentenceEnabled = it },
                                modifier = Modifier.padding(start = DEFAULT_PADDING_DP)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun rollDice(numberOfDice: Int, numberOfSides: Int): List<Int> {
    val validNumberOfDice = numberOfDice.coerceIn(MainActivity.MIN_DICE_COUNT, MainActivity.MAX_DICE_COUNT)
    val validNumberOfSides = numberOfSides.coerceAtLeast(MainActivity.MIN_SIDES_COUNT)

    return if (validNumberOfSides < 1) {
        emptyList()
    } else {
        List(validNumberOfDice) { Random.nextInt(1, validNumberOfSides + 1) }
    }
}

data class DiceButtonConfig(
    val sides: Int,
    val imageResId: Int,
    val contentDescription: String
)

class RandomSentenceGenerator {
    private val sentences = listOf(
        "Shit yourself",
        "That's a lot of copium right here",
        "Going to 1v1 God in a fight",
        "Nah I'd win",
        "Is this a gun in your pocket or are you just happy to see me ?",
        "If you're reading this, then I know your IP",
        "If you're reading this, you're a virgin"
    )
    fun generate(): String = sentences.random()
}

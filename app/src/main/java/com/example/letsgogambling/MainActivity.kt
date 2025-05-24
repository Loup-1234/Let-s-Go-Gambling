package com.example.letsgogambling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.letsgogambling.MainActivity.Companion.DEFAULT_PADDING_DP
import com.example.letsgogambling.MainActivity.Companion.DEFAULT_SPACING_DP
import com.example.letsgogambling.ui.theme.LetsGoGamblingTheme

// Main activity for the gambling app
class MainActivity : ComponentActivity() {
    // Shake detector for rolling dice
    private var shakeDetector: ShakeDetector? = null
    // View model for dice logic
    private val diceViewModel: DiceViewModel by viewModels()

    // Companion object for constants
    companion object {
        // Min and max dice count
        const val MIN_DICE_COUNT = 1
        const val MAX_DICE_COUNT = 10

        // Min and max sides count
        const val MIN_SIDES_COUNT = 2
        const val MAX_SIDES_COUNT = 100

        // Default spacing and padding
        val DEFAULT_SPACING_DP = 16.dp
        val DEFAULT_PADDING_DP = 8.dp
        // Default text size
        val TEXT_SIZE_SP = 24.sp
    }

    // Called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge display
        enableEdgeToEdge()
        // Set the content view
        setContent {
            // Apply the app theme
            LetsGoGamblingTheme {
                // Get formatted dice results
                val formattedValues = diceViewModel.diceResults.joinToString(", ")
                // Get current configuration
                val configuration = LocalConfiguration.current

                // Initialize shake detector
                shakeDetector = shakeDetector ?: ShakeDetector(this) {
                    diceViewModel.performRoll()
                }

                shakeDetector?.let { lifecycle.addObserver(it) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Main column layout
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Display random sentence if available
                        diceViewModel.randomSentenceText?.let { sentence ->
                            if (sentence.isNotEmpty()) {
                                Text(
                                    text = sentence,
                                    fontSize = TEXT_SIZE_SP,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(DEFAULT_SPACING_DP))

                        // Display formatted dice results
                        Text(
                            text = formattedValues,
                            fontSize = TEXT_SIZE_SP,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(DEFAULT_SPACING_DP))

                        // Display number of dice
                        Text(
                            text = "Number Of Dice : ${diceViewModel.numberOfDice}",
                            textAlign = TextAlign.Center
                        )

                        Slider(
                            // Slider for number of dice
                            value = diceViewModel.numberOfDice.toFloat(),
                            onValueChange = { diceViewModel.numberOfDice = it.toInt() },
                            valueRange = MIN_DICE_COUNT.toFloat()..MAX_DICE_COUNT.toFloat(),
                            steps = MAX_DICE_COUNT - MIN_DICE_COUNT - 1,
                            modifier = Modifier.padding(
                                horizontal = if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) 64.dp else DEFAULT_SPACING_DP
                            )
                        )

                        // Display number of sides
                        Text(
                            text = "Number Of Sides : ${diceViewModel.numberOfSides}",
                            textAlign = TextAlign.Center
                        )

                        Slider(
                            // Slider for number of sides
                            value = diceViewModel.numberOfSides.toFloat(),
                            onValueChange = { diceViewModel.numberOfSides = it.toInt() },
                            valueRange = MIN_SIDES_COUNT.toFloat()..MAX_SIDES_COUNT.toFloat(),
                            steps = MAX_SIDES_COUNT - MIN_SIDES_COUNT - 1,
                            modifier = Modifier.padding(
                                horizontal = if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) 64.dp else 16.dp
                            )
                        )

                        // Row for dice buttons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = DEFAULT_PADDING_DP)
                        ) {
                            DiceButtonsRow(diceViewModel)
                        }

                        // Choose layout based on orientation
                        if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
                            LandscapeLayout(
                                onRollDice = { diceViewModel.performRoll() },
                                isRandomDiceEnabled = diceViewModel.isRandomDiceEnabled,
                                onRandomDiceChanged = { diceViewModel.isRandomDiceEnabled = it },
                                isRandomSentenceEnabled = diceViewModel.isRandomSentenceEnabled,
                                onRandomSentenceChanged = { diceViewModel.isRandomSentenceEnabled = it }
                            )
                        } else {
                            PortraitLayout(
                                onRollDice = { diceViewModel.performRoll() },
                                isRandomDiceEnabled = diceViewModel.isRandomDiceEnabled,
                                onRandomDiceChanged = { diceViewModel.isRandomDiceEnabled = it },
                                isRandomSentenceEnabled = diceViewModel.isRandomSentenceEnabled,
                                onRandomSentenceChanged = { diceViewModel.isRandomSentenceEnabled = it }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Composable for dice buttons row
@Composable
fun DiceButtonsRow(diceViewModel: DiceViewModel) {
    // Remember dice button configurations
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

    // Create a button for each dice configuration
    diceButtons.forEach { config ->
        Button(
            onClick = { diceViewModel.numberOfSides = config.sides },
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

// Composable for landscape layout
@Composable
fun LandscapeLayout(
    onRollDice: () -> Unit,
    isRandomDiceEnabled: Boolean,
    onRandomDiceChanged: (Boolean) -> Unit,
    isRandomSentenceEnabled: Boolean,
    onRandomSentenceChanged: (Boolean) -> Unit
) {
    // Row for landscape layout
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = onRollDice,
            modifier = Modifier.padding(end = DEFAULT_PADDING_DP)
        ) {
            Text("Roll dices")
        }

        Spacer(modifier = Modifier.width(DEFAULT_SPACING_DP))

        Text("Random dices")

        Switch(
            checked = isRandomDiceEnabled,
            onCheckedChange = onRandomDiceChanged,
            modifier = Modifier.padding(start = DEFAULT_PADDING_DP, end = DEFAULT_PADDING_DP)
        )

        Spacer(modifier = Modifier.width(DEFAULT_SPACING_DP))

        Text("Random sentences")

        Switch(
            checked = isRandomSentenceEnabled,
            onCheckedChange = onRandomSentenceChanged,
            modifier = Modifier.padding(start = DEFAULT_PADDING_DP)
        )
    }
}

// Composable for portrait layout
@Composable
fun PortraitLayout(
    onRollDice: () -> Unit,
    isRandomDiceEnabled: Boolean,
    onRandomDiceChanged: (Boolean) -> Unit,
    isRandomSentenceEnabled: Boolean,
    onRandomSentenceChanged: (Boolean) -> Unit
) {
    Spacer(modifier = Modifier.height(DEFAULT_SPACING_DP))

    // Button to roll the dice
    Button(
        onClick = onRollDice
    ) {
        Text("Roll The Dice")
    }

    // Spacer
    Spacer(modifier = Modifier.height(DEFAULT_SPACING_DP))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Random dices")
        Switch(
            checked = isRandomDiceEnabled,
            onCheckedChange = onRandomDiceChanged,
            modifier = Modifier.padding(start = DEFAULT_PADDING_DP)
        )
    }

    // Spacer
    Spacer(modifier = Modifier.height(DEFAULT_SPACING_DP - 4.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Random sentences")
        Switch(
            checked = isRandomSentenceEnabled,
            onCheckedChange = onRandomSentenceChanged,
            modifier = Modifier.padding(start = DEFAULT_PADDING_DP)
        )
    }
}
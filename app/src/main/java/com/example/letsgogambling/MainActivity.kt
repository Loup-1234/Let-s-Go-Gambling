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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.letsgogambling.ui.theme.LetsGoGamblingTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private lateinit var shakeDetector: ShakeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LetsGoGamblingTheme {
                var diceResults by remember { mutableStateOf(emptyList<Int>()) } // State variable holding the results of the dice rolls, initially empty.
                var numberOfDice by remember { mutableIntStateOf(10) } // State variable for the number of dice, initially 10.
                var numberOfSides by remember { mutableIntStateOf(6) }  // State variable for the number of sides on each die, initially 6.

                shakeDetector = ShakeDetector(this) {
                    numberOfDice = Random.nextInt(1, 10)
                    numberOfSides = Random.nextInt(2, 100)
                    diceResults = rollDice(numberOfDice, numberOfSides)
                }

                lifecycle.addObserver(shakeDetector)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    )  {
                        DiceRollResults(results = diceResults)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Number Of Dice : $numberOfDice")

                        Slider(
                            value = numberOfDice.toFloat(),
                            onValueChange = { numberOfDice = it.toInt() },
                            valueRange = 1f..10f,
                            steps = 8,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )

                        Text(text = "Number Of Sides : $numberOfSides")


                        Slider(
                            value = numberOfSides.toFloat(),
                            onValueChange = { numberOfSides = it.toInt() },
                            valueRange = 2f..100f,
                            steps = 97,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                        ){
                            Button(
                                onClick = { numberOfSides = 4},
                                modifier = Modifier.padding(16.dp,0.dp,8.dp,0.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.dice_d4),
                                    contentDescription = "dice_d4",
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                                )
                            }

                            Button(
                                onClick = { numberOfSides = 6},
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.dice_d6),
                                    contentDescription = "dice_d6",
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                                )
                            }

                            Button(
                                onClick = { numberOfSides = 8},
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.dice_d8),
                                    contentDescription = "dice_d8",
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                                )
                            }

                            Button(
                                onClick = { numberOfSides = 10},
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.dice_d10),
                                    contentDescription = "dice_d10",
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                                )
                            }

                            Button(
                                onClick = { numberOfSides = 12},
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.dice_d12),
                                    contentDescription = "dice_d12",
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                                )
                            }

                            Button(
                                onClick = { numberOfSides = 20},
                                modifier = Modifier.padding(8.dp,0.dp,16.dp,0.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.dice_d20),
                                    contentDescription = "dice_d20",
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = { diceResults = rollDice(numberOfDice, numberOfSides) }) {
                            Text("Roll The Dice")
                        }
                    }
                }
            }
        }
    }
}

fun rollDice(numberOfDice: Int, numberOfSides: Int): List<Int> {
    return try {
        List(numberOfDice) { Random.nextInt(1, numberOfSides + 1) }
    } catch (e: IllegalArgumentException) {
        emptyList()
    }
}

@Composable
fun DiceRollResults(results: List<Int>) {
    val formattedValues = results.joinToString(", ")
    Text(text = formattedValues, fontSize = 24.sp)
}
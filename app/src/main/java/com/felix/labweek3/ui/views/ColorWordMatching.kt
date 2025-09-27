package com.felix.labweek3.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class GameState{
    WELCOME,
    COUNTDOWN,
    GAME,
    GAMEOVER
}

enum class Mode{
    COLOR,
    TEXT
}

enum class ColorName(val color: Color){
    RED(Color.Red),
    GREEN(Color.Green),
    BLUE(Color.Blue),
    ORANGE(Color(0xFFFF8C00))
}

@Composable
fun ColorWordMatching(){
    var gameState by rememberSaveable { mutableStateOf(GameState.WELCOME) }
    var countdown by rememberSaveable { mutableStateOf(5) }
    var countdownRunning by rememberSaveable { mutableStateOf(false) }

    var mode by rememberSaveable { mutableStateOf<Mode?>(null) }
    var questionText by rememberSaveable { mutableStateOf<ColorName?>(null) }
    var questionColor by rememberSaveable { mutableStateOf<ColorName?>(null) }
    var answerOptions by rememberSaveable { mutableStateOf(listOf<ColorName?>(null, null)) }
    var correctAmount by rememberSaveable { mutableStateOf(0) }
    var incorrectAmount by rememberSaveable { mutableStateOf(-1) }
    var highScore by rememberSaveable { mutableStateOf(0) }

    fun getRandomColor():ColorName{
        return ColorName.entries[Random.nextInt(0, ColorName.entries.size)]
    }
    // Get random color with exclude feature, so the options will not duplicated
    fun getRandomColor(exclude: ColorName): ColorName {
        val availableColors = ColorName.entries.filter { it != exclude }
        return availableColors.random()
    }

    // Logic Game
    fun generateQuestion(){
        // random mode
        mode = when(Random.nextInt(0, 2)){
            0-> Mode.COLOR
            else -> Mode.TEXT
        }

        // Create Question and Answer
        questionText = getRandomColor()
        questionColor = getRandomColor()
        answerOptions = answerOptions.toMutableList().apply {
            val randIndex = Random.nextInt(0, 2)
            this[randIndex] = if(mode == Mode.COLOR) questionColor
                else questionText
            this[1-randIndex] = getRandomColor(this[randIndex]!!)
        }

        // start Countdown
        countdown = 5
        countdownRunning = true

    }

    fun strike(){
        if(incorrectAmount>=3){
            gameState = GameState.GAMEOVER
            countdownRunning = false
        }else{
            incorrectAmount++
            generateQuestion()
        }
    }

    // Countdown Engine
    LaunchedEffect(countdownRunning, gameState, incorrectAmount) {
        while(countdown>=0){
            if(countdown == 0){
                delay(700L)
            }else{
                delay(1000L)
            }
            countdown--
        }
        countdownRunning = false

        // if countdown is on starting game
        if(gameState == GameState.COUNTDOWN){
            generateQuestion()
            gameState = GameState.GAME
        }

        // if in game mode
        if(gameState == GameState.GAME){
            strike()
        }
    }

    fun answer(answer:ColorName){
        // COLOR mode
        if(mode == Mode.COLOR){
            if(answer == questionColor){
                correctAmount++
                generateQuestion()
                if(correctAmount > highScore){
                    highScore = correctAmount
                }
            }else{
                strike()
            }
        }
        // TEXT mode
        else{
            if(answer == questionText){
                correctAmount++
                generateQuestion()
                if(correctAmount > highScore){
                    highScore = correctAmount
                }
            }else{
                strike()
            }
        }
    }

    // Logic App Flow
    fun startGame(){
        // reset
        incorrectAmount =-1
        correctAmount=0
        countdown=5

        gameState = GameState.COUNTDOWN
        countdownRunning = true
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3E3E3)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Welcome Text
        if(gameState == GameState.WELCOME){
            Text(
                text = "Welcome\nto\nColor Word Matching",
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = {startGame()},
                colors = ButtonColors(
                    containerColor = Color(0xFFA9BBC0),
                    contentColor = Color.Black,
                    disabledContentColor = Color.Black,
                    disabledContainerColor = Color.Gray
                ),
                modifier = Modifier
                    .padding(top = 20.dp)
            ) {
                Text(
                    text = "Start Game",
                    fontSize = 20.sp
                )
            }
        }

        // Countdown Starting Game
        else if(gameState == GameState.COUNTDOWN){
            Text(
                text = if(countdown>0) countdown.toString()
                else "Start!",
                fontSize = 30.sp
            )
        }
        
        // In Game Mode
        else if(gameState == GameState.GAME){
            // Mode, Correct & Incorrect amount
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .fillMaxWidth(0.8f)
            ) {
                Text(
                    text = "Mode: $mode",
                    fontSize = 20.sp
                )
                Text(
                    text = "✅ $correctAmount   ❌ $incorrectAmount/3",
                    fontSize = 20.sp
                )
            }

            // in game countdown
            Text(
                modifier = Modifier
                    .padding(bottom = 20.dp),
                text = "$countdown s",
                fontSize = 25.sp
            )

            // Question
            Text(
                text = questionText.toString(),
                fontSize = 53.sp,
                color = questionColor!!.color,
                modifier = Modifier
                    .padding(bottom = 100.dp)
            )

            // Answer Button
            Row (
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomBtn(answerOptions[0].toString()) { answer(answerOptions[0]!!) }
                CustomBtn(answerOptions[1].toString()) { answer(answerOptions[1]!!) }
            }
        }

        // Game Over Page
        else if(gameState == GameState.GAMEOVER){
            // Game Over Text
            Text(
                text = "Game Over!",
                fontSize = 40.sp,
                modifier = Modifier
                    .padding(bottom = 90.dp)
            )

            // Youre Score Text
            Text(
                text = "You're Score\n$correctAmount",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(bottom = 20.dp)
            )

            // Best Score
            Text(
                text = "Best Score\n$highScore",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(bottom = 50.dp)
            )

            // Button
            CustomBtn("Restart Game") { startGame() }
            CustomBtn("Exit") { gameState = GameState.WELCOME }

        }

    }
}


@Composable
fun CustomBtn(text: String, onClick: ()->Unit){
    Button(
        modifier = Modifier
            .padding(8.dp),
        onClick = onClick,
        colors = ButtonColors(
            containerColor = Color(0xFFA9BBC0),
            contentColor = Color.Black,
            disabledContentColor = Color.Black,
            disabledContainerColor = Color.Gray,
        ),
        contentPadding = PaddingValues(
            horizontal = 30.dp,
            vertical = 15.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 18.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Preview3(){
    ColorWordMatching()
}

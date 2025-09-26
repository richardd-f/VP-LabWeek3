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
import androidx.compose.runtime.mutableStateOf
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

enum class GameState{
    WELCOME,
    COUNTDOWN,
    GAME,
    GAMEOVER
}

@Composable
fun ColorWordMatching(){
    var countdown by rememberSaveable { mutableStateOf(5) }
    var gameState by rememberSaveable { mutableStateOf(GameState.GAMEOVER) }
    var countdownRunning by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(countdownRunning) {
        while(countdown>=0){
            delay(1000L)
            countdown--
        }
        countdownRunning = false

        // if countdown is on starting game
        if(gameState == GameState.COUNTDOWN){
            gameState = GameState.GAME
        }
    }

    // Logic Flow
    fun startGame(){
        gameState = GameState.COUNTDOWN
        countdown = 5
        countdownRunning = true
    }

    fun exit(){
        // reset all records

        gameState = GameState.WELCOME
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
                    text = "Mode: COLOR",
                    fontSize = 20.sp
                )
                Text(
                    text = "✅ 0   ❌ 0/3",
                    fontSize = 20.sp
                )
            }

            // in game countdown
            Text(
                modifier = Modifier
                    .padding(bottom = 20.dp),
                text = "5 s",
                fontSize = 25.sp
            )

            // Question
            Text(
                text = "RED",
                fontSize = 53.sp,
                color = Color.Blue,
                modifier = Modifier
                    .padding(bottom = 100.dp)
            )

            // Answer Button
            Row (
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomBtn("BLUE") { }
                CustomBtn("RED") { }
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
                text = "You're Score\n5",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(bottom = 20.dp)
            )

            // Best Score
            Text(
                text = "Best Score\n5",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(bottom = 50.dp)
            )

            // Button
            CustomBtn("Restart Game") { startGame() }
            CustomBtn("Exit") { exit() }

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
fun Preview(){
    ColorWordMatching()
}

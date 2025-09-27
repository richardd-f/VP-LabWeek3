package com.felix.labweek3.ui.views

import android.annotation.SuppressLint
import android.graphics.Paint.Align
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class GameView{
    START,
    PICK,
    REVEAL,
    RESULT
}

enum class RPS(val symbol: String, val text: String){
    ROCK(symbol = "✊", text = "✊ Rock"),
    PAPER(symbol = "\uD83D\uDD90\uFE0F", text = "\uD83D\uDD90\uFE0F Paper"),
    SCISSORS(symbol = "✌\uFE0F", text = "✌\uFE0F Scissor")
}

enum class WinState{
    WIN,
    LOSE,
    DRAW
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun RockPaperScissors(){
    var gameView by rememberSaveable { mutableStateOf(GameView.START) }
    var bestOf by rememberSaveable { mutableStateOf(5) }

    var score by rememberSaveable { mutableStateOf(0) }
    var cpuScore by rememberSaveable { mutableStateOf(0) }
    var highScore by rememberSaveable { mutableStateOf(0) }
    var userRPS by rememberSaveable { mutableStateOf<RPS?>(null) }
    var cpuRPS by rememberSaveable { mutableStateOf<RPS?>(null) }
    var winState by rememberSaveable { mutableStateOf<WinState?>(null) }
    var indices by rememberSaveable { mutableStateOf(listOf(0, 1, 2)) }


    fun startGame(){
        cpuRPS = RPS.entries[Random.nextInt(0, 3)]
        indices = indices.shuffled()
        gameView = GameView.PICK
    }
    fun restartGame(){
        score=0
        cpuScore=0
        startGame()
    }

    fun result(action1: RPS, action2: RPS): WinState {
        if (action1 == action2) return WinState.DRAW
        return when (action1) {
            RPS.ROCK -> if (action2 == RPS.SCISSORS) WinState.WIN else WinState.LOSE
            RPS.PAPER -> if (action2 == RPS.ROCK) WinState.WIN else WinState.LOSE
            RPS.SCISSORS -> if (action2 == RPS.PAPER) WinState.WIN else WinState.LOSE
        }
    }

    fun pickAnswer(answer:RPS){
        userRPS = answer
        winState = result(userRPS!!, cpuRPS!!)
        if(winState == WinState.WIN){
            score++
            if(score>highScore) highScore = score
        }
        else cpuScore++

        if(cpuScore>(bestOf/2) || score>(bestOf/2)){
            gameView = GameView.RESULT
        }else{
            gameView = GameView.REVEAL
        }
    }

    LaunchedEffect(gameView) {
        if(gameView == GameView.REVEAL){
            delay(700L)
            startGame()
        }
    }


    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Stats Info
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "\uD83E\uDDD1 $score - $cpuScore \uD83E\uDD16",
                fontSize = 20.sp
            )
            Text(
                text = "Best of $bestOf",
                fontSize = 18.sp
            )
        }

        // GameView.START
        if(gameView == GameView.START){
            // Rock Paper Scissor Text
            Text(
                text = "Rock ● Paper ● Scissors",
                fontSize = 25.sp,
                modifier = Modifier
                    .padding(top = 100.dp, bottom = 50.dp)
            )

            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(bottom = 20.dp)
            ) {
                // decrement bestof
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Decrement Button",
                    modifier = Modifier
                        .background(
                            color = if(bestOf > 3) Color.Black
                            else Color.Gray,
                            shape = RoundedCornerShape(10000.dp)
                        )
                        .padding(8.dp)
                        .clickable { bestOf = (bestOf - 2).coerceAtLeast(3) },
                    tint = Color.White,
                )
                Text(
                    text = "Best of $bestOf",
                    fontSize = 20.sp
                )
                // increment bestof
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Increment Button",
                    modifier = Modifier
                        .background(
                            color = Color.Black,
                            shape = RoundedCornerShape(10000.dp)
                        )
                        .padding(8.dp)
                        .clickable { bestOf+=2 },
                    tint = Color.White
                )
            }

            // Start Button
            Button(
                onClick = {restartGame()},
                modifier = Modifier,
                contentPadding = PaddingValues(horizontal = 100.dp, vertical =15.dp),
                colors = ButtonColors(
                    containerColor = Color(0xFFA9BBC0),
                    contentColor = Color.Black,
                    disabledContentColor = Color.Black,
                    disabledContainerColor = Color.Gray,
                )
            ) {
                Text(
                    text="Start",
                    fontSize = 20.sp
                )
            }
        }

        else if(gameView == GameView.PICK || gameView == GameView.REVEAL){
            // Text: Pick your move
            if(gameView == GameView.PICK){
                Text(
                    text = "Pick your move!",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding( bottom = 20.dp)
                )
            }

            // VS
            Text(
                text = if(gameView == GameView.PICK) "❔ VS ❔"
                else "${userRPS!!.symbol} VS ${cpuRPS!!.symbol}",
                fontSize = 50.sp,
                modifier = Modifier
            )


            // PICK, Button RPS Options
            if(gameView == GameView.PICK){
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Randomized position options
                    indices.forEach { i ->
                        CustomBtn2(RPS.entries[i].text) { pickAnswer(RPS.entries[i]) }
                    }
                }
            }else{
                // REVEAL
                // Draw
                Text(
                    text = if(winState == WinState.WIN) "You Win!"
                    else if (winState == WinState.LOSE) "You Lose!"
                    else "Draw",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                )
            }
        }

        else if (gameView == GameView.RESULT){
            Text(
                text = if(score>cpuScore) "You Win the Match!"
                else "You Lose the Match",
                fontSize = 25.sp
            )
            Text(
                text = "Best score: 3",
                fontSize = 18.sp
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomBtn("Restart") { restartGame() }
                CustomBtn("Exit") { gameView = GameView.START }
            }
        }

    }
}


@Composable
fun CustomBtn2(text: String, onClick: ()->Unit){
    Button(
        onClick = onClick,
        colors = ButtonColors(
            containerColor = Color(0xFFA9BBC0),
            contentColor = Color.Black,
            disabledContentColor = Color.Black,
            disabledContainerColor = Color.Gray,
        ),
        contentPadding = PaddingValues(
            horizontal = 25.dp,
            vertical = 15.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 13.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Preview4(){
    RockPaperScissors()
}

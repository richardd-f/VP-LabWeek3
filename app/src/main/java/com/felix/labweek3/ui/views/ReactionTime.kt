package com.felix.labweek3.ui.views

import com.felix.labweek3.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssistWalker
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class AppState {
    START,
    WAITING,
    GO,
    FINISH,
    FAILED,
    FINAL
}

enum class FinalType{
    SLOW,
    STANDARD,
    MEDIUM,
    FAST
}

@Composable
fun ReactionTime() {
    var state by rememberSaveable { mutableStateOf(AppState.START) }
    val recordsTime = rememberSaveable { mutableStateListOf<Long?>(null, null, null) } // keep as val
    var currentRecordTime by rememberSaveable { mutableStateOf(0L) }
    var trialNumber by rememberSaveable { mutableStateOf(1) }
    var averageTime by rememberSaveable { mutableStateOf(0L) }
    var finalType by rememberSaveable { mutableStateOf<FinalType?>(null) }

    var initialCountdown by rememberSaveable { mutableIntStateOf(0) }
    var countdown by rememberSaveable { mutableStateOf(5000) } // ms
    var running by rememberSaveable { mutableStateOf(false) }
    var startTime by rememberSaveable { mutableStateOf(0L) }
    var goTime by rememberSaveable { mutableStateOf(0L) }
    var stopTime by rememberSaveable { mutableStateOf(0L) }

    LaunchedEffect(running) {
        if (running) {
            startTime = System.currentTimeMillis()
            while (countdown > 0 && running) {
                delay(10L) // smaller steps for smoother countdown
                val elapsed = (System.currentTimeMillis() - startTime).toInt()
                countdown = (initialCountdown - elapsed).coerceAtLeast(0)
            }
            // if running turned false while waiting, bail out
            if (!running) return@LaunchedEffect

            if (countdown <= 0 && state != AppState.GO) {
                state = AppState.GO
                goTime = System.currentTimeMillis()
            }
        }
    }

    fun clickScreen() {
        // debug helper: watch logcat output
        println("clickScreen called -> state=$state trial=$trialNumber running=$running countdown=$countdown")

        when (state) {
            AppState.START -> {
                state = AppState.WAITING
                initialCountdown = Random.nextInt(500, 4500)
                countdown = initialCountdown
                running = true
            }

            AppState.WAITING -> {
                running = false
                state = AppState.FAILED
            }

            AppState.GO -> {
                // user clicked while GO -> stop stopwatch
                running = false
                stopTime = System.currentTimeMillis()
                currentRecordTime = (stopTime - goTime).coerceAtLeast(0L)

                if (trialNumber in 1..3) {
                    recordsTime[trialNumber - 1] = currentRecordTime
                }

                if (trialNumber < 3) {
                    state = AppState.FINISH
                } else {
                    // safe average calculation: only if all 3 values are present
                    val values = recordsTime.filterNotNull()
                    if (values.size == 3) {
                        averageTime = values.sum() / 3
                    } else {
                        // fallback: average of available values (defensive)
                        averageTime = if (values.isNotEmpty()) values.sum() / values.size else 0L
                    }

                    finalType = when {
                        averageTime < 180 -> FinalType.FAST
                        averageTime < 280 -> FinalType.MEDIUM
                        averageTime < 450 -> FinalType.STANDARD
                        else -> FinalType.SLOW
                    }
                    state = AppState.FINAL
                }
            }

            AppState.FINISH -> {
                // go to next trial
                if (trialNumber < 3) trialNumber++
                // reset small bits for next trial
                currentRecordTime = 0L
                countdown = 0
                initialCountdown = 0
                state = AppState.START
            }

            AppState.FAILED -> {
                // just reset for retry
                running = false
                currentRecordTime = 0L
                countdown = 0
                state = AppState.START
            }

            AppState.FINAL -> {
                // reset everything for a fresh run
                trialNumber = 1
                recordsTime.clear()
                recordsTime.addAll(listOf(null, null, null))
                averageTime = 0L
                finalType = null
                currentRecordTime = 0L
                countdown = 0
                running = false
                state = AppState.START
            }
        }

        println("clickScreen done -> state=$state trial=$trialNumber running=$running countdown=$countdown")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = if (state == AppState.START) Color(0xFF6ECDDC)
                else if (state == AppState.WAITING) Color.Gray
                else if (state == AppState.GO) Color.Green
                else if (state == AppState.FINISH) Color.Green
                else if (state == AppState.FAILED) Color.Red
                else if (state == AppState.FINAL && finalType == FinalType.SLOW) Color(0xFFFF3C00)
                else if (state == AppState.FINAL && finalType == FinalType.MEDIUM) Color(0xFFFF9900)
                else if (state == AppState.FINAL && finalType == FinalType.STANDARD) Color(
                    0xFF03A9F4
                )
                else Color(0xFF00FF0B)
            )
            .padding(10.dp)
            .padding(horizontal = 25.dp)
            .clickable {
                clickScreen()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Reaction, Get Ready, Go, Trial Complete
        Text(
            text = if(state == AppState.START) "Reaction"
            else if(state == AppState.WAITING) "Get Ready"
            else if(state == AppState.GO) "GO"
            else if (state == AppState.FAILED) "FAIL!"
            else if (state == AppState.FINISH) "Trial $trialNumber Complete"
            else if (state == AppState.FINAL && finalType == FinalType.SLOW) "YOU LIKE A SNAIL BRO"
            else if (state == AppState.FINAL && finalType == FinalType.STANDARD) "MEH LIKE OTHER PERSON"
            else if (state == AppState.FINAL && finalType == FinalType.MEDIUM) "YOUR REFLEX IS GOOD"
            else "DANG YOU ARE SO FAST BRO!",
            style = headerStyle
        )

        // Logo : Flash, Warning, Walker, Checkcircler,
        if(state != AppState.FINAL){
            Icon(
                imageVector = if(state == AppState.START) Icons.Filled.FlashOn
                else if (state == AppState.WAITING) Icons.Filled.Warning
                else if (state == AppState.GO) Icons.Filled.AssistWalker
                else if (state == AppState.FAILED) Icons.Filled.ThumbDown
                else Icons.Filled.CheckCircle,
                contentDescription = "Flash Logo",
                tint = Color.White,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth()
                    .height(140.dp)
            )
        }else {
            Image(
                painter = painterResource(
                    when (finalType) {
                        FinalType.SLOW -> R.drawable.slow
                        FinalType.STANDARD -> R.drawable.standard
                        FinalType.MEDIUM -> R.drawable.medium
                        else -> R.drawable.fast
                    }
                ),
                contentDescription = "Final Logo",
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth()
                    .height(140.dp)
            )
        }


        // Text: Test, Wait for green light, CLICK NOW, Time...ms
        Text(
            text = if(state == AppState.START) "Test"
            else if(state == AppState.WAITING) "Wait for green light..."
            else if(state == AppState.GO) "CLICK NOW!!!"
            else if(state == AppState.FAILED) "You clicked to early, TRY TO READ THE RULES BRO"
            else if(state == AppState.FINISH) "Time :  ${currentRecordTime}ms"
            else "Average : ${averageTime}ms",
            style = bodyStyle,
            modifier = Modifier
                .padding(bottom = 30.dp)
        )

        // Text: Click to start, DONT CLICK YET, TAP AS FAST AS YOU CAN, Continue to trial
        Text(
            text = if (state == AppState.START) "Click to Start"
            else if(state == AppState.WAITING) "DON'T CLICK YET!"
            else if (state == AppState.GO) "TAP AS FAST AS YOU CAN!"
            else if (state == AppState.FAILED) "TRY AGAIN"
            else if (state == AppState.FINISH && trialNumber<3) "Continue to trial ${trialNumber + 1}"
            else "Click to Start New Test",
            style = bodyStyle
        )

        // Box Trial Results
        if(state == AppState.FINISH || state == AppState.FAILED || state == AppState.FINAL){
            Column(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxWidth(0.6f)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10.dp),
                        clip = true
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Trial Results",
                    color = Color(0xFF0088FF),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(bottom = 13.dp)
                )
                Row {
                    TrialResultParts(1, recordsTime[0])
                    TrialResultParts(2, recordsTime[1])
                    TrialResultParts(3, recordsTime[2])
                }

                if(state == AppState.FINAL){
                    Text(
                        text = "Average Score",
                        color = Color(0xFF0088FF),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(top = 13.dp)
                    )
                    Text(
                        text = "${averageTime}ms",
                        color = Color(0xFFFF5722),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                }
            }
        }

    }

}

@Composable
fun TrialResultParts(order:Int , recordTime:Long?){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = order.toString(),
            fontSize = 18.sp,
            color = if(recordTime == null) Color.Gray
            else Color.Green,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(bottom = 3.dp)
        )
        val text = if(recordTime != null) (recordTime.toString()+"ms")
        else "-"
        Text(text)
    }
}

val headerStyle= TextStyle(
    fontSize = 30.sp,
    fontWeight = FontWeight.SemiBold,
    color = Color.White
)
val bodyStyle= TextStyle(
    fontSize = 20.sp,
    fontWeight = FontWeight.SemiBold,
    color = Color.White
)

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun Preview(){
//    ReactionTime()
//}

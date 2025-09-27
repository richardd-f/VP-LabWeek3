package com.felix.labweek3.ui.views

import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felix.labweek3.R
import java.text.DecimalFormat

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClickerGame(){
    val numberFormat = DecimalFormat("#.##")
    var coins by rememberSaveable { mutableStateOf<Float>(0f) }
    var addAmount by rememberSaveable { mutableStateOf<Float>(1f) }
    var upgradePrice by rememberSaveable { mutableStateOf<Float>(10f) }
    var isMouthOpen by rememberSaveable { mutableStateOf(false) }
    var coinNeededToUpgrade = (upgradePrice - coins).coerceAtLeast(0f)

    fun upgradeValue(){
        coins -= upgradePrice
        addAmount *= 1.5f
        upgradePrice *= 2
    }
    fun onClickImage(){
        isMouthOpen = true
        coins += addAmount
    }
    fun onReleaseImage(){
        isMouthOpen = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Image(
            painter = painterResource(R.drawable.s2bg),
            contentDescription = "bg image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // Main Column for Content
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Box Your coins
            Column (
                modifier = Modifier
                    .padding(bottom = 50.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Coins",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 25.sp,
                    color = Color.White
                ) // Your coins
                Text(
                    text = numberFormat.format(coins),
                    color = Color(0xFF00FF2A),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                ) // 0 (coin amount)
                Text(
                    text = "${numberFormat.format(addAmount)} coins per tap",
                    color = Color.White,
                    fontSize = 18.sp
                ) // 1 coins per tap
            }

            // Tap the cat
            Text(
                text = "Tap the cat!",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold
            )
            Image(
                painter = painterResource(
                    if(!isMouthOpen) R.drawable.s2kucingtutup
                    else R.drawable.s2kucingbuka
                ),
                contentDescription = "Closed mouth of cat",
                modifier = Modifier
                    .padding(top = 15.dp, bottom = 10.dp)
                    .width(200.dp)
                    .height(200.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .pointerInteropFilter { motionEvent ->
                        when (motionEvent.action) {
                            MotionEvent.ACTION_DOWN -> {
                                onClickImage()
                                true
                            }
                            MotionEvent.ACTION_UP -> {
                                // 👉 User released
                                onReleaseImage()
                                true
                            }
                            else -> false
                    }
                }
            )

            // Purr or Meow
            Text(
                text = if(!isMouthOpen) "Purr~~"
                else "Meoww~~~",
                color = Color.White,
                fontSize = 18.sp
            )

            // White Box
            Column (
                modifier = Modifier
                    .padding(top = 30.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text =" Give Me Your Coin",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Next upgrade: +${numberFormat.format(addAmount*1.5)} coins per tap",
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 8.dp)
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonColors(
                        containerColor = Color(0xFF00D045),
                        contentColor = Color.White,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray
                    ),
                    enabled = !(coinNeededToUpgrade>0),
                    onClick = {upgradeValue()}
                ) {
                    Text(
                        text = if(coinNeededToUpgrade>0) "Find ${numberFormat.format(coinNeededToUpgrade)} more coins to upgrade"
                        else "Pay for ${numberFormat.format(upgradePrice)} coins"
                    )
                }

            }

        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Preview2(){
    ClickerGame()
}

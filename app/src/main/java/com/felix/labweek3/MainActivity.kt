package com.felix.labweek3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.felix.labweek3.ui.theme.LabWeek3Theme
import com.felix.labweek3.ui.views.ClickerGame
import com.felix.labweek3.ui.views.ColorWordMatching
import com.felix.labweek3.ui.views.ReactionTime
import com.felix.labweek3.ui.views.RockPaperScissors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabWeek3Theme {
                window.statusBarColor = android.graphics.Color.WHITE
//                ReactionTime()
//                ClickerGame()
//                ColorWordMatching()
                RockPaperScissors()
            }
        }
    }
}
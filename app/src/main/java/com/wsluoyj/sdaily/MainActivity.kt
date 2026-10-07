package com.wsluoyj.sdaily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wsluoyj.sdaily.ui.MainScaffold
import com.wsluoyj.sdaily.ui.theme.SdailyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SdailyTheme {
                MainScaffold()
            }
        }
    }
}

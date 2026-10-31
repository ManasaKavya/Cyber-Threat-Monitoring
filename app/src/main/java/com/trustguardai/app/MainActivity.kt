
package com.trustguardai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.trustguardai.app.data.api.RetrofitClient
import com.trustguardai.app.navigation.AppNavigation
import com.trustguardai.app.ui.theme.TrustGuardAITheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(this)

        setContent {
            TrustGuardAITheme {
                AppNavigation()
            }
        }
    }
}
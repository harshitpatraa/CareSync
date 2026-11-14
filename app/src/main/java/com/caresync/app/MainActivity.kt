package com.caresync.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.caresync.app.navigation.CareSyncNavHost
import com.caresync.app.network.configureAppCheck
import com.caresync.app.theme.CareSyncTheme
import com.caresync.app.utils.LocalCareSyncStrings
import com.caresync.app.utils.stringsForLanguage
import com.caresync.app.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure App Check with launch intent before any UI, ViewModels, or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            val authViewModel: AuthViewModel = hiltViewModel()
            val prefs by authViewModel.preferences.collectAsStateWithLifecycle()
            CompositionLocalProvider(LocalCareSyncStrings provides stringsForLanguage(prefs.language)) {
                CareSyncTheme(darkTheme = prefs.isDarkMode) {
                    CareSyncNavHost(authViewModel = authViewModel)
                }
            }
        }
    }
}

package com.earldev.self_update

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.earldev.app_update.UpdateStep
import com.earldev.appupdater.BuildConfig
import com.earldev.self_update.compose.MainScreen
import com.earldev.self_update.compose.NoPermissionAlert
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity() {

    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initAppUpdater(this)

        setContent {
            val screenState: ScreenState by viewModel.screenStateFlow.collectAsState()
            val updateStep: UpdateStep? by viewModel.updateStepFlow.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.updateCancelledTrigger.collectLatest {
                    Toast.makeText(this@MainActivity, "You have cancelled the installation", Toast.LENGTH_SHORT).show()
                }
            }

            MainScreen(
                versionName = BuildConfig.VERSION_NAME,
                versionCode = BuildConfig.VERSION_CODE,
                updateStep = updateStep,
                updateAvailable = screenState.updateAvailable,
                loading = screenState.loading,
                onCheckUpdateButtonClick = viewModel::onCheckUpdateClick,
                onStartUpdateButtonClick = viewModel::onStartUpdateClick,
                onStopUpdateButtonClick = viewModel::onStopUpdateClick
            )

            if (screenState.noPermissionAlert) {
                NoPermissionAlert(
                    onAgreedToGivePermission = viewModel::onAgreedToGivePermission,
                    onDeclinedToGivePermission = viewModel::onDeclinedToGivePermission
                )
            }
        }
    }
}


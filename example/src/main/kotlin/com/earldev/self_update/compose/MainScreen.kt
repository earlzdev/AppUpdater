package com.earldev.self_update.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earldev.app_update.UpdateStep

@Composable
fun MainScreen(
    versionCode: Int,
    versionName: String,
    loading: Boolean,
    updateStep: UpdateStep?,
    updateAvailable: Boolean?,
    onCheckUpdateButtonClick: () -> Unit,
    onStartUpdateButtonClick: () -> Unit,
    onStopUpdateButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                VersionText(
                    text = "Current version code: $versionCode",
                )
                VersionText(
                    text = "Current version name: $versionName",
                )
                Spacer(modifier = Modifier.height(20.dp))
                updateAvailable?.let {
                    Text(
                        text = "Update available -> $it"
                    )
                }
                updateStep?.let {
                    Text(
                        text = "\nUpdate step\n$it",
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                contentAlignment = Alignment.Center
            ) {
                if (loading) {
                    CircularProgressIndicator()
                }
            }

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Button(onClick = onCheckUpdateButtonClick) {
                    Text(
                        text = "Is update available?"
                    )
                }
                Button(onClick = onStartUpdateButtonClick) {
                    Text(
                        text = "Start update"
                    )
                }
                Button(onClick = onStopUpdateButtonClick) {
                    Text(
                        text = "Stop update"
                    )
                }
            }

        }
    }
}

@Composable
private fun VersionText(
    text: String
) {
    Text(
        text = text,
        fontSize = 22.sp,
    )
}

@Preview
@Composable
private fun Preview() {
    MainScreen(
        versionName = "1.0",
        versionCode = 1,
        loading = true,
        updateStep = UpdateStep.InstallApk(),
        updateAvailable = true,
        onCheckUpdateButtonClick = {},
        onStartUpdateButtonClick = {},
        onStopUpdateButtonClick = {}
    )
}

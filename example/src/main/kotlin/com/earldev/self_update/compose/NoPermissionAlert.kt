package com.earldev.self_update.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun NoPermissionAlert(
    onAgreedToGivePermission: () -> Unit,
    onDeclinedToGivePermission: () -> Unit
) {
    Dialog(
        onDismissRequest = {}
    ) {
        NoPermissionCard(
            onAgreedToGivePermission = onAgreedToGivePermission,
            onDeclinedToGivePermission = onDeclinedToGivePermission
        )
    }
}

@Composable
fun NoPermissionCard(
    onAgreedToGivePermission: () -> Unit,
    onDeclinedToGivePermission: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Attention",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Need permission for install packages",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 16.dp)
            )

            Row {
                Button(
                    onClick = onAgreedToGivePermission
                ) {
                    Text("Agree")
                }
                Spacer(modifier = Modifier.width(30.dp))
                Button(
                    onClick = onDeclinedToGivePermission
                ) {
                    Text("Decline")
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
@Composable
private fun Preview() {
    NoPermissionAlert(
        onAgreedToGivePermission = {},
        onDeclinedToGivePermission = {}
    )
}
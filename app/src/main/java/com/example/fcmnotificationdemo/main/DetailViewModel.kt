package com.example.fcmnotificationdemo.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel


class DetailViewModel: ViewModel() {
    private var count = 0

    fun setUpData(count: Int) {
        this.count = count
    }

    @Composable
    fun Run(
        navigateUp: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = navigateUp) {
                Text("Back")
            }
            Spacer(modifier = Modifier.height(50.dp))

            Text("Data Received: ${count}")

        }

    }
}
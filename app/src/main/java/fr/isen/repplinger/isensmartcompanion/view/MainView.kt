package fr.isen.repplinger.isensmartcompanion.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.isen.repplinger.isensmartcompanion.models.QAHistory
import fr.isen.repplinger.isensmartcompanion.services.qa.QAHistoryDao
import fr.isen.repplinger.isensmartcompanion.R
import fr.isen.repplinger.isensmartcompanion.models.askGeminiAi
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun MainScreen(qaHistoryDao: QAHistoryDao, modifier : Modifier) {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    var inputHistory by remember { mutableStateOf(listOf<String>()) }
    var aiResponses by remember { mutableStateOf(listOf<String>()) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier.fillMaxSize().padding(top = 16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.la_mere_patriev3),
                contentDescription = "La mère patrie",
                modifier = Modifier
                    .size(100.dp)
            )
            Text(
                text = "ISEN Smart Compagnion",
                modifier = Modifier.padding(16.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    inputHistory.forEachIndexed { index, input ->
                        Text(
                            text = "You: $input",
                            modifier = Modifier.padding(16.dp)
                        )
                        if (index < aiResponses.size) {
                            Text(
                                text = "Gemini: ${aiResponses[index]}",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Enter your prompt") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 100.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            Log.i("MainPage", "Send: $text")
                            Toast.makeText(context, "Send: $text", Toast.LENGTH_LONG).show()
                            inputHistory = inputHistory + text
                            coroutineScope.launch {
                                val response = askGeminiAi(text)
                                aiResponses = aiResponses + response

                                val qaHistory = QAHistory(
                                    question = text,
                                    answer = response,
                                    date = Date()
                                )
                                try {
                                    qaHistoryDao.insert(qaHistory)
                                } catch (e: Exception) {
                                    Log.e("MainPage", "Error inserting QAHistory: ${e.message}")
                                }
                                text = ""
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFCD0000), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = "Send",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        }
    }
}
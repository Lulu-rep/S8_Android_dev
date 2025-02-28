package fr.isen.repplinger.isensmartcompanion.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.isen.repplinger.isensmartcompanion.services.qa.AppDatabase
import fr.isen.repplinger.isensmartcompanion.models.QAHistory
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(modifier : Modifier) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val qaHistoryDao = database.qaHistoryDao()
    var history by remember { mutableStateOf<List<QAHistory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            history = qaHistoryDao.getAll()
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "An error occurred: ${e.message}"
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "History Screen", modifier = Modifier.padding(16.dp))
        if (isLoading) {
            Text(text = "Loading...")
        } else if (errorMessage != null) {
            Text(text = "Error: $errorMessage")
        } else {
            Button(
                onClick = {
                    coroutineScope.launch {
                        qaHistoryDao.deleteAll()
                        history = emptyList()
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Delete All")
            }
            LazyColumn(
                modifier = Modifier.padding(bottom = 64.dp)
            ) {
                items(history) { qaHistory ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(text = "Date: ${qaHistory.date}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Question: ${qaHistory.question}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Answer: ${qaHistory.answer}", style = MaterialTheme.typography.bodyMedium)
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        qaHistoryDao.delete(qaHistory)
                                        history = qaHistoryDao.getAll()
                                    }
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}
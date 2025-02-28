package fr.isen.repplinger.isensmartcompanion.view

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.isen.repplinger.isensmartcompanion.EventDetailActivity
import fr.isen.repplinger.isensmartcompanion.models.EventModel
import fr.isen.repplinger.isensmartcompanion.services.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EventsScreen(modifier: Modifier) {
    var events by remember { mutableStateOf<List<EventModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val call = RetrofitInstance.api.getEvents()
        call.enqueue(object : Callback<List<EventModel>> {
            override fun onResponse(
                call: Call<List<EventModel>>,
                response: Response<List<EventModel>>
            ) {
                if (response.isSuccessful) {
                    events = response.body() ?: emptyList()
                    isLoading = false
                } else {
                    errorMessage = "An error occurred: ${response.errorBody().toString()}"
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<List<EventModel>>, t: Throwable) {
                errorMessage = "An error occurred: ${t.message}"
                isLoading = false
            }
        })
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Events Screen", modifier = Modifier.padding(16.dp))
        if (isLoading) {
            Text(text = "Loading...")
        } else if (errorMessage != null) {
            Text(text = "Error: $errorMessage")
        }
        LazyColumn(
            modifier = Modifier.padding(bottom = 64.dp)
        ) {
            items(events) { event ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            val intent = Intent(context, EventDetailActivity::class.java).apply {
                                putExtra("event", event)
                            }
                            context.startActivity(intent)
                        }
                ) {
                    EventItem(event)
                }
            }
        }
    }
}

@Composable
fun EventItem(event: EventModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = event.title, style = MaterialTheme.typography.titleLarge)
            Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = event.date, style = MaterialTheme.typography.bodyMedium)
            Text(text = event.location, style = MaterialTheme.typography.bodyMedium)
            Text(text = event.category, style = MaterialTheme.typography.bodyMedium)
        }
    }
}



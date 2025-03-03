package fr.isen.repplinger.isensmartcompanion.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.google.gson.Gson
import fr.isen.repplinger.isensmartcompanion.EventDetailActivity
import fr.isen.repplinger.isensmartcompanion.models.EventModel
import fr.isen.repplinger.isensmartcompanion.services.notification.sendNotification
import fr.isen.repplinger.isensmartcompanion.services.retrofit.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EventsScreen(modifier: Modifier) {
    var events by remember { mutableStateOf<List<EventModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("event_prefs", Context.MODE_PRIVATE)

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
                    EventItem(event, sharedPreferences)
                }
            }
        }
    }
}

@Composable
fun EventItem(event: EventModel, sharedPreferences: SharedPreferences) {
    val context = LocalContext.current
    var isPinned by remember { mutableStateOf(sharedPreferences.getBoolean(event.id, false)) }
    val coroutineScope = rememberCoroutineScope()

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = event.title, style = MaterialTheme.typography.titleLarge)
                Icon(
                    imageVector = if (isPinned) Icons.Default.Clear else Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable {
                            isPinned = !isPinned
                            event.isPinned = isPinned
                            savePinnedState(sharedPreferences, event)
                            if (isPinned) {
                                coroutineScope.launch {
                                    delay(100)
                                    sendNotification(context, event)
                                }
                            }
                        }
                        .padding(8.dp)
                )
            }
            Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = event.date, style = MaterialTheme.typography.bodyMedium)
            Text(text = event.location, style = MaterialTheme.typography.bodyMedium)
            Text(text = event.category, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@SuppressLint("MutatingSharedPrefs")
fun savePinnedState(sharedPreferences: SharedPreferences, event: EventModel) {
    with(sharedPreferences.edit()) {
        putBoolean(event.id, event.isPinned)
        if (event.isPinned) {
            val eventJson = Gson().toJson(event)
            putString(event.id + "_json", eventJson)
            val eventIds = sharedPreferences.getStringSet("attending_events", mutableSetOf()) ?: mutableSetOf()
            eventIds.add(event.id)
            putStringSet("attending_events", eventIds)
        } else {
            remove(event.id + "_json")
            val eventIds = sharedPreferences.getStringSet("attending_events", mutableSetOf()) ?: mutableSetOf()
            eventIds.remove(event.id)
            putStringSet("attending_events", eventIds)
        }
        apply()
    }
}


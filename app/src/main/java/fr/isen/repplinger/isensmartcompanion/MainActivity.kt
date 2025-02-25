package fr.isen.repplinger.isensmartcompanion

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.ai.client.generativeai.GenerativeModel
import fr.isen.repplinger.isensmartcompanion.retrofit.RetrofitInstance
import fr.isen.repplinger.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ISENSmartCompanionTheme {
                Scaffold(modifier = Modifier.fillMaxSize())
                {
                    MainPage()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainPage() {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomNavigationBar(navController) }
    ) {
        NavigationHost(navController)
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val items = listOf("home", "events", "history")
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        "home" -> Icon(Icons.Default.Home, contentDescription = null)
                        "events" -> Icon(Icons.Rounded.DateRange, contentDescription = null)
                        "history" -> Icon(Icons.Rounded.Menu, contentDescription = null)
                        else -> Icon(Icons.Default.Clear, contentDescription = null)
                    }
                },
                label = { Text(screen.replaceFirstChar { it.uppercaseChar() }) },
                selected = false,
                onClick = {
                    navController.navigate(screen)
                }
            )
        }
    }
}

@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("home") { MainScreen() }
        composable("events") { EventsScreen() }
        composable("history") { HistoryScreen() }
    }
}

@Composable
fun EventsScreen() {
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
        modifier = Modifier
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

@Composable
fun HistoryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "History Screen", modifier = Modifier.padding(16.dp))
    }
}


@Composable
fun MainScreen() {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    var inputHistory by remember { mutableStateOf(listOf<String>()) }
    var aiResponses by remember { mutableStateOf(listOf<String>()) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
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

suspend fun askGeminiAi(question: String): String {
    return try {
    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyAwCohjvlVfgydfqpSJjL9F3QE_x4g01H4"
    )
    Log.i("MainPage", "Ask Gemini AI: $question")
    val response = generativeModel.generateContent(question)
        response.text.toString()
    } catch (e: Exception) {
        Log.e("MainPage", "Error asking Gemini AI: ${e.message}")
        "Error: ${e.message}"
    }
}

@Preview(showBackground = true)
@Composable
fun Main_pagePreview() {
    ISENSmartCompanionTheme {
        MainPage()
    }
}
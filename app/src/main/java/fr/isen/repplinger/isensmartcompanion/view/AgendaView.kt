package fr.isen.repplinger.isensmartcompanion.view

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.isen.repplinger.isensmartcompanion.models.EventModel
import java.io.InputStreamReader
import java.util.Date

data class Course(
    val courseName: String,
    val courseTime: String,
    val courseLocation: String,
    val date: String
)

@Composable
fun AgendaScreen(modifier: Modifier) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("event_prefs", Context.MODE_PRIVATE)
    val courses = loadCourses(context)
    val events = loadAttendingEvents(sharedPreferences)

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Agenda", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
        LazyColumn {
            item {
                Text(text = "Cours", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(courses) { course ->
                CourseItem(course)
            }
            item {
                Text(text = "Événements", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(events) { event ->
                EventItem(event, sharedPreferences)
            }
        }
    }
}

@Composable
fun CourseItem(course: Course) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = course.courseName, style = MaterialTheme.typography.titleLarge)
            Text(text = course.courseTime, style = MaterialTheme.typography.bodyMedium)
            Text(text = course.courseLocation, style = MaterialTheme.typography.bodyMedium)
            Text(text = course.date, style = MaterialTheme.typography.bodyMedium)
        }
    }
}


fun loadCourses(context: Context): List<Course> {
    val inputStream = context.assets.open("courses.json")
    val reader = InputStreamReader(inputStream)
    val courseType = object : TypeToken<List<Course>>() {}.type
    return Gson().fromJson(reader, courseType)
}

fun loadAttendingEvents(sharedPreferences: SharedPreferences): List<EventModel> {
    val events = mutableListOf<EventModel>()
    val eventIds = sharedPreferences.getStringSet("attending_events", setOf()) ?: setOf()
    for (eventId in eventIds) {
        val eventJson = sharedPreferences.getString(eventId + "_json", null)
        if (eventJson != null) {
            events.add(Gson().fromJson(eventJson, EventModel::class.java))
        }
    }
    return events
}

fun findClosestEventOrCourse(courses: List<Course>, events: List<EventModel>): Any? {
    val allItems = courses.map { it.date to it } + events.map { it.date to it }
    val closestItem = allItems.minByOrNull { (date, _) -> Date(date).time }
    return closestItem?.second
}
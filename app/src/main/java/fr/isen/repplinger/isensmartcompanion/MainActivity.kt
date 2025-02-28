package fr.isen.repplinger.isensmartcompanion

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.repplinger.isensmartcompanion.services.qa.AppDatabase
import fr.isen.repplinger.isensmartcompanion.services.qa.QAHistoryDao
import fr.isen.repplinger.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import fr.isen.repplinger.isensmartcompanion.view.EventsScreen
import fr.isen.repplinger.isensmartcompanion.view.HistoryScreen
import fr.isen.repplinger.isensmartcompanion.view.MainScreen

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var qaHistoryDao: QAHistoryDao

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(this)
        qaHistoryDao = database.qaHistoryDao()
        setContent {
            val navController = rememberNavController()
            ISENSmartCompanionTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = "home",
                    ) {
                        composable("home") {
                            MainScreen(
                                qaHistoryDao,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable("events") { EventsScreen(modifier = Modifier.padding(innerPadding)) }
                        composable("history") {
                            HistoryScreen(
                                modifier = Modifier.padding(
                                    innerPadding
                                )
                            )
                        }
                    }
                }
            }
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
}
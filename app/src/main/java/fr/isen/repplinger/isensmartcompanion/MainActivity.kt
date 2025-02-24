package fr.isen.repplinger.isensmartcompanion

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.repplinger.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ISENSmartCompanionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MainPage()
                }
            }
        }
    }
}

@Composable
fun MainPage() {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    var response by remember { mutableStateOf("") }
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Image(
            painter = painterResource(id = R.drawable.la_mere_patriev3),
            contentDescription = "La mère patrie",
            modifier = Modifier
                .size(200.dp)
                .padding(top = 64.dp)
        )
        Text(
            text = "ISEN Smart Compagnion",
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Response :  $response",
            modifier = Modifier.padding(top = 64.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter your prompt")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 100.dp),
            trailingIcon = {
                IconButton(
                    onClick = {
                        Log.i("MainPage", "Send: $text")
                        Toast.makeText(context, "Send: $text", Toast.LENGTH_SHORT).show()
                        response = text
                        text = ""
                    }
                ) {
                    Box (
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

@Preview(showBackground = true)
@Composable
fun Main_pagePreview() {
    ISENSmartCompanionTheme {
        MainPage()
    }
}
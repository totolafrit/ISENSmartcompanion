package fr.isen.IMPROTA.isensmartcompanion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.isen.IMPROTA.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import fr.isen.IMPROTA.isensmartcompanion.data.Event


class EventDetailActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val event = intent.getParcelableExtra<Event>("event")
        setContent {
            ISENSmartCompanionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (event != null) {
                        EventDetailScreen(event = event)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(event: Event) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("event_prefs", Context.MODE_PRIVATE)
    val isNotifiedKey = "notified_${event.id}"
    var isNotified by remember {
        mutableStateOf(sharedPreferences.getBoolean(isNotifiedKey, false))
    }

    // Create the notification channel
    createNotificationChannel(context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = { (context as? EventDetailActivity)?.finish() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isNotified = !isNotified
                        with(sharedPreferences.edit()) {
                            putBoolean(isNotifiedKey, isNotified)
                            apply()
                        }
                        if (isNotified) {
                            // Schedule the notification
                            scheduleNotification(context, event)
                        }
                    }) {
                        Icon(
                            imageVector = if (isNotified) Icons.Filled.Notifications else Icons.Filled.NotificationsOff,
                            contentDescription = "Toggle Notification",
                            tint = if (isNotified) Color.White else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Red,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Date:   ${event.date}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Category:   ${event.category}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Description:  " + event.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Event ID:   ${event.id}",
                        style = MaterialTheme.typography.titleMedium
                    )

                }
            }
        }
    }
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Event Notifications"
        val descriptionText = "Notifications for upcoming events"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("event_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}


private fun scheduleNotification(context: Context, event: Event) {
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
        sendNotification(context, event)
    }, 10000) // 10 seconds delay
}


private fun sendNotification(context: Context, event: Event) {
    // NotificationBuilder to construct the notification
    val builder = NotificationCompat.Builder(context, "event_channel")
        .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your own icon
        .setContentTitle("Event Reminder: ${event.title}")
        .setContentText("Don't forget about ${event.title} on ${event.date}!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true) // Automatically dismisses the notification when tapped
        .setDefaults(NotificationCompat.DEFAULT_ALL) // Default sound, vibration, etc.

    // Send the notification using NotificationManagerCompat
    with(NotificationManagerCompat.from(context)) {
        // event.id.hashCode() is used to ensure a unique ID for each notification
        notify(event.id.hashCode(), builder.build())
    }
}

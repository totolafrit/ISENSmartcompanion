package fr.isen.IMPROTA.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import fr.isen.IMPROTA.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.material.icons.filled.CalendarToday
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Brush
import fr.isen.IMPROTA.isensmartcompanion.data.AppDatabase
import fr.isen.IMPROTA.isensmartcompanion.data.Chat



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ISENSmartCompanionTheme {
                MainScreen()
            }
        }
    }

    data class Event(
        val id: String = "",
        val name: String = "",
        val description: String = "",
        val date: String = "",
        val location: String = "",
        val category: String = ""
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: ""
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(name)
            parcel.writeString(description)
            parcel.writeString(date)
            parcel.writeString(location)
            parcel.writeString(category)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Event> {
            override fun createFromParcel(parcel: Parcel): Event {
                return Event(parcel)
            }

            override fun newArray(size: Int): Array<Event?> {
                return arrayOfNulls(size)
            }
        }
    }

    sealed class Screen(val route: String, val name: String) {
        object Home : Screen("home", "Home")
        object Event : Screen("event", "Event")
        object History : Screen("history", "History")
        object Agenda : Screen("agenda", "Agenda") // Ajout de l'Agenda
    }


    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        val tabBarItems = listOf(Screen.Home, Screen.Event, Screen.History, Screen.Agenda) // Ajout de l'Agenda

        Scaffold(
            bottomBar = { TabView(tabBarItems, navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) { HomeScreen(navController) }
                composable(Screen.Event.route) { EventScreen(navController) }
                composable(Screen.History.route) { HistoryScreen(navController) }
                composable(Screen.Agenda.route) { AgendaScreen(navController) } // Ajout de l'Agenda
            }
        }
    }


    @Composable
    fun TabView(tabBarItems: List<Screen>, navController: NavController) {
        var selectedScreen by remember { mutableStateOf<Screen>(Screen.Home) }

        NavigationBar {
            tabBarItems.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        when (screen) {
                            Screen.Home -> Icon(Icons.Filled.Home, contentDescription = screen.name)
                            Screen.Event -> Icon(Icons.Filled.DateRange, contentDescription = screen.name)
                            Screen.History -> Icon(Icons.Filled.History, contentDescription = screen.name)
                            Screen.Agenda -> Icon(Icons.Filled.CalendarToday, contentDescription = screen.name) // Ajout de l'Agenda
                        }
                    },
                    label = { Text(screen.name) },
                    selected = selectedScreen == screen,
                    onClick = {
                        selectedScreen = screen
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }


    @Composable
    fun HomeScreen(navController: NavController) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header()
            Spacer(modifier = Modifier.height(16.dp))
            ChatSection()
        }
    }


    @Composable
    fun Header(modifier: Modifier = Modifier) {
        val context = LocalContext.current

        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.isenlogo),
                        contentDescription = context.getString(R.string.app_name),
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "ISEN Smart Companion",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Votre assistant intelligent pour l'ISEN",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }


    @Composable
    fun ChatSection() {
        val context = LocalContext.current
        val geminiManager = remember { GeminiManager(context) }
        var userQuery by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        var chatHistory by remember { mutableStateOf<List<Pair<String, Boolean>>>(emptyList()) }

        // Obtenez l'instance de la base de données
        val db = AppDatabase.getDatabase(context)
        val chatDao = db.chatDao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome On GEMINISEN",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true
                ) {
                    items(chatHistory.reversed()) { (message, isUser) ->
                        ChatBubble(message, isUser)
                    }
                }
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            ChatInputField(onSend = { query ->
                userQuery = query
                isLoading = true
                chatHistory = chatHistory + (query to true)

                // Sauvegarder la question dans la base de données
                scope.launch {
                    // Récupérer la réponse et mettre à jour l'historique
                    geminiManager.generateContent(query).collect { response ->
                        isLoading = false
                        val newResponse = response.text ?: "Pas de réponse."
                        chatHistory = chatHistory + (newResponse to false)

                        // Mettre à jour la réponse dans la base de données
                        val updatedChat = Chat(question = query, answer = newResponse)
                        chatDao.insertChat(updatedChat)
                    }
                }
            })
        }
    }


    @Composable
    fun ChatBubble(message: String, isUser: Boolean) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .widthIn(min = 80.dp, max = 280.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }


    @Composable
    fun ChatInputField(onSend: (String) -> Unit) {
        var textState by remember { mutableStateOf(TextFieldValue("")) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                placeholder = { Text("Pose-moi une question...") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    onSend(textState.text)
                    textState = TextFieldValue("")
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    onSend(textState.text)
                    textState = TextFieldValue("")
                },
                modifier = Modifier.height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Envoyer",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

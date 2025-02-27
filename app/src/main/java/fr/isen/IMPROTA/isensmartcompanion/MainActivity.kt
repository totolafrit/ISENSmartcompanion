package fr.isen.IMPROTA.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.VisualTransformation
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
    }

    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        val homeTab = Screen.Home
        val eventTab = Screen.Event
        val historyTab = Screen.History
        val tabBarItems = listOf(homeTab, eventTab, historyTab)

        Scaffold(
            bottomBar = { TabView(tabBarItems, navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = homeTab.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(homeTab.route) {
                    HomeScreen(navController)
                }
                composable(eventTab.route) {
                    EventScreen(navController)
                }
                composable(historyTab.route) {
                    HistoryScreen(navController)
                }
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
                            Screen.Event -> Icon(
                                Icons.Filled.DateRange,
                                contentDescription = screen.name
                            )

                            Screen.History -> Icon(
                                Icons.Filled.History,
                                contentDescription = screen.name
                            )
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
        Column(
            modifier = modifier
                .background(Color.LightGray)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.isenlogo),
                contentDescription = context.getString(R.string.app_name),
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ISEN Smart Companion",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun ChatSection() {
        var userQuery by remember { mutableStateOf("") }
        var displayedText by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (displayedText.isNotEmpty()) {
                Text(text = displayedText)
            }
            ChatInputField(onSend = {
                displayedText = "Vous avez demandé : $it"
                userQuery = it
            })
        }
    }

    @Composable
    fun ChatInputField(onSend: (String) -> Unit) {
        var textState by remember { mutableStateOf(TextFieldValue("")) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                placeholder = { Text("Pose-moi une question...") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(40.dp)),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    onSend(textState.text)
                    textState = TextFieldValue("")
                }),
                visualTransformation = VisualTransformation.None,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray,
                    disabledContainerColor = Color.LightGray,
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
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            )
            {
                Text(
                    text = "Envoyer",
                    color = Color.Black
                )
            }
        }
    }
}
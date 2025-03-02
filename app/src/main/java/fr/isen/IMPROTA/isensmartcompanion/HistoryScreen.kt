package fr.isen.IMPROTA.isensmartcompanion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.isen.IMPROTA.isensmartcompanion.data.AppDatabase
import fr.isen.IMPROTA.isensmartcompanion.data.Chat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val db = AppDatabase.getDatabase(context)
    val chatDao = db.chatDao()

    var chatHistory by remember { mutableStateOf<List<Chat>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Charger les messages dès l'ouverture de l'écran
    LaunchedEffect(Unit) {
        scope.launch {
            chatHistory = chatDao.getAllChats()
            isLoading = false
        }
    }

    // Fonction pour supprimer une conversation spécifique
    fun deleteChatById(chatId: Int) {
        scope.launch {
            chatDao.deleteChat(chatId)
            chatHistory = chatHistory.filter { it.id.toInt() != chatId }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Historique des Conversations",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Bouton pour vider l'historique
        Button(
            onClick = {
                scope.launch {
                    chatDao.deleteAllChats()
                    chatHistory = emptyList()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Vider l'historique", color = MaterialTheme.colorScheme.onError)
        }

        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(chatHistory) { chat ->
                    ChatItem(chat = chat, onDelete = ::deleteChatById)
                }
            }
        }
    }
}

@Composable
fun ChatItem(chat: Chat, onDelete: (Int) -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Affichage de la question
            Text(
                text = "❓ ${chat.question}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // Affichage de la réponse
            Text(
                text = "💬 ${chat.answer}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Affichage de la date
            Text(
                text = "📅 ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(chat.timestamp))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bouton pour supprimer une seule conversation
            Button(
                onClick = { onDelete(chat.id.toInt()) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = "Supprimer", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}

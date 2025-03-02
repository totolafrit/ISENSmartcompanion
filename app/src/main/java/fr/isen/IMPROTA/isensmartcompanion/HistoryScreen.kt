package fr.isen.IMPROTA.isensmartcompanion

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    LaunchedEffect(Unit) {
        scope.launch {
            chatHistory = chatDao.getAllChats()
            isLoading = false
        }
    }

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
            text = "Historique des Chats",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
            ChatList(chatHistory, onDelete = ::deleteChatById)
        }
    }
}



@Composable
fun ChatList(chatHistory: List<Chat>, onDelete: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(chatHistory) { chat ->
            ChatItem(chat = chat, onDelete = onDelete)
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
            Text(
                text = "Q: ${chat.question}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "A: ${chat.answer}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Date: ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(chat.timestamp))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

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

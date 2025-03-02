package fr.isen.IMPROTA.isensmartcompanion

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.View
import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import fr.isen.IMPROTA.isensmartcompanion.data.AppDatabase
import fr.isen.IMPROTA.isensmartcompanion.data.Course
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AgendaScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)
    val courseDao = db.courseDao()

    var selectedDate by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())) }
    var coursesOnSelectedDate by remember { mutableStateOf<List<Course>>(emptyList()) }
    var allCourseDates by remember { mutableStateOf(setOf<String>()) }
    var showAddCourseForm by remember { mutableStateOf(false) }

    // Charger les cours depuis la base de données
    LaunchedEffect(Unit) {
        scope.launch {
            allCourseDates = courseDao.getAllCourseDates().toSet()
            coursesOnSelectedDate = courseDao.getCoursesByDate(selectedDate)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📅 Mon Agenda",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 📅 Affichage du calendrier avec indication des jours contenant un cours
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                factory = { CalendarView(it) },
                update = { calendarView ->
                    calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                        selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                        scope.launch {
                            coursesOnSelectedDate = courseDao.getCoursesByDate(selectedDate)
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 📌 Affichage des cours du jour sélectionné
        if (coursesOnSelectedDate.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                LazyColumn(modifier = Modifier.padding(10.dp)) {
                    items(coursesOnSelectedDate) { course ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Text("📖 ${course.subject}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                Text("⏰ ${course.time}  |  🏛️ ${course.room}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Spacer(modifier = Modifier.height(6.dp))

                                // 🔴 Bouton pour supprimer un cours
                                Button(
                                    onClick = {
                                        scope.launch {
                                            courseDao.deleteCourse(course)
                                            coursesOnSelectedDate = courseDao.getCoursesByDate(selectedDate)
                                            allCourseDates = courseDao.getAllCourseDates().toSet()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Supprimer", color = MaterialTheme.colorScheme.onError)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Aucun cours prévu pour cette date",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 🔘 Bouton "Ajouter un cours"
        Button(
            onClick = { showAddCourseForm = !showAddCourseForm },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(if (showAddCourseForm) "Fermer" else "Ajouter un cours", color = MaterialTheme.colorScheme.onPrimary)
        }

        // ✅ Afficher le formulaire uniquement si showAddCourseForm est activé
        if (showAddCourseForm) {
            Spacer(modifier = Modifier.height(12.dp))

            var subject by remember { mutableStateOf(TextFieldValue("")) }
            var time by remember { mutableStateOf(TextFieldValue("")) }
            var room by remember { mutableStateOf(TextFieldValue("")) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("📌 Ajouter un cours", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("📚 Matière") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("⏰ Heure") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = room,
                            onValueChange = { room = it },
                            label = { Text("🏛️ Salle") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                val newCourse = Course(
                                    date = selectedDate,
                                    time = time.text,
                                    room = room.text,
                                    subject = subject.text
                                )
                                courseDao.insertCourse(newCourse)

                                coursesOnSelectedDate = courseDao.getCoursesByDate(selectedDate)
                                allCourseDates = courseDao.getAllCourseDates().toSet()
                                showAddCourseForm = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ajouter")
                    }
                }
            }
        }
    }
}

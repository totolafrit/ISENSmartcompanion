package fr.isen.IMPROTA.isensmartcompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // Stocke la date du cours (dd/MM/yyyy)
    val time: String, // Heure du cours
    val room: String, // Salle
    val subject: String // Matière
)

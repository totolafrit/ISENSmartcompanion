package fr.isen.IMPROTA.isensmartcompanion.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface CourseDao {
    // ✅ Ajout d'un cours dans la base de données
    @Insert
    suspend fun insertCourse(course: Course)

    // ✅ Récupération des cours en fonction d'une date spécifique
    @Query("SELECT * FROM courses WHERE date = :date")
    suspend fun getCoursesByDate(date: String): List<Course>

    // ✅ Récupération de toutes les dates contenant au moins un cours
    @Query("SELECT DISTINCT date FROM courses")
    suspend fun getAllCourseDates(): List<String>

    // ✅ Suppression d'un cours
    @Delete
    suspend fun deleteCourse(course: Course) // ⚠️ Correction : Ajout de @Delete
}

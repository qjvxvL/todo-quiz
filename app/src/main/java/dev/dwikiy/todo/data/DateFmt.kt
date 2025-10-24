// file: data/DateFmt.kt
package dev.dwikiy.todo.data

import java.util.*
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

data class Todo(
    val id: String = "",
    val title: String = "",
    val dueAt: Date? = null,
    val done: Boolean = false,
    val createdAt: Date = Date()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "title" to title,
        "dueAt" to (dueAt?.let { Timestamp(it) }),
        "done" to done,
        "createdAt" to Timestamp(createdAt)
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Todo {
            val due = (map["dueAt"] as? com.google.firebase.Timestamp)?.toDate()
            val created = (map["createdAt"] as? com.google.firebase.Timestamp)?.toDate() ?: Date()
            return Todo(
                id = id,
                title = map["title"] as? String ?: "",
                dueAt = due,
                done = map["done"] as? Boolean ?: false,
                createdAt = created
            )
        }
    }
}

object DateFmt {
    // Format wajib: dd:MM:yyyy – HH:mm
    private const val PATTERN = "dd:MM:yyyy – HH:mm"

    fun parseOrNull(text: String): Date? =
        try { SimpleDateFormat(PATTERN, Locale.getDefault()).parse(text) } catch (_: Exception) { null }

    fun formatOrDash(date: Date?): String =
        date?.let { SimpleDateFormat(PATTERN, Locale.getDefault()).format(it) } ?: "—"
}
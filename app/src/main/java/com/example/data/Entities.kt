package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String, // e.g., "Kariyer", "Sağlık", "Kişisel Gelişim", "Finans"
    val targetDate: Long,
    val isCompleted: Boolean = false,
    val targetValue: Float = 0f, // e.g. Read 15 books -> targetValue = 15
    val currentValue: Float = 0f  // current progress, e.g. 5 books read -> currentValue = 5
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val goalId: Int? = null, // Optional connection to a Goal
    val title: String,
    val description: String,
    val dueDate: Long,
    val priority: String, // "Yüksek", "Orta", "Düşük"
    val isCompleted: Boolean = false,
    val completedDate: Long? = null
)

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "Sağlık", "Zihin", "Öğrenme", "Genel"
    val frequency: String, // "Günlük", "Haftalık"
    val streak: Int = 0,
    val lastCompletedDate: Long = 0L,
    val completedDates: String = "" // Comma-separated list of date strings (e.g., "2026-05-26,2026-05-25")
)

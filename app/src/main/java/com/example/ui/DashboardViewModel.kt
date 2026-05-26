package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DashboardRepository
import com.example.data.Goal
import com.example.data.Habit
import com.example.data.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DashboardRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DashboardRepository(
            database.goalDao(),
            database.taskDao(),
            database.habitDao()
        )
    }

    // Selected Top/Bottom Tab Navigation (0 = Panel, 1 = Hedefler, 2 = Görevler, 3 = Alışkanlıklar)
    var selectedTab by mutableIntStateOf(0)

    // Room Streams
    val goals: StateFlow<List<Goal>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<Habit>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter properties
    var taskPriorityFilter by mutableStateOf<String?>(null) // null = Hepsi
    var taskCompletedFilter by mutableStateOf<Boolean?>(null) // null = Hepsi
    var goalCategoryFilter by mutableStateOf<String?>(null) // null = Hepsi

    // Goals CRUD
    fun addGoal(title: String, description: String, category: String, targetDate: Long, targetValue: Float = 0f, currentValue: Float = 0f) {
        viewModelScope.launch {
            val newGoal = Goal(
                title = title,
                description = description,
                category = category,
                targetDate = targetDate,
                targetValue = targetValue,
                currentValue = currentValue,
                isCompleted = targetValue > 0f && currentValue >= targetValue
            )
            repository.insertGoal(newGoal)
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }

    fun updateGoalProgress(goal: Goal, newProgress: Float) {
        viewModelScope.launch {
            val updatedProgress = if (goal.targetValue > 0f) {
                newProgress.coerceIn(0f, goal.targetValue)
            } else {
                newProgress.coerceIn(0f, 100f)
            }
            val completed = if (goal.targetValue > 0f) {
                updatedProgress >= goal.targetValue
            } else {
                updatedProgress >= 100f
            }
            repository.updateGoal(goal.copy(currentValue = updatedProgress, isCompleted = completed))
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    // Tasks CRUD
    fun addTask(title: String, description: String, priority: String, dueDate: Long, goalId: Int? = null) {
        viewModelScope.launch {
            val newTask = Task(
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                goalId = goalId,
                isCompleted = false
            )
            repository.insertTask(newTask)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(
                isCompleted = !task.isCompleted,
                completedDate = if (!task.isCompleted) System.currentTimeMillis() else null
            )
            repository.updateTask(updated)
            
            // If linked to a goal and it is a metrics-based goal, maybe trigger updates,
            // or let the user manage goal metrics manually as a flexible workspace!
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Habits CRUD
    fun addHabit(title: String, category: String, frequency: String) {
        viewModelScope.launch {
            val newHabit = Habit(
                title = title,
                category = category,
                frequency = frequency,
                streak = 0,
                completedDates = ""
            )
            repository.insertHabit(newHabit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // Toggles a habit as completed for today!
    fun toggleHabitCompletion(habit: Habit) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = sdf.format(Date())
            
            val datesList = habit.completedDates.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toMutableList()
            
            val isCompletedToday = datesList.contains(todayStr)
            
            if (isCompletedToday) {
                // Uncomplete for today
                datesList.remove(todayStr)
            } else {
                // Complete for today
                datesList.add(todayStr)
            }
            
            // Re-calculate streak live!
            val newStreak = calculateStreak(datesList)
            val updatedDatesString = datesList.distinct().sortedDescending().joinToString(",")
            
            repository.updateHabit(
                habit.copy(
                    completedDates = updatedDatesString,
                    streak = newStreak,
                    lastCompletedDate = if (!isCompletedToday) System.currentTimeMillis() else habit.lastCompletedDate
                )
            )
        }
    }

    // Seed mock data for Turkish demonstration so first launch UI is extremely warm and beautiful!
    fun seedDemoData() {
        viewModelScope.launch {
            // Check if database is empty first
            // We use standard Room flow values which are state flows
            if (goals.value.isEmpty() && tasks.value.isEmpty() && habits.value.isEmpty()) {
                // Create Goals
                val g1 = Goal(
                    title = "Kotlin & Jetpack Compose Öğren",
                    description = "Modern Android geliştirme pratiklerini ve state yönetimlerini pekiştir.",
                    category = "Kariyer",
                    targetDate = System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L, // 30 days
                    targetValue = 10f, // 10 sections
                    currentValue = 4f
                )
                val g2 = Goal(
                    title = "Forma Gir & Yağ Oranını Düşür",
                    description = "Haftalık spor salonu idmanları ve düzenli protein alımı.",
                    category = "Sağlık",
                    targetDate = System.currentTimeMillis() + 60 * 24 * 60 * 60 * 1000L, // 60 days
                    targetValue = 30f, // 30 workouts target
                    currentValue = 12f
                )
                val g3 = Goal(
                    title = "Kitap Okuma Alışkanlığı",
                    description = "Her gün en az 10 sayfa felsefe veya kişisel gelişim kitabı oku.",
                    category = "Kişisel Gelişim",
                    targetDate = System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000L,
                    targetValue = 5f, // 5 books target
                    currentValue = 2f
                )
                
                val g1Id = repository.insertGoal(g1).toInt()
                val g2Id = repository.insertGoal(g2).toInt()
                repository.insertGoal(g3)

                // Create Tasks
                repository.insertTask(Task(
                    goalId = g1Id,
                    title = "Room Database Entegrasyonu Kodla",
                    description = "KSP plugin'i ile lokal veritabanı akışını test et.",
                    dueDate = System.currentTimeMillis(), // Today
                    priority = "Yüksek",
                    isCompleted = true,
                    completedDate = System.currentTimeMillis()
                ))
                repository.insertTask(Task(
                    goalId = g1Id,
                    title = "Material 3 Dashboard Tasarımı",
                    description = "Sıra dışı asimetrik kartlar ve ilerleme barları ekle.",
                    dueDate = System.currentTimeMillis(), // Today
                    priority = "Yüksek",
                    isCompleted = false
                ))
                repository.insertTask(Task(
                    goalId = g2Id,
                    title = "HIIT Kardiyo İdmanı Yap",
                    description = "En az 30 dakika yüksek yoğunluklu kardiyo.",
                    dueDate = System.currentTimeMillis(), // Today
                    priority = "Orta",
                    isCompleted = false
                ))
                repository.insertTask(Task(
                    title = "Haftalık Alışveriş Listesi Hazırla",
                    description = "Yulaf ezmesi, yumurta, brokoli ve kuruyemiş al.",
                    dueDate = System.currentTimeMillis() + 24 * 60 * 60 * 1000L, // Tomorrow
                    priority = "Düşük",
                    isCompleted = false
                ))

                // Create Habits
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = sdf.format(Date())
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val yesterday = sdf.format(cal.time)
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val dayBeforeYesterday = sdf.format(cal.time)

                repository.insertHabit(Habit(
                    title = "Her Gün 2 Litre Su İç",
                    category = "Sağlık",
                    frequency = "Günlük",
                    streak = 3,
                    completedDates = "$today,$yesterday,$dayBeforeYesterday"
                ))
                repository.insertHabit(Habit(
                    title = "Hızlı Kitap Okuma (15 Dk)",
                    category = "Zihin",
                    frequency = "Günlük",
                    streak = 2,
                    completedDates = "$yesterday,$dayBeforeYesterday" // Not done today yet
                ))
                repository.insertHabit(Habit(
                    title = "Erken Uyan (En geç 07:30)",
                    category = "Genel",
                    frequency = "Günlük",
                    streak = 1,
                    completedDates = "$today"
                ))
            }
        }
    }

    // Helper to calculate custom habit streak
    private fun calculateStreak(completedDatesList: List<String>): Int {
        if (completedDatesList.isEmpty()) return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sortedUnique = completedDatesList.filter { it.isNotBlank() }.distinct().sortedDescending()
        if (sortedUnique.isEmpty()) return 0
        
        val todayStr = sdf.format(Date())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = sdf.format(calendar.time)
        
        var currentStreak = 0
        
        if (sortedUnique.contains(todayStr)) {
            currentStreak = 1
            // start checking backwards from yesterday
            val targetCal = Calendar.getInstance()
            while (true) {
                targetCal.add(Calendar.DAY_OF_YEAR, -1)
                val dayStr = sdf.format(targetCal.time)
                if (sortedUnique.contains(dayStr)) {
                    currentStreak++
                } else {
                    break
                }
            }
        } else if (sortedUnique.contains(yesterdayStr)) {
            currentStreak = 1
            val targetCal = Calendar.getInstance()
            targetCal.add(Calendar.DAY_OF_YEAR, -1) // check day before yesterday
            while (true) {
                targetCal.add(Calendar.DAY_OF_YEAR, -1)
                val dayStr = sdf.format(targetCal.time)
                if (sortedUnique.contains(dayStr)) {
                    currentStreak++
                } else {
                    break
                }
            }
        } else {
            currentStreak = 0
        }
        return currentStreak
    }

    // Export/Import JSON mock sync representation
    fun formatDataAsJsonString(): String {
        val gList = goals.value
        val tList = tasks.value
        val hList = habits.value
        
        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("  \"goals\": [\n")
        gList.forEachIndexed { i, g ->
            sb.append("    {\"title\":\"${g.title}\", \"description\":\"${g.description}\", \"category\":\"${g.category}\", \"currentProgress\":${g.currentValue}, \"targetValue\":${g.targetValue}, \"isCompleted\":${g.isCompleted}}")
            if (i < gList.lastIndex) sb.append(",\n") else sb.append("\n")
        }
        sb.append("  ],\n")
        sb.append("  \"tasks\": [\n")
        tList.forEachIndexed { i, t ->
            sb.append("    {\"title\":\"${t.title}\", \"description\":\"${t.description}\", \"priority\":\"${t.priority}\", \"isCompleted\":${t.isCompleted}}")
            if (i < tList.lastIndex) sb.append(",\n") else sb.append("\n")
        }
        sb.append("  ],\n")
        sb.append("  \"habits\": [\n")
        hList.forEachIndexed { i, h ->
            sb.append("    {\"title\":\"${h.title}\", \"category\":\"${h.category}\", \"streak\":${h.streak}, \"completedDates\":\"${h.completedDates}\"}")
            if (i < hList.lastIndex) sb.append(",\n") else sb.append("\n")
        }
        sb.append("  ]\n")
        sb.append("}")
        return sb.toString()
    }
}

class DashboardViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

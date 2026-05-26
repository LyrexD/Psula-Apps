package com.example.data

import kotlinx.coroutines.flow.Flow

class DashboardRepository(
    private val goalDao: GoalDao,
    private val taskDao: TaskDao,
    private val habitDao: HabitDao
) {
    // Goals
    val allGoals: Flow<List<Goal>> = goalDao.getAllGoals()
    
    suspend fun getGoalById(id: Int): Goal? = goalDao.getGoalById(id)
    
    suspend fun insertGoal(goal: Goal): Long = goalDao.insertGoal(goal)
    
    suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)
    
    suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)
    
    suspend fun clearAllGoals() = goalDao.deleteAllGoals()

    // Tasks
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    
    fun getTasksForGoal(goalId: Int): Flow<List<Task>> = taskDao.getTasksForGoal(goalId)
    
    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)
    
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    
    suspend fun clearAllTasks() = taskDao.deleteAllTasks()

    // Habits
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    
    suspend fun insertHabit(habit: Habit): Long = habitDao.insertHabit(habit)
    
    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)
    
    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)
    
    suspend fun clearAllHabits() = habitDao.deleteAllHabits()
}

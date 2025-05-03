package com.example.taskmanager.service;

import com.example.taskmanager.entity.Task;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {
    List<Task> getActiveTasks(Long userId);
    List<Task> getHighPriorityTasks(Long userId);
    List<Task> getMediumLowPriorityTasks(Long userId);
    List<Task> getCompletedTasks(Long userId);
    List<Task> getExpiredTasks(Long userId);
    Task createTask(Task task);
    void markTaskAsCompleted(Long taskId);
    void markTaskAsUncompleted(Long taskId);
    void deleteTask(Long taskId);
    String getTaskDescription(Long taskId);
    void updateTaskDescription(Long taskId, String description);
    void updateDescription(Long taskId, String description);
    void updateDueDate(Long taskId, LocalDateTime dueDate);
} 
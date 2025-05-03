package com.example.taskmanager.service.impl;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    @Override
    public void updateDescription(Long taskId, String description) {
        taskMapper.updateDescription(taskId, description);
    }

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public List<Task> getActiveTasks(Long userId) {
        return taskMapper.findActiveTasksByUserId(userId);
    }

    @Override
    public List<Task> getHighPriorityTasks(Long userId) {
        return taskMapper.findHighPriorityTasks(userId);
    }

    @Override
    public List<Task> getMediumLowPriorityTasks(Long userId) {
        return taskMapper.findMediumLowPriorityTasks(userId);
    }

    @Override
    public List<Task> getCompletedTasks(Long userId) {
        return taskMapper.findCompletedTasks(userId);
    }

    @Override
    public List<Task> getExpiredTasks(Long userId) {
        return taskMapper.findExpiredTasks(userId);
    }

    @Override
    public Task createTask(Task task) {
        taskMapper.insert(task);
        return task;
    }

    @Override
    public void markTaskAsCompleted(Long taskId) {
        taskMapper.markAsCompleted(taskId);
    }

    @Override
    public void markTaskAsUncompleted(Long taskId) {
        taskMapper.markAsUncompleted(taskId);
    }

    @Override
    public void deleteTask(Long taskId) {
        taskMapper.delete(taskId);
    }

    @Override
    public String getTaskDescription(Long taskId) {
        return taskMapper.getDescription(taskId);
    }

    @Override
    public void updateTaskDescription(Long taskId, String description) {
        taskMapper.updateDescription(taskId, description);
    }

    @Override
    public void updateDueDate(Long taskId, LocalDateTime dueDate) {
        taskMapper.updateDueDate(taskId, dueDate);
    }
} 
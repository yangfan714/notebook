package com.example.taskmanager.scheduler;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TaskCleanupScheduler {

    @Autowired
    private TaskMapper taskMapper;

    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    public void cleanupExpiredTasks() {
        // 获取所有过期的未完成任务
        List<Task> expiredTasks = taskMapper.findAllExpiredTasks();
        
        // 将过期任务标记为已完成
        for (Task task : expiredTasks) {
            taskMapper.markAsCompleted(task.getId());
        }
    }
} 
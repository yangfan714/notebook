package com.example.taskmanager.mapper;

import com.example.taskmanager.entity.Task;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskMapper {
    @Select("SELECT id, user_id as userId, title, description, due_date as dueDate, priority, completed, create_time as createTime, update_time as updateTime " +
            "FROM task WHERE user_id = #{userId} ORDER BY completed ASC, priority DESC, due_date ASC")
    List<Task> findActiveTasksByUserId(Long userId);

    @Select("SELECT id, user_id as userId, title, description, due_date as dueDate, priority, completed, create_time as createTime, update_time as updateTime " +
            "FROM task WHERE user_id = #{userId} AND completed = false AND priority = 3 ORDER BY due_date ASC")
    List<Task> findHighPriorityTasks(Long userId);

    @Select("SELECT id, user_id as userId, title, description, due_date as dueDate, priority, completed, create_time as createTime, update_time as updateTime " +
            "FROM task WHERE user_id = #{userId} AND completed = false AND priority IN (1, 2) ORDER BY priority DESC, due_date ASC")
    List<Task> findMediumLowPriorityTasks(Long userId);

    @Select("SELECT id, user_id as userId, title, description, due_date as dueDate, priority, completed, create_time as createTime, update_time as updateTime " +
            "FROM task WHERE user_id = #{userId} AND completed = true ORDER BY due_date DESC")
    List<Task> findCompletedTasks(Long userId);

    @Select("SELECT id, user_id as userId, title, description, due_date as dueDate, priority, completed, create_time as createTime, update_time as updateTime " +
            "FROM task WHERE user_id = #{userId} AND completed = false AND due_date < CURDATE() ORDER BY due_date ASC")
    List<Task> findExpiredTasks(Long userId);

    @Select("SELECT id, user_id as userId, title, description, due_date as dueDate, priority, completed, create_time as createTime, update_time as updateTime " +
            "FROM task WHERE completed = false AND due_date < CURDATE() ORDER BY due_date ASC")
    List<Task> findAllExpiredTasks();

    @Insert("INSERT INTO task (user_id, title, description, due_date, priority, completed) " +
            "VALUES (#{userId}, #{title}, #{description}, #{dueDate}, #{priority}, #{completed})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Task task);

    @Update("UPDATE task SET completed = true WHERE id = #{taskId}")
    void markAsCompleted(Long taskId);

    @Update("UPDATE task SET completed = false WHERE id = #{taskId}")
    void markAsUncompleted(Long taskId);

    @Delete("DELETE FROM task WHERE id = #{taskId}")
    void delete(Long taskId);

    @Select("SELECT description FROM task WHERE id = #{taskId}")
    String getDescription(Long taskId);

    @Update("UPDATE task SET description = #{description} WHERE id = #{taskId}")
    void updateDescription(@Param("taskId") Long taskId, @Param("description") String description);

    @Update("UPDATE task SET due_date = #{dueDate} WHERE id = #{taskId}")
    void updateDueDate(@Param("taskId") Long taskId, @Param("dueDate") LocalDateTime dueDate);
} 
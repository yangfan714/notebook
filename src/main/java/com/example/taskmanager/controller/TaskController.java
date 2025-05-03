package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/task")
public class TaskController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String listTasks(Model model, 
                          Authentication authentication,
                          @RequestParam(defaultValue = "0") Integer priority,
                          @RequestParam(defaultValue = "1") Integer page) {
        try {
            // 从认证信息中获取当前用户
            org.springframework.security.core.userdetails.User securityUser = 
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            String username = securityUser.getUsername();
            
            // 通过用户名获取用户ID
            com.example.taskmanager.entity.User user = userService.findByUsername(username);
            if (user == null) {
                model.addAttribute("error", "用户不存在");
                return "error";
            }
            
            List<Task> allTasks = taskService.getActiveTasks(user.getId());
            
            // 计算每个任务距离截止日期的倒计时并更新优先级
            LocalDateTime now = LocalDateTime.now();
            allTasks.forEach(task -> {
                LocalDateTime dueDate = task.getDueDate();
                long daysUntilDue = ChronoUnit.DAYS.between(now.toLocalDate(), dueDate.toLocalDate());
                
                // 如果倒计时小于3天，自动升级为高优先级
                if (daysUntilDue <= 3 && daysUntilDue >= 0) {
                    task.setPriority(3);
                }
            });

            // 按优先级过滤
            List<Task> filteredTasks = allTasks;
            if (priority > 0) {
                filteredTasks = allTasks.stream()
                    .filter(task -> task.getPriority() == priority)
                    .collect(Collectors.toList());
            }
            
            // 对任务进行排序
            filteredTasks.sort((t1, t2) -> {
                // 首先按优先级排序（数字越大优先级越高）
                int priorityCompare = Integer.compare(t2.getPriority(), t1.getPriority());
                if (priorityCompare != 0) {
                    return priorityCompare;
                }
                
                // 如果优先级相同，则按截止日期排序（越早的越靠前）
                return t1.getDueDate().compareTo(t2.getDueDate());
            });

            // 计算分页信息
            int totalTasks = filteredTasks.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalTasks / PAGE_SIZE));
            
            // 确保页码在有效范围内
            page = Math.max(1, Math.min(page, totalPages));
            
            // 计算起始和结束索引
            int startIndex = (page - 1) * PAGE_SIZE;
            int endIndex = Math.min(startIndex + PAGE_SIZE, totalTasks);
            
            // 处理空列表的情况
            List<Task> pagedTasks;
            if (totalTasks == 0) {
                pagedTasks = new ArrayList<>();
            } else {
                // 确保起始索引不超过列表大小
                startIndex = Math.min(startIndex, totalTasks - 1);
                pagedTasks = filteredTasks.subList(startIndex, endIndex);
            }
            
            model.addAttribute("tasks", pagedTasks);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("selectedPriority", priority);
            model.addAttribute("now", now);
            return "task-list";
        } catch (Exception e) {
            model.addAttribute("error", "获取任务列表时发生错误: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("task", new Task());
        return "task-form";
    }

    @PostMapping("/add")
    public String addTask(@RequestParam("title") String title,
                         @RequestParam("description") String description,
                         @RequestParam("dueDate") String dueDate,
                         @RequestParam("dueTime") String dueTime,
                         @RequestParam("priority") Integer priority,
                         Authentication authentication) {
        try {
            // 从认证信息中获取当前用户
            org.springframework.security.core.userdetails.User securityUser = 
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            String username = securityUser.getUsername();
            
            // 通过用户名获取用户ID
            com.example.taskmanager.entity.User user = userService.findByUsername(username);
            
            // 创建新任务
            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            task.setUserId(user.getId());
            task.setPriority(priority);
            task.setCompleted(false);
            
            // 设置截止时间
            LocalDate date = LocalDate.parse(dueDate);
            LocalTime time = LocalTime.parse(dueTime);
            task.setDueDate(LocalDateTime.of(date, time));
            
            taskService.createTask(task);
            return "redirect:/task/list";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/task/add?error=" + e.getMessage();
        }
    }

    @PostMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id) {
        taskService.markTaskAsCompleted(id);
        return "redirect:/task/list";
    }

    @PostMapping("/uncomplete/{id}")
    public String uncompleteTask(@PathVariable Long id) {
        taskService.markTaskAsUncompleted(id);
        return "redirect:/task/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/task/list";
    }

    @GetMapping("/{id}/description")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getTaskDescription(@PathVariable Long id) {
        try {
            String description = taskService.getTaskDescription(id);
            Map<String, String> response = new HashMap<>();
            response.put("description", description);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/description")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateTaskDescription(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String description = request.get("description");
            taskService.updateTaskDescription(id, description);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/update-description")
    @ResponseBody
    public ResponseEntity<?> updateDescription(@RequestParam Long taskId, @RequestParam String description) {
        try {
            taskService.updateDescription(taskId, description);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/update-due-date")
    @ResponseBody
    public ResponseEntity<?> updateDueDate(@RequestParam Long taskId, 
                                         @RequestParam String dueDate,
                                         @RequestParam String dueTime) {
        try {
            LocalDate date = LocalDate.parse(dueDate);
            LocalTime time = LocalTime.parse(dueTime);
            LocalDateTime dueDateTime = LocalDateTime.of(date, time);
            taskService.updateDueDate(taskId, dueDateTime);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/active")
    public List<Task> getActiveTasks(@RequestParam Long userId) {
        return taskService.getActiveTasks(userId);
    }

    @GetMapping("/high-priority")
    public List<Task> getHighPriorityTasks(@RequestParam Long userId) {
        return taskService.getHighPriorityTasks(userId);
    }

    @GetMapping("/medium-low-priority")
    public List<Task> getMediumLowPriorityTasks(@RequestParam Long userId) {
        return taskService.getMediumLowPriorityTasks(userId);
    }

    @GetMapping("/completed")
    public List<Task> getCompletedTasks(@RequestParam Long userId) {
        return taskService.getCompletedTasks(userId);
    }

    @GetMapping("/expired")
    public List<Task> getExpiredTasks(@RequestParam Long userId) {
        return taskService.getExpiredTasks(userId);
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }
} 
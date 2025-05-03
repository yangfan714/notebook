package com.example.taskmanager.service;

import com.example.taskmanager.entity.User;

public interface UserService {
    User register(User user);
    User login(String username, String password);
    User updateUser(User user);
    User findByUsername(String username);
} 
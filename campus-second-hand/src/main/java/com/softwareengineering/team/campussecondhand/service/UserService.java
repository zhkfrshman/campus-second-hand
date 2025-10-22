package com.softwareengineering.team.campussecondhand.service;

import com.softwareengineering.team.campussecondhand.entity.User;

public interface UserService {
    User register(String phone, String rawPassword, String username);
    User findByPhone(String phone);
    User findById(Long id);
    User updateUser(User user);
}

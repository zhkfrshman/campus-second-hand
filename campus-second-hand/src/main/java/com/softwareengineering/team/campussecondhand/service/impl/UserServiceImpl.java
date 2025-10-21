package com.softwareengineering.team.campussecondhand.service.impl;

import com.softwareengineering.team.campussecondhand.entity.User;
import com.softwareengineering.team.campussecondhand.entity.UserPassword;
import com.softwareengineering.team.campussecondhand.repository.UserPasswordRepository;
import com.softwareengineering.team.campussecondhand.repository.UserRepository;
import com.softwareengineering.team.campussecondhand.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPasswordRepository userPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserPasswordRepository userPasswordRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userPasswordRepository = userPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(String phone, String rawPassword, String username) {
        if (userRepository.findByPhone(phone).isPresent()) {
            throw new RuntimeException("手机号已被注册");
        }
        User u = new User();
        u.setPhone(phone);
        u.setUsername(username == null ? phone : username);
        userRepository.save(u);

        UserPassword up = new UserPassword();
        up.setUid(u.getId());
        up.setPasswordHash(passwordEncoder.encode(rawPassword));
        userPasswordRepository.save(up);
        return u;
    }

    @Override
    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone).orElse(null);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}

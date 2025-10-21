package com.softwareengineering.team.campussecondhand.security;

import com.softwareengineering.team.campussecondhand.entity.User;
import com.softwareengineering.team.campussecondhand.entity.UserPassword;
import com.softwareengineering.team.campussecondhand.repository.UserPasswordRepository;
import com.softwareengineering.team.campussecondhand.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserPasswordRepository userPasswordRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    UserPasswordRepository userPasswordRepository) {
        this.userRepository = userRepository;
        this.userPasswordRepository = userPasswordRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username 我们用手机号
        User u = userRepository.findByPhone(username).orElseThrow(() -> new UsernameNotFoundException("手机号未注册"));
        UserPassword up = userPasswordRepository.findByUid(u.getId())
                .orElseThrow(() -> new UsernameNotFoundException("密码未设置"));
        // role 简化为 ROLE_USER
        return org.springframework.security.core.userdetails.User.withUsername(u.getPhone())
                .password(up.getPasswordHash())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false).accountLocked(false).credentialsExpired(false).disabled(false)
                .build();
    }
}

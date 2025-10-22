package com.softwareengineering.team.campussecondhand.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 只保留这一个过滤器链Bean，删除其他filterChain方法
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // 简化开发，生产环境请考虑启用CSRF保护
            .authorizeHttpRequests(authz -> authz
                // 允许静态资源访问
                .requestMatchers("/css/**", "/js/**", "/images/**", "/files/**", "/uploads/**").permitAll()
                // 允许公开页面
                .requestMatchers("/", "/index", "/register", "/login", "/logout-success").permitAll()
                .requestMatchers("/product/list", "/product/{id}").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/product/list")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/logout-success")
                .permitAll()
            );

        return http.build();
    }

}
package com.softwareengineering.team.campussecondhand.config;

import com.softwareengineering.team.campussecondhand.security.CustomAuthenticationProvider;
import com.softwareengineering.team.campussecondhand.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 注册自定义认证逻辑
     */
    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        return new CustomAuthenticationProvider(userDetailsService, passwordEncoder);
    }

    /**
     * Spring Security 权限规则配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CustomAuthenticationProvider customAuthenticationProvider) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        // ✅ 以下接口允许匿名访问
                        .requestMatchers(
                                "/", "/index",                   // 首页
                                "/login", "/register",           // 登录注册
                                "/product/list", "/product/*",   // 浏览商品
                                "/static/**", "/uploads/**",     // 静态资源
                                "/api/**"
                        ).permitAll()
                        // ✅ 其他接口（如发布、购物车）需要登录
                        .anyRequest().authenticated()
                )
                // ✅ 表单登录
                .formLogin(form -> form
                        .loginPage("/login")                       // 自定义登录页
                        .loginProcessingUrl("/login")              // 登录表单提交路径
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)              // 登录成功后跳转首页
                        .failureUrl("/login?error=true")           // 登录失败跳转回登录页并带参数
                        .permitAll()
                )
                // ✅ 登出
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                // ✅ 关闭CSRF（开发阶段）
                .csrf(csrf -> csrf.disable());

        // 注册我们自定义的登录认证逻辑
        http.authenticationProvider(customAuthenticationProvider);

        return http.build();
    }
}

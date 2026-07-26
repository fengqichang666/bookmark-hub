package com.bookmarkhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordEncoder 单独成一个配置类，不放在 SecurityConfig 里。
 *
 * <p>SecurityConfig 依赖 JwtAuthenticationFilter，后者又依赖 AuthService，
 * 若把该 Bean 放进 SecurityConfig，AuthServiceImpl 注入它就会形成循环依赖。
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

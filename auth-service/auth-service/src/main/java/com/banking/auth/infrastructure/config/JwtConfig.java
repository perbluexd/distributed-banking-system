package com.banking.auth.infrastructure.config;

import com.banking.auth.infrastructure.security.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration  
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
}

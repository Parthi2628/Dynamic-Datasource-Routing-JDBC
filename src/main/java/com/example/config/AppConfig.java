package com.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"aspect", "dao", "service", "com.example.config"})
public class AppConfig {}

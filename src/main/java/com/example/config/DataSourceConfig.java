package com.example.config;

import com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import routing.DataSourceType;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Value("${spring.datasource.write.url}")
    private String writeUrl;

    @Value("${spring.datasource.read.url}")
    private String readUrl;

    @Value("${spring.datasource.username}")
    private String secretName; // AWS secret id per aws-secretsmanager-jdbc

    private DataSource build(String url) {
        SimpleDriverDataSource ds = new SimpleDriverDataSource();
        ds.setDriverClass(AWSSecretsManagerPostgreSQLDriver.class);
        ds.setUrl(url);
        ds.setUsername(secretName);
        return ds;
    }

    @Bean
    public DataSource writeDataSource() { return build(writeUrl); }

    @Bean
    public DataSource readDataSource() { return build(readUrl); }

    @Primary
    @Bean
    public DataSource routingDataSource() {
        RoutingDataSource routing = new RoutingDataSource();
        Map<Object, Object> targets = new HashMap<>();
        targets.put(DataSourceType.WRITE, writeDataSource());
        targets.put(DataSourceType.READ, readDataSource());
        routing.setTargetDataSources(targets);
        routing.setDefaultTargetDataSource(writeDataSource());
        return routing;
    }
}

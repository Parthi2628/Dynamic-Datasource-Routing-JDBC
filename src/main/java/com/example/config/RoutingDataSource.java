package com.example.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import routing.DataSourceContextHolder;

public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.get();
    }
}
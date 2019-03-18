package com.o2o.action.server;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DBConfigure {
    @ConfigurationProperties(prefix = "spring.datasource1")
    @Bean
    @Primary
    public DataSource dataSource1(){
        return DataSourceBuilder
                .create()
                .build();
    }
}


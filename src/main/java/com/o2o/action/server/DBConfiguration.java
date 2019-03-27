package com.o2o.action.server;


import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
/*
@Configuration
public class DBConfiguration {
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.no1")
    public DataSourceProperties dataSourceno1Prop() {
        return new DataSourceProperties();
    }

    @ConfigurationProperties(prefix = "spring.datasource.no1.configuration")
    @Bean
    @Primary
    public DataSource dataSourceNo1(){
        return dataSourceno1Prop().initializeDataSourceBuilder().build();
    }
}*/

@Configuration
public class DBConfiguration {
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource1")
    public DataSourceProperties dataSource1Prop() {
        return new DataSourceProperties();
    }

    @ConfigurationProperties(prefix = "spring.datasource1.configuration")
    @Bean
    @Primary
    public DataSource dataSource1(){
        return dataSource1Prop().initializeDataSourceBuilder().build();
    }
}
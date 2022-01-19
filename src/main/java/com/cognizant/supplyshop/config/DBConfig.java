package com.cognizant.supplyshop.config;

import com.mysql.jdbc.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DBConfig {
    @Bean
    public DataSource dataSource() throws SQLException {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setUrl("jdbc:mysql://database-1-instance-1.cfqctdytya09.us-east-2.rds.amazonaws.com:3306/supplyshop?useSSL=false&allowPublicKeyRetrieval=true");
        dataSource.setUsername("admin");
        dataSource.setPassword("Be.Cognizant2022!");
        dataSource.setDriver(new Driver());
        return dataSource;
    }
}

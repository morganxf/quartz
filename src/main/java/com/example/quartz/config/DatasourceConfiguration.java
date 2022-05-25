package com.example.quartz.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DatasourceConfiguration {

    @Bean("dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DataSource dataSource() {
        DataSource dataSource = new DruidDataSource();
//        checkDataSource(dataSource);
        return dataSource;
    }

    @Bean("sqlSessionFactoryBean")
    public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("dataSource") DataSource dataSource) {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        return sessionFactoryBean;
    }

    @Bean("dataSourceTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("dataSource2")
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    public DataSource dataSource2() {
        DataSource dataSource = new DruidDataSource();
//        checkDataSource(dataSource);
        return dataSource;
    }

    @Bean("SqlSessionFactoryBean2")
    public SqlSessionFactoryBean sqlSessionFactoryBean2(@Qualifier("dataSource2") DataSource dataSource) {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        return sessionFactoryBean;
    }

    @Bean("dataSourceTransactionManager2")
    public DataSourceTransactionManager transactionManager2(@Qualifier("dataSource2") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    public static void checkDataSource(DataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select database() as name");
            resultSet.next();
            System.out.println(resultSet.getString("name"));
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}

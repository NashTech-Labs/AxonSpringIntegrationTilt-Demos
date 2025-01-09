/*
 * Copyright 2024 H-E-B.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of H-E-B.
 */
package org.nashtech.reception.preauth.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuration class for setting up the projection data source.
 */
@Configuration
@Generated
public class ProjectionDataSourceConfiguration {

    /**
     * Creates a DataSourceProperties bean to hold properties for the projection data source.
     *
     * @return DataSourceProperties object containing the configuration properties.
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.read-side-projection-store")
    public HikariConfig projectionHikariConfig() {
        return new HikariConfig();
    }

    /**
     * Configures a HikariDataSource bean for the projection.
     *
     * @param projectionHikariConfig the DataSourceProperties for configuring the data source.
     * @return HikariDataSource instance configured with IAM properties.
     */
    @Bean
    public HikariDataSource projectionDataSource(HikariConfig projectionHikariConfig) {
        return new HikariDataSource(projectionHikariConfig);
    }

    /**
     * Configures a JdbcTemplate bean using the projection data source.
     *
     * @param projectionDataSource the data source to use for the JdbcTemplate.
     * @return JdbcTemplate instance configured for the projection data source.
     */
    @Bean
    public JdbcTemplate jdbcTemplate(HikariDataSource projectionDataSource) {
        return new JdbcTemplate(projectionDataSource);
    }

    /**
     * Configures an EntityManagerFactory bean for managing JPA entities in the projection context.
     *
     * @param projectionDataSource the data source to use for the EntityManagerFactory.
     * @return EntityManagerFactory instance configured for JPA entities.
     */
    @Bean
    EntityManagerFactory entityManagerFactory(final HikariDataSource projectionDataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(projectionDataSource);
        factoryBean.setPackagesToScan("org.nashtech.reception.preauth.entity");
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factoryBean.afterPropertiesSet();
        return factoryBean.getNativeEntityManagerFactory();
    }

    /**
     * Configures a PlatformTransactionManager bean for managing transactions in the projection context.
     *
     * @param entityManagerFactory the EntityManagerFactory to associate with the transaction manager.
     * @return PlatformTransactionManager instance for managing JPA transactions.
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager dbTransactionManager(final EntityManagerFactory entityManagerFactory) {

        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
